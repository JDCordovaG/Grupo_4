package com.BuildMyPC.msvc_quotation_service.Services;

import com.BuildMyPC.msvc_quotation_service.Clients.CompatibilityClient;
import com.BuildMyPC.msvc_quotation_service.Exceptions.QuotationException;
import com.BuildMyPC.msvc_quotation_service.Models.Dtos.QuotationDTO;
import com.BuildMyPC.msvc_quotation_service.Models.Quotation;
import com.BuildMyPC.msvc_quotation_service.Repositories.QuotationRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de {@link QuotationServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class QuotationServiceTests {

    @Mock
    private QuotationRepository repository;

    @Mock
    private CompatibilityClient compatibilityClient;

    @InjectMocks
    private QuotationServiceImpl quotationService;

    private QuotationDTO quotationDTOPrueba;
    private Quotation quotationPrueba;
    private List<Quotation> quotationList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // DTO de prueba
        QuotationDTO.ComponenteCotizadoDTO comp1 = new QuotationDTO.ComponenteCotizadoDTO();
        comp1.setComponenteId(10L);
        comp1.setNombre("Ryzen 7 5800X");
        comp1.setPrecio(450.0);

        QuotationDTO.ComponenteCotizadoDTO comp2 = new QuotationDTO.ComponenteCotizadoDTO();
        comp2.setComponenteId(20L);
        comp2.setNombre("RTX 4070");
        comp2.setPrecio(650.0);

        this.quotationDTOPrueba = new QuotationDTO();
        this.quotationDTOPrueba.setBuildId(50L);
        this.quotationDTOPrueba.setUsuarioId(1L);
        this.quotationDTOPrueba.setDescuento(50.0);
        this.quotationDTOPrueba.setComponentesSeleccionados(List.of(comp1, comp2));

        // Quotation de prueba
        this.quotationPrueba = Quotation.builder()
                .id(1L)
                .buildId(50L)
                .usuarioId(1L)
                .subtotal(1100.0)
                .descuento(50.0)
                .total(1050.0)
                .estado("VIGENTE")
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(7))
                .build();

        // Lista para pruebas
        for (int i = 0; i < 15; i++) {
            Quotation q = Quotation.builder()
                    .id((long) (i + 1))
                    .buildId((long) (50 + i))
                    .usuarioId(1L)
                    .total(faker.number().randomDouble(2, 800, 2500))
                    .estado(faker.options().option("VIGENTE", "ANULADA", "ACEPTADA"))
                    .fechaEmision(LocalDate.now().minusDays(i))
                    .build();
            quotationList.add(q);
        }
    }

    @Test
    @DisplayName("Debe generar cotización exitosamente cuando la build es compatible")
    public void shouldGenerateQuotationSuccessfully() {
        Map<String, Object> compatMap = new HashMap<>();
        compatMap.put("compatible", true);

        when(compatibilityClient.obtenerEstadoCompatibilidad(50L)).thenReturn(compatMap);
        when(repository.save(any(Quotation.class))).thenAnswer(inv -> {
            Quotation saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        Quotation result = quotationService.generarCotizacion(quotationDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getTotal()).isPositive();
        assertThat(result.getEstado()).isEqualTo("VIGENTE");

        verify(compatibilityClient, times(1)).obtenerEstadoCompatibilidad(50L);
        verify(repository, times(1)).save(any(Quotation.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la build no es compatible")
    public void shouldThrowExceptionWhenBuildNotCompatible() {
        Map<String, Object> compatMap = new HashMap<>();
        compatMap.put("compatible", false);

        when(compatibilityClient.obtenerEstadoCompatibilidad(50L)).thenReturn(compatMap);

        assertThatThrownBy(() -> quotationService.generarCotizacion(quotationDTOPrueba))
                .isInstanceOf(QuotationException.class)
                .hasMessageContaining("No se puede cotizar una build con fallos de compatibilidad");
    }

    @Test
    @DisplayName("Debe listar cotizaciones por usuario")
    public void shouldListQuotationsByUser() {
        Long usuarioId = 1L;
        when(repository.findByUsuarioId(usuarioId)).thenReturn(quotationList);

        List<Quotation> result = quotationService.listarPorUsuario(usuarioId);

        assertThat(result).hasSize(15);
        verify(repository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debe buscar cotización por ID")
    public void shouldFindQuotationById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(quotationPrueba));

        Quotation result = quotationService.buscarPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getBuildId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Debe actualizar estado de cotización")
    public void shouldUpdateQuotationStatus() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(quotationPrueba));

        Quotation result = quotationService.actualizarEstado(id, "ACEPTADA");

        assertThat(result.getEstado()).isEqualTo("ACEPTADA");
        verify(repository, times(1)).save(quotationPrueba);
    }

    @Test
    @DisplayName("Debe anular cotización vencida")
    public void shouldAnulateExpiredQuotation() {
        Long id = 1L;
        Quotation vencida = Quotation.builder()
                .id(id)
                .fechaVencimiento(LocalDate.now().minusDays(1))
                .estado("VIGENTE")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(vencida));

        Quotation result = quotationService.anularCotizacionVencida(id);

        assertThat(result.getEstado()).isEqualTo("ANULADA");
    }
}