package com.pixelcraft.backend.controller;

import com.pixelcraft.backend.dto.AuthResponse;
import com.pixelcraft.backend.dto.LoginRequest;
import com.pixelcraft.backend.dto.RegisterRequest;
import com.pixelcraft.backend.model.Rol;
import com.pixelcraft.backend.model.Usuario;
import com.pixelcraft.backend.repository.UsuarioRepository;
import com.pixelcraft.backend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión con JWT")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario (ADMIN, VENDEDOR o CLIENTE)")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("El correo ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol() != null ? request.getRol() : Rol.CLIENTE);

        Usuario guardado = usuarioRepository.save(usuario);
        String token = jwtUtil.generateToken(guardado.getEmail(), guardado.getRol().name());

        return ResponseEntity.ok(new AuthResponse(
                token,
                guardado.getId(),
                guardado.getNombre(),
                guardado.getEmail(),
                guardado.getRol().name()
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener Token JWT")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());

        return ResponseEntity.ok(new AuthResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name()
        ));
    }
}