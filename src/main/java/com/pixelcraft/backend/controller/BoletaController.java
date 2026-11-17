package com.pixelcraft.backend.controller;

import com.pixelcraft.backend.dto.BoletaRequest;
import com.pixelcraft.backend.dto.ItemBoletaRequest;
import com.pixelcraft.backend.model.Boleta;
import com.pixelcraft.backend.model.DetalleBoleta;
import com.pixelcraft.backend.model.Producto;
import com.pixelcraft.backend.model.Usuario;
import com.pixelcraft.backend.repository.BoletaRepository;
import com.pixelcraft.backend.repository.ProductoRepository;
import com.pixelcraft.backend.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
@Tag(name = "Boletas", description = "Emisión y consulta de compras/ventas")
public class BoletaController {

    private final BoletaRepository boletaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @GetMapping
    @Operation(summary = "Listar todas las boletas")
    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar boletas de un cliente específico")
    public List<Boleta> listarPorUsuario(@PathVariable Long usuarioId) {
        return boletaRepository.findByUsuarioId(usuarioId);
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Generar nueva boleta de compra y descontar stock")
    public ResponseEntity<?> crearBoleta(@RequestBody BoletaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        Boleta boleta = new Boleta();
        boleta.setFecha(LocalDateTime.now());
        boleta.setUsuario(usuario);

        List<DetalleBoleta> detalles = new ArrayList<>();
        int totalBoleta = 0;

        for (ItemBoletaRequest item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElse(null);
            if (producto == null) {
                return ResponseEntity.badRequest().body("Producto ID " + item.getProductoId() + " no existe.");
            }
            if (producto.getStock() < item.getCantidad()) {
                return ResponseEntity.badRequest().body("Stock insuficiente para: " + producto.getNombre());
            }

            // Descontar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boleta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            int subtotal = producto.getPrecio() * item.getCantidad();
            detalle.setSubtotal(subtotal);

            totalBoleta += subtotal;
            detalles.add(detalle);
        }

        boleta.setTotal(totalBoleta);
        boleta.setDetalles(detalles);

        return ResponseEntity.ok(boletaRepository.save(boleta));
    }
}