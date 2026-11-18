package br.com.zelabairro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//RN15: hash BCrypt das senhas (so a biblioteca spring-security-crypto, sem o Spring Security completo)
@Configuration
public class SenhaConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
