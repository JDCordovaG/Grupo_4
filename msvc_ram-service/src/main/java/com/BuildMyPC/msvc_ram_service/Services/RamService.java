package com.BuildMyPC.msvc_ram_service.Services;

import com.BuildMyPC.msvc_ram_service.Models.Dtos.RamDTO;
import com.BuildMyPC.msvc_ram_service.Models.Ram;
import java.util.List;

public interface RamService {
    Ram crearRam(RamDTO dto);
    List<Ram> listarTodas();
    Ram buscarPorId(Long id);
}