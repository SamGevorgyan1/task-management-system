package com.taskmanagement.config;

import com.taskmanagement.common.utils.MD5Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BeanConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new MD5Encoder();
    }

}
