package org.kimgooner.tycoon.job.mining.dto;

public record MiningResultDto(
        int result_amount,
        int target,
        int grade,
        int exp
) {
}
