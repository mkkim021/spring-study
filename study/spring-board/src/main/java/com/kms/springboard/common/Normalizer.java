package com.kms.springboard.common;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;


@Component
public class Normalizer {

    public String normalize(String user) {
        return user==null ? "" : user.trim().toLowerCase(Locale.ROOT);
    }
}
