package com.aminspire.domain.article.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleInfoResponse {

    private List<ArticleInfoItems> items;

    @Data
    @NoArgsConstructor
    public static class ArticleInfoItems {

        // Article 개별 필드
        @JsonProperty("title")
        private String title;

        @JsonProperty("link")
        private String link;

        @JsonProperty("description")
        private String description;

        @JsonProperty("pubDate")
        private String pubDate;
    }
}
