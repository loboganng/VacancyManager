package br.com.lobogang.vacancy_manager.modules.candidates.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ApplyJobRequestDTO(@NotNull(message = "The field [jobId] is mandatory") UUID jobId) {
}
