package com.aminspire.domain.tag.service;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.dto.request.TagCreateRequest;
import com.aminspire.domain.tag.dto.response.TagInfoResponse;
import com.aminspire.domain.tag.repository.TagRepository;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.TagErrorCode;
import com.aminspire.global.exception.errorcode.UserErrorCode;
import com.aminspire.global.security.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    //사용자 태그 추가
    @Override
    @Transactional
    public void addTagByUser(TagCreateRequest request, AuthDetails authDetails) {
        User user = getUserFromAuthDetails(authDetails);
        String keyword = normalizeKeyword(request.keyword());
        if(!isTagExists(keyword, user)){
            Tag tag = Tag.createTag(keyword, user);
            tagRepository.save(tag);
        }else{
            throw new CommonException(TagErrorCode.TAG_ALREADY_EXISTS);
        }

    }

    //AI 추출 태그 추가
    @Override
    @Transactional
    public void addTagByOpenAi(TagCreateRequest request, AuthDetails authDetails) {
        User user = getUserFromAuthDetails(authDetails);
        String keyword = normalizeKeyword(request.keyword());
        Optional<Tag> tag = tagRepository.findByKeywordAndSearcher(keyword, user);
        if(tag.isEmpty()){
            Tag newTag = Tag.createTag(keyword, user);
            tagRepository.save(newTag);
        }else{
            tag.get().increaseScore();
        }

    }

    //
    @Override
    public TagInfoResponse getTags(AuthDetails authDetails) {
        User user = getUserFromAuthDetails(authDetails);
        return new TagInfoResponse(tagRepository.findAllBySearcher(user));
    }

    //키워드 기사 조회용
    @Override
    public List<Tag> getUserPreferTags(User user) {
        return null;
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId, AuthDetails authDetails) throws CommonException {
        if(isTagExists(tagId)){
            Tag tag = getTagById(tagId);
            tagRepository.delete(tag);
        }else{
            throw new CommonException(TagErrorCode.TAG_NOT_FOUND);
        }

    }

    //utils
    private User getUserFromAuthDetails(AuthDetails authDetails) throws CommonException{
        return userRepository.findById(authDetails.user().getId())
                .orElseThrow(()-> new CommonException(UserErrorCode.USER_NOT_FOUND));
    }

    private Tag getTagById (Long tagId) throws CommonException{
        return tagRepository.findById(tagId)
                .orElseThrow(()-> new CommonException(TagErrorCode.TAG_NOT_FOUND));
    }

    private boolean isTagExists(String keyword, User searcher){
        return tagRepository.findByKeywordAndSearcher(keyword, searcher).isPresent();
    }

    private boolean isTagExists(Long tagId){
        return tagRepository.findById(tagId).isPresent();
    }

    private String normalizeKeyword(String keyword){
        return Optional.ofNullable(keyword)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .map(s -> Normalizer.normalize(s, Normalizer.Form.NFC))
                .map(s -> s.replaceAll("[^가-힣a-zA-Z0-9\\s]", ""))
                .map(s -> s.replaceAll("\\s+", " "))
                .orElse("");
    }


}