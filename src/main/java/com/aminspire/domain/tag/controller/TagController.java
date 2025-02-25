package com.aminspire.domain.tag.controller;

import com.aminspire.domain.tag.dto.request.TagCreateRequest;
import com.aminspire.domain.tag.dto.request.TagInfoRequest;
import com.aminspire.domain.tag.dto.response.TagInfoResponse;
import com.aminspire.domain.tag.service.TagService;
import com.aminspire.domain.user.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keyword")
public class TagController {

    private final TagService tagService;

    //사용자 태그 추가
    @PostMapping("/")
    ResponseEntity<Void> tagCreate(@RequestBody TagCreateRequest request, @AuthenticationPrincipal UserDetails user){
        System.out.println(user);
        tagService.addTag(request,0L );
        return ResponseEntity.ok().build();
    }

    //태그 조회
    @GetMapping("/")
    TagInfoResponse tagListGet(@RequestBody TagInfoRequest request){
        return tagService.getTags(request);
    }

    //태그 삭제
    @DeleteMapping("/{tag_id}")
    ResponseEntity<Void> tagByUserCreate(@RequestParam Long tagId){
        tagService.deleteTag(tagId);
        return ResponseEntity.ok().build();
    }


}
