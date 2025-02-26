package com.aminspire.domain.tag.controller;

import com.aminspire.domain.tag.dto.request.TagCreateRequest;
import com.aminspire.domain.tag.dto.response.TagInfoResponse;
import com.aminspire.domain.tag.dto.response.TagListInfoResponse;
import com.aminspire.domain.tag.service.TagService;
import com.aminspire.global.security.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keywords")
public class TagController {

    private final TagService tagService;

    //사용자 태그 추가
    @PostMapping("/")
    TagInfoResponse tagCreate(@RequestBody TagCreateRequest request, @AuthenticationPrincipal AuthDetails authDetails){
        return tagService.addTagByUser(request,authDetails);
    }

    //태그 조회
    @GetMapping("/")
    TagListInfoResponse tagListGet(@AuthenticationPrincipal AuthDetails authDetails){
        return tagService.getTags(authDetails);
    }

    //태그 삭제
    @DeleteMapping("/{tagId}")
    ResponseEntity<Void> tagByUserCreate(@PathVariable Long tagId, @AuthenticationPrincipal AuthDetails authDetails){
        tagService.deleteTag(tagId, authDetails);
        return ResponseEntity.ok().build();
    }


}
