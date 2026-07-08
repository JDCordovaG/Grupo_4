package com.BuildMyPC.msvc_quotation_service.Services;

import com.BuildMyPC.msvc_quotation_service.Clients.CompatibilityClient;
import com.BuildMyPC.msvc_quotation_service.Exceptions.QuotationException;
import com.BuildMyPC.msvc_quotation_service.Models.DetalleQuotation;
import com.BuildMyPC.msvc_quotation_service.Models.Quotation;
import com.BuildMyPC.msvc_quotation_service.Models.Dtos.QuotationDTO;
import com.BuildMyPC.msvc_quotation_service.Repositories.QuotationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository repository;
    private final CompatibilityClient compatibilityClient;

    @Override
    @Transactional
    public Quotation generarCotizacion(QuotationDTO dto) {
        log.info("Iniciando generación de cotización comercial para Build ID: {}", dto.getBuildId());

        try {
            Map<String, Object> compatibilidad = compatibilityClient.obtenerEstadoCompatibilidad(dto.getBuildId());
            boolean esCompatible = (boolean) compatibilidad.getOrDefault("compatible", false);
            if (!esCompatible) {
                throw new QuotationException("Operación Denegada: No se puede cotizar una build con fallos de compatibilidad técnica.");
            }
        } catch (Exception e) {
            log.error("Fallo de comunicación con msvc-compatibility: {}", e.getMessage());
            throw new QuotationException("Incapaz de verificar compatibilidad de la build en este momento.");
        }

        double subtotal = dto.getPreciosComponentes().stream().mapToDouble(Double::doubleValue).sum();
        double descuento = dto.getDescuento() != null ? dto.getDescuento() : 0.0;
        double total = subtotal - descuento;

        if (total < 0) total = 0.0;

        Quotation quotation = Quotation.builder()
                .buildId(dto.getBuildId())
                .usuarioId(dto.getUsuarioId())
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .estado("VIGENTE")
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(7))
                .build();

        dto.getComponentesSeleccionados().forEach(comp -> {
            DetalleQuotation detalle = DetalleQuotation.builder()
                    .componenteId(comp.getComponenteId())
                    .nombre(comp.getNombre())
                    .precio(comp.getPrecio())
                    .build();
            quotation.agregarDetalle(detalle);
        });

        log.info("Cotización generada exitosamente con ID asignado. Monto Total: ${}", total);
        return repository.save(quotation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quotation> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quotation> listarPorEstado(String estado) {
        return repository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public Quotation buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new QuotationException("Cotización comercial no localizada con ID: " + id));
    }

    @Override
    @Transactional
    public Quotation actualizarEstado(Long id, String nuevoEstado) {
        Quotation q = buscarPorId(id);
        q.setEstado(nuevoEstado.toUpperCase());
        log.info("Cambio de estado en cotización ID {}: a {}", id, nuevoEstado);
        return repository.save(q);
    }

    @Override
    @Transactional
    public Quotation anularCotizacionVencida(Long id) {
        Quotation q = buscarPorId(id);
        if (LocalDate.now().isAfter(q.getFechaVencimiento())) {
            q.setEstado("ANULADA");
            log.warn("La cotización ID {} ha expirado y se ha marcado automáticamente como ANULADA", id);
            return repository.save(q);
        }
        return q;
    }
}