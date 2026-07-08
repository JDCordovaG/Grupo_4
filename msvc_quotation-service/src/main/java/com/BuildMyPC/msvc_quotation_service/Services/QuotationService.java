package com.BuildMyPC.msvc_quotation_service.Services;

import com.BuildMyPC.msvc_quotation_service.Models.Quotation;
import com.BuildMyPC.msvc_quotation_service.Models.Dtos.QuotationDTO;
import java.util.List;

public interface QuotationService {
    Quotation generarCotizacion(QuotationDTO dto);
    List<Quotation> listarPorUsuario(Long usuarioId);
    List<Quotation> listarPorEstado(String estado);
    Quotation buscarPorId(Long id);
    Quotation actualizarEstado(Long id, String nuevoEstado);
    Quotation anularCotizacionVencida(Long id);
}