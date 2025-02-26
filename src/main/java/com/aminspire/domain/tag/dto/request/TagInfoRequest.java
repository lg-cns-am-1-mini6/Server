package com.aminspire.domain.tag.dto.request;

import com.aminspire.domain.user.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagInfoRequest(@NotNull User searcher) {}