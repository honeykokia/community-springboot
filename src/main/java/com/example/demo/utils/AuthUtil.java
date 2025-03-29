package com.example.demo.utils;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.exception.ApiException;

public class AuthUtil {

    public static Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        if(userId == null){
            throw new ApiException(Map.of("general", "資料取得失敗"));
        }
        return userId;
    }

}
