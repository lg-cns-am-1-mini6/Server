package com.aminspire.global.utils;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class HtmlCleaner {
    private static final Logger log = LoggerFactory.getLogger(HtmlCleaner.class);

    public static void cleanObjectHtml(Object obj) {
        if (obj == null) return;

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
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

    private static String cleanHtml(String html) {
        if (html == null) {
            return null;
        }
        return Jsoup.parse(Jsoup.parse(html).text()).text();
    }
}
