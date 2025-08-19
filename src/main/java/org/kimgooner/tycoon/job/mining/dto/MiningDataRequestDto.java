package org.kimgooner.tycoon.job.mining.dto;

import java.util.Map;

public record MiningDataRequestDto(
        int level,
        Map<Integer, Integer> heartLevels
) {
}
