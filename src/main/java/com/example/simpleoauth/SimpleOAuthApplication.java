package com.example.simpleoauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@RestController
public class SimpleOAuthApplication {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/test")
    public ModelAndView testMethod() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("test");
        return modelAndView;
    }
//http://localhost:8080/test.html?continue
    public static void main(String[] args) {
        SpringApplication.run(SimpleOAuthApplication.class, args);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.authorizeHttpRequests(authorize -> authorize.requestMatchers(
               new AntPathRequestMatcher("/"), new AntPathRequestMatcher("/test"),
               new AntPathRequestMatcher("/test.html"), new AntPathRequestMatcher("/index.html"),
                               new AntPathRequestMatcher("/error"),
                               new AntPathRequestMatcher("/webjars/**"))
               .permitAll()
               .anyRequest().authenticated())
               .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
               .oauth2Login(Customizer.withDefaults());
//               .oauth2Client(Customizer.withDefaults());

       return http.build();
    }

    @RequestMapping("/page")
    public String page(){
        return "User is authorized";
    }
}
