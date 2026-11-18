package org.example.balance.dtos.cbr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CbrDailyResponse(
        @JsonProperty("Valute") Map<String, CbrValuteDto> valute
) {
}
