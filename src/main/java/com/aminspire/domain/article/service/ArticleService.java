package com.aminspire.domain.article.service;

import java.util.List;

import com.aminspire.domain.article.dto.response.ArticleInfoResponse;

public abstract interface ArticleService {
	public abstract List<ArticleInfoResponse> searchArticles(String keyword, String country);

	public abstract List<ArticleInfoResponse> getAllArticles();
}