package com.pixelcraft.backend.controller;

import com.pixelcraft.backend.model.Producto;
import com.pixelcraft.backend.repository.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo y gestión de productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Filtrar productos por categoría")
    public List<Producto> listarPorCategoria(@PathVariable Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @PostMapping
    @Operation(summary = "Crear producto (ADMIN / VENDEDOR)")
    public Producto crear(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto (ADMIN / VENDEDOR)")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto datos) {
        return productoRepository.findById(id)
                .map(prod -> {
                    prod.setCodigo(datos.getCodigo());
                    prod.setNombre(datos.getNombre());
                    prod.setPrecio(datos.getPrecio());
                    prod.setDescripcion(datos.getDescripcion());
                    prod.setImagen(datos.getImagen());
                    prod.setStock(datos.getStock());
                    prod.setCategoria(datos.getCategoria());
                    return ResponseEntity.ok(productoRepository.save(prod));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto (Solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}