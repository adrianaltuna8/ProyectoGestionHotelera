package com.GestionHotelera.Administracion.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.GestionHotelera.Administracion.Model.Usuario;
import com.GestionHotelera.Administracion.Repository.UsuarioRepository;
import com.GestionHotelera.Administracion.Service.UsuarioService;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadInitialData(UsuarioService usuarioService) {
        return args -> {
            // Revisa si ya existe un usuario admin
            if (!usuarioService.existeUsername("ani")) {
                Usuario admin = new Usuario();
                admin.setUsername("ani");
                admin.setPassword("ani123"); // La contraseña será encriptada en el service
                admin.setRol("ADMIN");
                usuarioService.guardar(admin);
                System.out.println("Usuario 'test' creado con éxito.");
            }
        };
    }

}
