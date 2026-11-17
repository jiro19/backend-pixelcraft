package com.pixelcraft.backend.dto;

import lombok.Data;

@Data
public class ItemBoletaRequest {
    private Long productoId;
    private Integer cantidad;
}