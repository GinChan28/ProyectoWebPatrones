package com.taller_mecanico.security;

import com.taller_mecanico.repository.UsuarioRepository;
import com.taller_mecanico.repository.UsuarioRolRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ------------------------------------------
    // PASSWORD ENCODER
    // ------------------------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    // ------------------------------------------
    // HANDLER DE REDIRECCIÓN SEGÚN ROL
    // ------------------------------------------
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {

            var authorities = authentication.getAuthorities();
            String redirectUrl = "/login?error";

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/admin/dashboard";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MECANICO"))) {
                redirectUrl = "/mecanico/dashboard";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                redirectUrl = "/cliente/dashboard";
            }

            response.sendRedirect(redirectUrl);
        };
    }

    // ------------------------------------------
    // SECURITY FILTER CHAIN
    // ------------------------------------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/logout"))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/css/*", "/js/**", "/img/**", "/webjars/**").permitAll()
                    .requestMatchers("/", "/index", "/login", "/registro/**", "/error").permitAll()

                    // Permisos por rol
                    .requestMatchers("/cliente/**").hasRole("CLIENTE")
                    .requestMatchers("/mecanico/**").hasRole("MECANICO")
                    .requestMatchers("/admin/**").hasRole("ADMIN")

                    // Rutas antiguas
                    .requestMatchers("/vehiculo/**", "/cita/**")
                        .hasAnyRole("CLIENTE", "MECANICO", "ADMIN")

                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login").permitAll()
                    .failureUrl("/login?error")
                    .successHandler(successHandler())  // AQUÍ USAMOS EL HANDLER
            )
            .logout(log -> log
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            );

        return http.build();
    }

    // ------------------------------------------
    // USER DETAILS SERVICE
    // ------------------------------------------
    @Bean
    public UserDetailsService userDetailsService(
            UsuarioRepository usuarioRepo,
            UsuarioRolRepository usuarioRolRepo) {

        return username -> {
            var u = usuarioRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("No existe: " + username));

            var authorities = usuarioRolRepo.findByUsuario_IdUsuario(u.getIdUsuario())
                    .stream()
                    .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getRol()))
                    .toList();

            return User.withUsername(u.getUsername())
                    .password(u.getPassword())
                    .authorities(authorities)
                    .accountLocked(Boolean.FALSE.equals(u.getActivo()))
                    .disabled(Boolean.FALSE.equals(u.getActivo()))
                    .build();
        };
    }
}
