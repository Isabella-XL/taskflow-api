package com.xiaoxu.taskflow;

import com.xiaoxu.taskflow.security.JwtService;

public class JwtTest {

    public static void main(String[] args) {

        JwtService jwtService = new JwtService();

        String token = jwtService.generateToken("xiaoxu");

        System.out.println(token);
    }
}