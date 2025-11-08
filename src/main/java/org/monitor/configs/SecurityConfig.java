package org.monitor.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()  // disable CSRF for API
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and().httpBasic();  // enable basic auth
        return http.build();
    }
}
