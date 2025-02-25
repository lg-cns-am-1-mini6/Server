package com.aminspire.domain.tag.service;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.dto.request.TagCreateRequest;
import com.aminspire.domain.tag.dto.request.TagInfoRequest;
import com.aminspire.domain.tag.dto.response.TagInfoResponse;
import com.aminspire.domain.user.domain.user.User;

import java.util.List;

public interface TagService {
    void addTag(TagCreateRequest request, Long userId);

    void addTagDirectByUser(TagCreateRequest request, User searcher);

    TagInfoResponse getTags(TagInfoRequest request);
    List<Tag> getUserPreferTags(User user);

    void deleteTag(Long tagId);


}
