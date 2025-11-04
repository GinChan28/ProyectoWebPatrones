/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.security;

import com.taller_mecanico.repository.UsuarioRepository;
import com.taller_mecanico.repository.UsuarioRolRepository;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author megan
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // para poder utilizar bcrypt por el tema de seguridad y encriptacion
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                .ignoringRequestMatchers("/logout")
                )
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/*", "/js/", "/img/**", "/webjars/*").permitAll()
                .requestMatchers("/", "/index", "/login", "/registro", "/registro/**", "/error").permitAll()
                .requestMatchers("/vehiculo/**", "/cita/**").hasAnyRole("CLIENTE", "ADMIN", "MECANICO")
                .requestMatchers("/mecanico/**", "/inventario/**").hasAnyRole("MECANICO", "ADMIN")
                /*.requestMatchers("/admin/**", "/inventario/**").hasRole("ADMIN")*/
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login").permitAll()
                .failureUrl("/login?error")
                /*.loginProcessingUrl("/login")*/
                .defaultSuccessUrl("/cita/lista", true)
                )
                .logout(log -> log
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                );
        /*.csrf(csrf -> csrf.ignoringRequestMatchers("/registro/guardar"))*/

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            UsuarioRepository usuarioRepo,
            UsuarioRolRepository usuarioRolRepo) {

        return username -> {
            var u = usuarioRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("No existe: " + username));

            /*var rolesUsuario = usuarioRolRepo.findByUsuario_IdUsuario(u.getIdUsuario());*/
            var authorities = usuarioRolRepo.findByUsuario_IdUsuario(u.getIdUsuario()).stream()
                    .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getRol()))
                    .toList();

            /*var authorities = rolesUsuario.stream()
                    .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getRol()))
                    .collect(Collectors.toList());*/
            return User.withUsername(u.getUsername())
                    .password(u.getPassword())
                    .authorities(authorities)
                    .accountLocked(Boolean.FALSE.equals(u.getActivo()))
                    .disabled(Boolean.FALSE.equals(u.getActivo()))
                    .build();
        };
    }
}
