package com.app.quantitymeasurement.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.quantitymeasurement.security.JwtFilter;
import com.app.quantitymeasurement.security.UserDetail;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtFilter jwtFilter;
	
	@Autowired
	private UserDetail userDetail;
	
	@Bean
	 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	return http.cors(cors -> cors.configurationSource( corsConfigurationSource()))
	.csrf(csrf -> csrf.disable())
	.sessionManagement (session ->
	session. sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	.authorizeHttpRequests (authz -> authz
	.requestMatchers("/api/v1/auth/signup","/api/v1/auth/signin","/api/v1/quantities/h2-console/**","/api/v1/quantities/swagger-ui/**").permitAll()
	.anyRequest ().authenticated()
	)
	.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();

	
	}

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
	CorsConfiguration configuration = new CorsConfiguration();
	configuration.setAllowedOrigins(Arrays.asList( "http://localhost:3000", "http://localhost:8080")
	);
	configuration. setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

	configuration. setAllowedHeaders(Arrays.asList("*"));
	configuration. setAllowCredentials(true);

	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source. registerCorsConfiguration ( "/**", configuration);
	return source;
	}
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
    	return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
}
