package com.GestionHotelera.Administracion.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.Usuario;
import com.GestionHotelera.Administracion.Repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        System.out.println("Usuario: " + usuario.getUsername()+", "+usuario.getId());
        if (usuario.getFlgEstado()==0) {
            throw new DisabledException("Usuario bloqueado o inactivo");            
        }
        
        //Convertimos el Entity User a UserDetails
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol()) // "ADMIN" → ROLE_ADMIN
                .disabled(usuario.getFlgEstado()==0)
                .build();
    }
}
