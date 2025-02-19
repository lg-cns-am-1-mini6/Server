package com.aminspire.domain.article.dto.response;
// 클라이언트와 서버 간 데이터 전송 담당. 서버가 클라로 보낼 응답 데이터.
// API에서 받은 뉴스 정보를 클라이언트에게 전송할 때 사용
// 뉴스 제목, 링크, 설명, 이미지를 포함한 데이터를 반환하는 형태
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArticleInfoResponse(@NotNull Long articleId, @NotBlank String articleName){
}
