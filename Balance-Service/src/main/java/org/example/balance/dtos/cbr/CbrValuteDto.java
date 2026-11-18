package org.example.balance.dtos.cbr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CbrValuteDto(
        @JsonProperty("CharCode") String charCode,
        @JsonProperty("Nominal") Integer nominal,
        @JsonProperty("Value") BigDecimal value
) {
}
