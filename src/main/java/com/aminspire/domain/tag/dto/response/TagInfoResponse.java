package com.aminspire.domain.tag.dto.response;

import com.aminspire.domain.tag.domain.Tag;

import java.util.List;

public record TagInfoResponse(List<Tag> tagList) {
}
