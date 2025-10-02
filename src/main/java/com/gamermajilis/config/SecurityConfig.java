package com.gamermajilis.config;

import com.gamermajilis.security.JwtAuthenticationEntryPoint;
import com.gamermajilis.security.JwtAuthenticationFilter;
import com.gamermajilis.security.OAuth2AuthenticationFailureHandler;
import com.gamermajilis.security.OAuth2AuthenticationSuccessHandler;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.DiscordOAuth2ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private DiscordOAuth2ServiceImpl discordOAuth2Service;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/verify-email",
                                "/auth/resend-verification", "/auth/validate-token")
                        .permitAll()

                        // Discord OAuth endpoints
                        .requestMatchers("/auth/discord/**").permitAll()

                        // OAuth2 endpoints (for Discord login) - FIXED
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // Test endpoints (remove in production)
                        .requestMatchers("/test/**").permitAll()

                        // AI endpoints
                        .requestMatchers("/ai/**").permitAll()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**")
                        .permitAll()

                        // Health check and actuator endpoints
                        .requestMatchers("/actuator/**", "/health").permitAll()

                        // Static resources
                        .requestMatchers("/images/**", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        
                        // Uploaded files (profile pictures, media, etc.)
                        .requestMatchers("/uploads/**").permitAll()

                        // Protected endpoints
                        .requestMatchers("/auth/me", "/auth/logout").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/moderator/**").hasAnyRole("ADMINISTRATOR", "MODERATOR")

                        // All other requests need authentication
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        // Configure the OAuth2UserService
                        .userInfoEndpoint(userInfo -> userInfo.userService(discordOAuth2Service))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler));

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}