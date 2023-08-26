package com.gms.alquimiapay.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil
{
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String hashPassword(String rawPassword){
        return passwordEncoder.encode(rawPassword);
    }

    public boolean isPasswordMatch(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
