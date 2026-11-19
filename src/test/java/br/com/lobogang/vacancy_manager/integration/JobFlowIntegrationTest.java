package br.com.lobogang.vacancy_manager.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Testes de integração: sobem a aplicação inteira (filtros de segurança, controllers, JPA)
// com banco H2 em memória e fazem requisições HTTP simuladas pelo MockMvc.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobFlowIntegrationTest {

  private static final String PASSWORD = "senha123456";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  // Cada teste usa nomes únicos, assim os testes não dependem da ordem nem de limpar o banco
  private String unique(String prefix) {
    return prefix + UUID.randomUUID().toString().substring(0, 8);
  }

  private String json(Object body) throws Exception {
    return mapper.writeValueAsString(body);
  }

  private JsonNode postJson(String url, Object body, String token, int expectedStatus) throws Exception {
    var request = post(url).contentType(MediaType.APPLICATION_JSON).content(json(body));
    if (token != null) {
      request.header("Authorization", "Bearer " + token);
    }
    var response = mvc.perform(request).andExpect(status().is(expectedStatus)).andReturn().getResponse();
    var content = response.getContentAsString();
    return content.isEmpty() ? null : mapper.readTree(content);
  }

  private String signUpAndLogin(String type) throws Exception {
    var username = unique(type);
    postJson("/" + type + "/", Map.of(
        "username", username,
        "email", username + "@example.test",
        "password", PASSWORD,
        "name", "Test " + type), null, 201);
    return postJson("/" + type + "/auth", Map.of("username", username, "password", PASSWORD), null, 200)
        .get("access_token").asText();
  }

  @Test
  @DisplayName("Company posts a job, candidate finds it and applies, company sees the candidate")
  void fullApplicationFlow() throws Exception {
    var companyToken = signUpAndLogin("company");
    var keyword = unique("kotlin");
    var job = postJson("/company/job",
        Map.of("description", "Backend developer " + keyword, "benefits", "Remote", "level", "JUNIOR"),
        companyToken, 201);
    var jobId = job.get("id").asText();

    mvc.perform(get("/company/job").header("Authorization", "Bearer " + companyToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(jobId));

    var candidateToken = signUpAndLogin("candidate");
    mvc.perform(get("/candidate/job").param("filter", keyword.toUpperCase())
        .header("Authorization", "Bearer " + candidateToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].companyName").value("Test company"));

    postJson("/candidate/job/apply", Map.of("jobId", jobId), candidateToken, 201);
    postJson("/candidate/job/apply", Map.of("jobId", jobId), candidateToken, 409);

    mvc.perform(get("/company/job/" + jobId + "/applications").header("Authorization", "Bearer " + companyToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Test candidate"));

    // Outra empresa não enxerga os candidatos dessa vaga
    var otherCompanyToken = signUpAndLogin("company");
    mvc.perform(get("/company/job/" + jobId + "/applications").header("Authorization", "Bearer " + otherCompanyToken))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Sign-up response never exposes the password hash")
  void signUpDoesNotExposePassword() throws Exception {
    var username = unique("company");
    var body = postJson("/company/", Map.of(
        "username", username, "email", username + "@example.test", "password", PASSWORD), null, 201);
    assertThat(body.has("password")).isFalse();

    var duplicated = postJson("/company/", Map.of(
        "username", username, "email", username + "@example.test", "password", PASSWORD), null, 409);
    assertThat(duplicated.get("message").asText()).isEqualTo("User already exists");
  }

  @Test
  @DisplayName("Sign-up ignores a client-supplied id, so it cannot overwrite an existing account")
  void signUpCannotOverwriteExistingAccount() throws Exception {
    var victim = unique("company");
    var created = postJson("/company/", Map.of(
        "username", victim, "email", victim + "@example.test", "password", PASSWORD), null, 201);
    var victimId = created.get("id").asText();

    var attacker = unique("company");
    var attempt = postJson("/company/", Map.of(
        "id", victimId, "username", attacker, "email", attacker + "@example.test", "password", "attacker-pass"),
        null, 201);

    assertThat(attempt.get("id").asText()).isNotEqualTo(victimId);
    // A vítima continua logando com a senha original
    postJson("/company/auth", Map.of("username", victim, "password", PASSWORD), null, 200);
  }

  @Test
  @DisplayName("An expired or foreign token in the header does not block the public login")
  void staleTokenDoesNotBlockLogin() throws Exception {
    var username = unique("company");
    postJson("/company/", Map.of(
        "username", username, "email", username + "@example.test", "password", PASSWORD), null, 201);

    mvc.perform(post("/company/auth").header("Authorization", "Bearer not-a-valid-token")
        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("username", username, "password", PASSWORD))))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Each route only accepts the token of its own user type")
  void tokensAreNotInterchangeable() throws Exception {
    var candidateToken = signUpAndLogin("candidate");
    var companyToken = signUpAndLogin("company");

    mvc.perform(get("/company/job")).andExpect(status().isUnauthorized());
    mvc.perform(get("/company/job").header("Authorization", "Bearer " + candidateToken))
        .andExpect(status().isUnauthorized());
    mvc.perform(get("/candidate/job").header("Authorization", "Bearer " + companyToken))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Invalid input returns 400 with the field errors, and a wrong password returns 401")
  void validationAndBadCredentials() throws Exception {
    var companyToken = signUpAndLogin("company");

    mvc.perform(post("/company/job").header("Authorization", "Bearer " + companyToken)
        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("description", "No level"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0].field").value("level"));

    var error = postJson("/company/auth", Map.of("username", "nobody", "password", "wrong-password"), null, 401);
    assertThat(error).isNotNull();
  }
}
