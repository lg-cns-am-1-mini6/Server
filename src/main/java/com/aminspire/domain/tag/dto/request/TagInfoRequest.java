package com.aminspire.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagInfoRequest(@NotNull Long teamId, @NotBlank String tagName){
}
