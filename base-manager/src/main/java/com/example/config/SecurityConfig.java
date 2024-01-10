package com.example.config;

import com.example.filters.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /* List of APIs to be accessible only via localhost (i.e. other microservices) for POST method */
    private final String[] API_ACCESSED_ONLY_BY_LOCALHOST_LIST = {
            "/api/building/completeUpgrade/**",
            "/api/base/*/finishBuilding/**",
            "/api/base/*/completeUnitsRecruitment",
            "/api/supportArmy/completeSend/*/to/**",
            "/api/supportArmy/completeReturn/**",
            "/api/base/*/getUnitsForNextRound"
    };

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(antMatcher("/error")).permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll());

        for (String endpoint : API_ACCESSED_ONLY_BY_LOCALHOST_LIST) {
               http
                   .authorizeHttpRequests(auth -> auth
                   .requestMatchers(antMatcher(HttpMethod.POST, endpoint))
                   .access((authentication, context) ->
                           new AuthorizationDecision(new IpAddressMatcher("127.0.0.1").matches(context.getRequest()))));
        }

        http.authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated());

                /*.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login-error")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/denied");*/

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}

