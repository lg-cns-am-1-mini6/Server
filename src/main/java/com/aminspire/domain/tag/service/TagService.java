package com.aminspire.domain.tag.service;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.dto.request.TagCreateRequest;
import com.aminspire.domain.tag.dto.request.TagInfoRequest;
import com.aminspire.domain.tag.dto.response.TagInfoResponse;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.global.security.AuthDetails;

import java.util.List;

public interface TagService {
    void addTagByUser(TagCreateRequest request, AuthDetails authDetails);

    void addTagByOpenAi(TagCreateRequest request, AuthDetails authDetails);

    TagInfoResponse getTags(AuthDetails authDetails);
    List<Tag> getUserPreferTags(User user);

    void deleteTag(Long tagId, AuthDetails authDetails);


}
