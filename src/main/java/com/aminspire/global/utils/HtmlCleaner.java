package com.aminspire.global.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class HtmlCleaner {
    private static final Logger log = LoggerFactory.getLogger(HtmlCleaner.class);

    // 클리닝할 필드와 해당 필드에 대한 클리닝 방식 설정
    private static final Map<String, HtmlCleanRule> fieldCleanRules = Map.of(
            "title", new HtmlCleanRule(value -> cleanHtml(value), true),
            "description", new HtmlCleanRule(value -> cleanHtml(value), false)
            // 다른 필드와 규칙을 추가할 수 있음
    );

    public static void cleanObjectHtml(Object obj) {
        if (obj == null) return;

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // 필드 이름에 맞는 클리닝 규칙이 있으면 해당 규칙을 적용
                HtmlCleanRule rule = fieldCleanRules.get(field.getName());
                if (rule != null && field.getType().equals(String.class)) {
                    String value = (String) field.get(obj);
                    if (value != null && rule.isCleanable) {
                        field.set(obj, rule.cleaningFunction.apply(value));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            log.error("HTML 클리닝 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private static String cleanHtml(String html) {
        if (html == null) {
            return null;
        }
        // 기본적으로 HTML 태그를 모두 제거
        return Jsoup.parse(Jsoup.parse(html).text()).text();
    }

    // 클리닝 규칙을 담는 클래스
    private static class HtmlCleanRule {
        private final java.util.function.Function<String, String> cleaningFunction;
        private final boolean isCleanable;

        public HtmlCleanRule(java.util.function.Function<String, String> cleaningFunction, boolean isCleanable) {
            this.cleaningFunction = cleaningFunction;
            this.isCleanable = isCleanable;
        }
    }

    // 특정 조건에 맞는 필드만 클리닝하고 싶다면 필터링 함수도 만들 수 있음
    public static void cleanHtmlByCondition(Object obj, Predicate<Field> condition) {
        if (obj == null) return;

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // 조건에 맞는 필드만 클리닝
                if (condition.test(field) && field.getType().equals(String.class)) {
                    String value = (String) field.get(obj);
                    if (value != null) {
                        field.set(obj, cleanHtml(value));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            log.error("HTML 클리닝 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
