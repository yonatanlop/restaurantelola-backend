package com.tialola.auth.service;

import com.tialola.auth.dto.LoginRequest;
import com.tialola.auth.dto.LoginResponse;
import com.tialola.auth.dto.UsuarioDTO;
import com.tialola.auth.model.Sesion;
import com.tialola.auth.model.Usuario;
import com.tialola.auth.repository.SesionRepository;
import com.tialola.auth.repository.UsuarioRepository;
import com.tialola.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsuarioAndActivo(request.getUsuario(), true)
            .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));
        
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }
        
        // Registrar sesión
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
        sesionRepository.save(sesion);
        
        // Generar token
        String token = jwtUtil.generateToken(usuario.getUsuario(), usuario.getRol());
        
        // Crear respuesta
        UsuarioDTO usuarioDTO = new UsuarioDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getUsuario(),
            usuario.getRol()
        );
        
        return new LoginResponse(token, usuarioDTO);
    }
    
    @Transactional
    public LoginResponse loginCajeroSinPassword() {
        // Buscar el usuario cajero (debe existir uno con rol CAJERO)
        Usuario cajero = usuarioRepository.findByRolAndActivo("CAJERO", true)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró usuario cajero configurado"));
        
        // Registrar sesión
        Sesion sesion = new Sesion();
        sesion.setUsuario(cajero);
        sesionRepository.save(sesion);
        
        // Generar token
        String token = jwtUtil.generateToken(cajero.getUsuario(), cajero.getRol());
        
        // Crear respuesta
        UsuarioDTO usuarioDTO = new UsuarioDTO(
            cajero.getId(),
            cajero.getNombre(),
            cajero.getUsuario(),
            cajero.getRol()
        );
        
        return new LoginResponse(token, usuarioDTO);
    }
    
    @Transactional
    public LoginResponse loginDueno(LoginRequest request) {
        // Buscar el usuario dueño
        Usuario dueno = usuarioRepository.findByRolAndActivo("DUENO", true)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró usuario dueño configurado"));
        
        // Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), dueno.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        // Registrar sesión
        Sesion sesion = new Sesion();
        sesion.setUsuario(dueno);
        sesionRepository.save(sesion);
        
        // Generar token
        String token = jwtUtil.generateToken(dueno.getUsuario(), dueno.getRol());
        
        // Crear respuesta
        UsuarioDTO usuarioDTO = new UsuarioDTO(
            dueno.getId(),
            dueno.getNombre(),
            dueno.getUsuario(),
            dueno.getRol()
        );
        
        return new LoginResponse(token, usuarioDTO);
    }
    
    public void logout(String token) {
        // Aquí podrías invalidar el token si usas una lista negra
        // Por ahora solo registramos el logout
    }
    
    public UsuarioDTO getCurrentUser(String token) {
        String username = jwtUtil.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsuario(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getUsuario(),
            usuario.getRol()
        );
    }
}
