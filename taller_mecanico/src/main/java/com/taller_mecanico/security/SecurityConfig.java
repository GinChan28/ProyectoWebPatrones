/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.security;

import com.google.cloud.storage.HttpMethod;
import com.taller_mecanico.repository.UsuarioRepository;
import com.taller_mecanico.repository.UsuarioRolRepository;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author megan
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/login",
                        "/registro", "/registro/**",
                        "/css/*", "/js/", "/img/", "/webjars/*").permitAll()
                .requestMatchers("/vehiculo/*", "/cita/*").hasRole("CLIENTE")
                .requestMatchers("/mecanico/**").hasRole("MECANICO")
                .requestMatchers("/admin/*", "/inventario/*").hasRole("ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/cita/lista", true)
                .permitAll()
                )
                .logout(log -> log
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/registro/guardar")
                );

        return http.build();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            UsuarioRepository usuarioRepo,
            UsuarioRolRepository usuarioRolRepo) {

        return username -> {
            var u = usuarioRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("No existe: " + username));

            var rolesUsuario = usuarioRolRepo.findByUsuario_IdUsuario(u.getIdUsuario());

            var authorities = rolesUsuario.stream()
                    .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getRol()))
                    .collect(Collectors.toList());

            return User.withUsername(u.getUsername())
                    .password(u.getPassword()) // se debe colocar {noop}en la clave por temas de encriptacion 
                    .authorities(authorities)
                    .accountLocked(Boolean.FALSE.equals(u.getActivo()))
                    .disabled(Boolean.FALSE.equals(u.getActivo()))
                    .build();
        };

    }
}
