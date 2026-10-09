package com.mengzhidui.a7springboot;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootTest
public class testPW {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${coze.url}")
    private String url;

    @Test
    public void getEncodePW() {
        String pw = "123456";
        System.out.println(passwordEncoder.encode(pw));
    }

    @Test
    public void testValue(){
        System.out.println(url);
    }

    @Test
    public void getUUID(){
        System.out.println(UUID.randomUUID());
    }
}
