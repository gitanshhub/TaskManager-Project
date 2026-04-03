package com.TaskManagerAPIProject.TaskManagerAPI_Project.configration;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.service.userDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class config {

    @Autowired
    private userDetailService userDetailService;

    @Bean
    // Uses BCrypt so stored passwords are hashed instead of persisted in plain text.
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(12);
    }

    @Bean
    // Connects Spring Security authentication to the custom user details service.
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    // Configures stateless basic-auth security and endpoint-level access rules.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/", "/index.html", "/task-ui.html", "/styles.css", "/login.js", "/app.js").permitAll()
                                .requestMatchers("/register").permitAll()
                                .requestMatchers(HttpMethod.GET, "/task/**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/task").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT,  "/task/**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.DELETE, "/task/**").hasAnyRole("ADMIN", "USER")
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }


}
