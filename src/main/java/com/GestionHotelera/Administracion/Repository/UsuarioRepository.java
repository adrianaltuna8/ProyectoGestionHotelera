package com.GestionHotelera.Administracion.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GestionHotelera.Administracion.Model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long id);
    List<Usuario> findByFlgEstado(Integer flgEstado);

}
