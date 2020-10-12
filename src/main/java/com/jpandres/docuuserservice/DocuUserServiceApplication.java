package com.jpandres.docuuserservice;

import com.jpandres.docuuserservice.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class DocuUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocuUserServiceApplication.class, args);
    }

    @Bean
    public Map<String, User> userRepo() {
        return new ConcurrentHashMap<>();
    }

}
