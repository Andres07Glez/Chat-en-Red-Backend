package mx.edu.unpa.ChatEnRed.security;

import mx.edu.unpa.ChatEnRed.security.jwt.AuthEntryPointJwt;
import mx.edu.unpa.ChatEnRed.security.jwt.AuthTokenFilter;
import mx.edu.unpa.ChatEnRed.security.services.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity // <--- Asegura que Spring sepa que esto es seguridad web
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // (Opcional: Clase simple para manejar error 401)

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("--- CARGANDO CONFIGURACIÓN DE SEGURIDAD PERSONALIZADA ---");

        http
                // Desactivar CSRF (Causa principal del 403 en Postman)
                .csrf(csrf -> csrf.disable())

                // 2. Configurar CORS explícitamente para permitir peticiones desde Angular/Postman
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8181")); // Angular y Localhost
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))

                // 3. Manejo de excepciones (401 en lugar de 403 genérico)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

                // 4. Sin estado (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Rutas Públicas vs Privadas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()  // Login DEBE ser público
                        .requestMatchers("/error").permitAll()    // Errores de Spring públicos
                        .requestMatchers("/ws-chat/**").permitAll()
                        .anyRequest().authenticated()             // Todo lo demás privado
                );

        // 6. Agregar el filtro JWT antes del filtro de usuario/pass estándar
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
