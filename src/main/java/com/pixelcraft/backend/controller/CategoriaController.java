package com.pixelcraft.backend.controller;

import com.pixelcraft.backend.model.Categoria;
import com.pixelcraft.backend.repository.CategoriaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías del catálogo")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    @GetMapping
    @Operation(summary = "Listar todas las categorías")
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría (ADMIN / VENDEDOR)")
    public Categoria crear(@RequestBody Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría (ADMIN / VENDEDOR)")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria datos) {
        return categoriaRepository.findById(id)
                .map(cat -> {
                    cat.setNombre(datos.getNombre());
                    cat.setDescripcion(datos.getDescripcion());
                    return ResponseEntity.ok(categoriaRepository.save(cat));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría (Solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}