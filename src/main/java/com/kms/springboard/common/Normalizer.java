package com.kms.springboard.common;


import org.springframework.stereotype.Component;

import java.util.Locale;


@Component
public class Normalizer {

    public String normalize(String user) {
        if (user == null) {
            throw new IllegalArgumentException("정규화할 문자열이 null입니다");
        }
        var trimmeed = user.trim();
        if(trimmeed.isEmpty()) {
            throw new IllegalArgumentException("정규화할 문자열이 비어 있습니다");
        }
        return trimmeed.toLowerCase(Locale.ROOT);
    }
}
