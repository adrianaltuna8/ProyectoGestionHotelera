package com.GestionHotelera.Administracion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.GestionHotelera.Administracion.Model.Usuario;
import com.GestionHotelera.Administracion.Service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')") // Solo administradores
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    
    //CREO QUE FALTA PARA REGISTRO
    // LISTAR USUARIOS
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "user/listar";
    }
    
    // FORMULARIO NUEVO USUARIO
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "user/formulario";
    }
    
    // GUARDAR USUARIO
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, 
        BindingResult result, Model model) {
        if (result.hasErrors()) {
            //En caso de error, estamos renderizando la vista directamente, El modelo actual que incluye
            //el objeot usuario con los datos y el atributo de error se pasa al thymeleaf
            return "user/formulario";
        }
        // Validar username único pero solo cuando se estar Creando no Cuando hace Update
        if (usuarioService.existeUsername(usuario.getUsername()) && usuario.getId()==null) {
            model.addAttribute("error", "El nombre de usuario ya existe");
            return "user/formulario";
        } else if(usuarioService.existeUsernameForUpdate(usuario.getUsername(), usuario.getId())){
            model.addAttribute("error", "El nombre de usuario ya esta usado por otra cuenta");
            return "user/formulario";            
        }
        //para caso de update se debe de tambien validar que no exista otro username igual
        //obviando el actual username que sí puede ser igual ya que es el mismo usuario
        usuarioService.guardar(usuario);
        //aqui redirige al endpoint de listado y con un parametro en la url
        return "redirect:/usuarios?exito";
    }
    // FORMULARIO EDITAR
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "user/formulario";
    }
    // ELIMINAR USUARIO
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios?eliminado";
    }
}
