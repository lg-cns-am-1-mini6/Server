package com.aminspire.domain.tag.service;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.dto.request.TagCreateRequest;
import com.aminspire.domain.tag.dto.request.TagInfoRequest;
import com.aminspire.domain.tag.dto.response.TagInfoResponse;
import com.aminspire.domain.tag.repository.TagRepository;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.TagErrorCode;
import com.aminspire.global.exception.errorcode.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    public void addTag(TagCreateRequest request, Long userId) {
        User user =getUserById(userId);
        Tag tag = Tag.createTag(request.keyword(), user);
        tagRepository.save(tag);
    }

    @Override
    public void addTagDirectByUser(TagCreateRequest request, User searcher) {
        tagRepository.save(Tag.createTag(request.keyword(), searcher));
    }

    @Override
    public TagInfoResponse getTags(TagInfoRequest request) {
        return new TagInfoResponse(tagRepository.findAllBySearcher(request.searcher()));
    }

    //키워드 기사 조회 용
    @Override
    public List<Tag> getUserPreferTags(User user) {
        return null;
    }

    @Override
    public void deleteTag(Long tagId) {
        Tag tag = getTagById(tagId);
        tagRepository.delete(tag);
    }

    private User getUserById(Long userId) throws CommonException{
        return userRepository.findById(userId)
                .orElseThrow(()-> new CommonException(UserErrorCode.USER_NOT_FOUND));
    }

    private Tag getTagById (Long tagId) throws CommonException{
        return tagRepository.findById(tagId)
                .orElseThrow(()-> new CommonException(TagErrorCode.TAG_NOT_FOUND));

    }

}
