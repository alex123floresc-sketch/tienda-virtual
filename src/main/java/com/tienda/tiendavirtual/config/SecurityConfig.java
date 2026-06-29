package com.tienda.tiendavirtual.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;



@Configuration
@EnableMethodSecurity //
public class SecurityConfig {



    @Autowired

    private UserDetailsServiceImpl userDetailsService;



// Encriptador de contraseñas — lo usará UsuarioService al registrar

    @Bean

    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }



    @Bean

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

                .authorizeHttpRequests(auth -> auth

// Rutas públicas: cualquiera puede entrar sin login

                                .requestMatchers(

                                        "/", "/producto/**", "/carrito/**",

                                        "/login", "/registro", "/uploads/**",

                                        "/css/**", "/js/**", "/img/**"

                                ).permitAll()



// Solo administradores

                                .requestMatchers("/admin/**").hasRole("ADMIN")



//Todo lo demás (checkout, mis-pedidos, factura) requiere login

                                .anyRequest().authenticated()

                )

                .formLogin(form -> form

                        .loginPage("/login") // página de login personalizada

                        .loginProcessingUrl("/login") // URL a la que se envía el formulario

                        .defaultSuccessUrl("/", false) // a dónde va después de loguearse

                        .failureUrl("/login?error=true") // a dónde va si falla

                        .permitAll()

                )

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout=true")

                        .permitAll()

                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/carrito/**") // el carrito usa fetch simple, sin token aún
                );



        return http.build();

    }

}