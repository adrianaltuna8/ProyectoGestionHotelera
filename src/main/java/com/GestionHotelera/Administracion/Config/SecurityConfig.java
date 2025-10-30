package com.GestionHotelera.Administracion.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.GestionHotelera.Administracion.Service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permite acceso sin autenticación a recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN") // ROLE_ADMIN
                .anyRequest().authenticated()
            )
            .formLogin(form -> form //Autenticacion
                .loginPage("/login") //
                .defaultSuccessUrl("/usuarios", true) //redirige a endpoint GET
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true) //clean sesion y cookies
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        
        return http.build();
    }
    
    //Componente que verifica las credenciales, crea un objeto Authentication sí todo es valido
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //Por defecto DaoAuthenticationProvider el cual usa Servicio y Encoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

}
