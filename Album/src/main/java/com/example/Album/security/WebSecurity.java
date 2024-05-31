package com.example.Album.security;

import com.example.Album.security.KeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(securedEnabled=true, prePostEnabled=true)
@Configuration
@EnableWebSecurity
public class WebSecurity {

    // Validating the user has specific scope
//    @Bean
//    SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(authz ->
//                        authz
//                                .requestMatchers(HttpMethod.GET, "/users/status/check").hasAuthority("SCOPE_test")
//                                .anyRequest()
//                                .authenticated())
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {}));
//        return http.build();
//    }


    // Validating the user has specific role
    // hasRole accepts 1 role and hasAnyRole accepts multiple roles
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        http.authorizeHttpRequests(authz ->
                        authz
                                .requestMatchers(HttpMethod.GET, "/users/status/check")
                                //.hasRole("developer")
                                .hasAnyRole("developer", "tester")
                                .anyRequest()
                                .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }
}
