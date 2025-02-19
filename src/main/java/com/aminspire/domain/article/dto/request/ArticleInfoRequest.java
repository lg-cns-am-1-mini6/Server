package com.aminspire.domain.article.dto.request;
// 클라이언트와 서버 간 데이터 전송 담당. 클라 요청 데이터.
// 뉴스 검색이나 필터링 등의 요청 데이터를 담는 용도
// 사용자가 선택한 카테고리, 키워드 받음
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArticleInfoRequest(@NotNull Long articleId, @NotBlank String articleName){
}
