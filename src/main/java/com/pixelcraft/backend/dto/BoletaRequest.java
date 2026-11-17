package com.pixelcraft.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BoletaRequest {
    private Long usuarioId;
    private List<ItemBoletaRequest> items;
}