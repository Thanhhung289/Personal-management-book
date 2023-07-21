package com.group6.moneymanagementbooking.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
public class CaptchaServiceUtils {
    public static final String SITE_KEY = "6LfMaukiAAAAABd36Sv2HGjfv4Rs9IYWXT8aYoyq";
    public static final String SECRET_KEY = "6LfMaukiAAAAACObPeOvRs7H3_z2FJ_1lVEXqzHO";
    public static final String RECAPTCHA_API_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static boolean verify(String gRecaptchaResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("secret", SECRET_KEY);
        map.add("response", gRecaptchaResponse);
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map, headers);
        RestTemplate captchaTemplate = new RestTemplate();
        ResponseEntity<String> response  = captchaTemplate.postForEntity(RECAPTCHA_API_URL, request, String.class);
        if (response != null && response.getBody() != null && response.getBody().contains("\"success\": true")) {
            return true;
        }
        return false;
    }
}
