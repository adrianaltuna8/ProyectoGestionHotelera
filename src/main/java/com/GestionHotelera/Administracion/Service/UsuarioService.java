package com.GestionHotelera.Administracion.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.Usuario;
import com.GestionHotelera.Administracion.Repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
public Usuario guardar(Usuario usuario) 
{
    if (usuario.getId() != null) {
        // Es edición → buscamos el actual
        Usuario existente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si no se ingresó nueva contraseña, mantenemos la anterior
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            usuario.setPassword(existente.getPassword());
        }
    }

    // Solo encriptar si viene en texto plano
    if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    }

    if (usuario.getFlgEstado() == null) {
        usuario.setFlgEstado(0);
    }

    return usuarioRepository.save(usuario);
}

    
    public void eliminar(Long id) {
        Usuario usuario=usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setFlgEstado(0);
        usuarioRepository.save(usuario);
    }
    
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existeUsernameForUpdate(String username, Long id){
        return usuarioRepository.existsByUsernameAndIdNot(username, id);
    }
}
