package com.BuildMyPC.msvc_compatibility_service.Services;

import com.BuildMyPC.msvc_compatibility_service.Clients.*;
import com.BuildMyPC.msvc_compatibility_service.Exceptions.CompatibilityException;
import com.BuildMyPC.msvc_compatibility_service.Models.Dtos.*;
import com.BuildMyPC.msvc_compatibility_service.Models.ValidacionCompatibility;
import com.BuildMyPC.msvc_compatibility_service.Repositories.CompatibilityRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de {@link CompatibilityService}.
 *
 * <p>Se mockean el repositorio y todos los clientes Feign de componentes para validar
 * las reglas de compatibilidad (socket, RAM, fuente, etc.).</p>
 */
@ExtendWith(MockitoExtension.class)
public class CompatibilityServiceTests {

    @Mock
    private CompatibilityRepository repository;

    @Mock
    private CpuClient cpuClient;
    @Mock
    private GpuClient gpuClient;
    @Mock
    private MotherboardClient motherboardClient;
    @Mock
    private RamClient ramClient;
    @Mock
    private PowerSupplyClient powerSupplyClient;

    @InjectMocks
    private CompatibilityService compatibilityService;

    private CompatibilityRequestDTO requestDTO;
    private ValidacionCompatibility validacionPrueba;
    private List<ValidacionCompatibility> validacionList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // Request DTO de prueba
        this.requestDTO = new CompatibilityRequestDTO();
        this.requestDTO.setBuildId(50L);
        this.requestDTO.setCpuId(10L);
        this.requestDTO.setGpuId(20L);
        this.requestDTO.setMotherboardId(5L);
        this.requestDTO.setRamId(8L);
        this.requestDTO.setFuenteId(15L);

        // Validación de prueba
        this.validacionPrueba = new ValidacionCompatibility();
        this.validacionPrueba.setId(1L);
        this.validacionPrueba.setBuildId(50L);
        this.validacionPrueba.setCompatible(true);
        this.validacionPrueba.setConsumoEstimadoWatts(450);
        this.validacionPrueba.setMargenFuente("150W");
        this.validacionPrueba.setObservaciones("Todos los componentes son compatibles.");

        // Lista para pruebas
        for (int i = 0; i < 25; i++) {
            ValidacionCompatibility v = new ValidacionCompatibility();
            v.setId((long) (i + 1));
            v.setBuildId((long) (50 + i));
            v.setCompatible(faker.bool().bool());
            validacionList.add(v);
        }
    }

    @Test
    @DisplayName("Debe crear validación de compatibilidad exitosamente (todos los componentes compatibles)")
    public void shouldCreateCompatibilityValidationSuccessfully() {
        // Mock de respuestas de clientes
        CpuDTO cpu = new CpuDTO();
        cpu.setId(10L);
        cpu.setSocket("AM4");
        cpu.setTdpWatts(105);

        GpuDTO gpu = new GpuDTO();
        gpu.setId(20L);
        gpu.setTdpWatts(250);

        MotherboardDTO mobo = new MotherboardDTO();
        mobo.setId(5L);
        mobo.setSocket("AM4");
        mobo.setTipoRamSoportada("DDR4");
        mobo.setMaxRamGb(128);

        RamDTO ram = new RamDTO();
        ram.setId(8L);
        ram.setTipoDdr("DDR4");
        ram.setCapacidadGb(32);

        PowerSupplyDTO psu = new PowerSupplyDTO();
        psu.setId(15L);
        psu.setPotenciaWatts(650);

        when(cpuClient.findById(10L)).thenReturn(cpu);
        when(gpuClient.findById(20L)).thenReturn(gpu);
        when(motherboardClient.findById(5L)).thenReturn(mobo);
        when(ramClient.findById(8L)).thenReturn(ram);
        when(powerSupplyClient.findById(15L)).thenReturn(psu);

        when(repository.save(any(ValidacionCompatibility.class))).thenAnswer(inv -> inv.getArgument(0));

        ValidacionCompatibility result = compatibilityService.crearValidacion(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCompatible()).isTrue();
        assertThat(result.getConsumoEstimadoWatts()).isPositive();
        assertThat(result.getDetalles()).isNotEmpty();

        verify(repository, times(1)).findByBuildId(50L); // para eliminar anterior si existe
        verify(repository, times(1)).save(any(ValidacionCompatibility.class));
    }

    @Test
    @DisplayName("Debe marcar como incompatible cuando hay conflicto de socket")
    public void shouldMarkIncompatibleWhenSocketMismatch() {
        CpuDTO cpu = new CpuDTO(); cpu.setSocket("AM5");
        MotherboardDTO mobo = new MotherboardDTO(); mobo.setSocket("AM4");

        when(cpuClient.findById(anyLong())).thenReturn(cpu);
        when(motherboardClient.findById(anyLong())).thenReturn(mobo);
        // otros mocks mínimos
        when(gpuClient.findById(anyLong())).thenReturn(new GpuDTO());
        when(ramClient.findById(anyLong())).thenReturn(new RamDTO());
        when(powerSupplyClient.findById(anyLong())).thenReturn(new PowerSupplyDTO());

        when(repository.save(any(ValidacionCompatibility.class))).thenAnswer(inv -> inv.getArgument(0));

        ValidacionCompatibility result = compatibilityService.crearValidacion(requestDTO);

        assertThat(result.getCompatible()).isFalse();
        assertThat(result.getDetalles()).anyMatch(d ->
                "NO_CUMPLE".equals(d.getResultado()) && d.getRegla().contains("Socket"));
    }

    @Test
    @DisplayName("Debe listar todas las validaciones")
    public void shouldListAllValidations() {
        when(repository.findAll()).thenReturn(validacionList);

        List<ValidacionCompatibility> result = compatibilityService.listarTodas();

        assertThat(result).hasSize(25);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar validación por ID")
    public void shouldFindValidationById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(validacionPrueba));

        ValidacionCompatibility result = compatibilityService.buscarPorId(id);

        assertThat(result.getId()).isEqualTo(1L);
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar validación inexistente")
    public void shouldThrowExceptionWhenValidationNotFound() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> compatibilityService.buscarPorId(id))
                .isInstanceOf(CompatibilityException.class)
                .hasMessage("Validación no encontrada con ID: 999");
    }

    @Test
    @DisplayName("Debe eliminar una validación existente")
    public void shouldDeleteValidation() {
        Long id = 10L;
        when(repository.findById(id)).thenReturn(Optional.of(validacionPrueba));

        compatibilityService.eliminarValidacion(id);

        verify(repository, times(1)).delete(validacionPrueba);
    }
}