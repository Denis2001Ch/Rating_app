package com.example.sweater.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean(name = "userService")
    public UserService getUserService(){
        return new UserService();
    }
}
