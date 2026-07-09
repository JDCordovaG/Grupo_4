package com.BuildMyPC.msvc_cpu_service.Services;

import com.BuildMyPC.msvc_cpu_service.Clients.ComponentClient;
import com.BuildMyPC.msvc_cpu_service.Exceptions.CpuException;
import com.BuildMyPC.msvc_cpu_service.Models.Cpu;
import com.BuildMyPC.msvc_cpu_service.Models.Dtos.CpuDTO;
import com.BuildMyPC.msvc_cpu_service.Repositories.CpuRepository;
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
 * Pruebas unitarias de {@link CpuServiceImpl}.
 *
 * <p>Se mockean el repositorio y el cliente ComponentClient para validar lógica,
 * reglas de negocio y comunicación entre servicios.</p>
 */
@ExtendWith(MockitoExtension.class)
public class CpuServiceTests {

    @Mock
    private CpuRepository cpuRepository;

    @Mock
    private ComponentClient componentClient;

    @InjectMocks
    private CpuServiceImpl cpuService;

    private CpuDTO cpuDTOPrueba;
    private Cpu cpuPrueba;
    private List<Cpu> cpuList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // DTO de prueba
        this.cpuDTOPrueba = new CpuDTO();
        this.cpuDTOPrueba.setComponenteId(100L);
        this.cpuDTOPrueba.setSocket("AM4");
        this.cpuDTOPrueba.setNucleos(8);
        this.cpuDTOPrueba.setHilos(16);
        this.cpuDTOPrueba.setFrecuenciaBase(4.2);
        this.cpuDTOPrueba.setTdpWatts(105);
        this.cpuDTOPrueba.setGeneracion("Zen 3");
        this.cpuDTOPrueba.setSoportaDdr4(true);
        this.cpuDTOPrueba.setSoportaDdr5(false);
        this.cpuDTOPrueba.setActivo(true);

        // Entidad de prueba
        this.cpuPrueba = new Cpu();
        this.cpuPrueba.setId(1L);
        this.cpuPrueba.setComponenteId(100L);
        this.cpuPrueba.setSocket("AM4");
        this.cpuPrueba.setNucleos(8);
        this.cpuPrueba.setHilos(16);
        this.cpuPrueba.setFrecuenciaBase(4.2);
        this.cpuPrueba.setTdpWatts(105);
        this.cpuPrueba.setGeneracion("Zen 3");
        this.cpuPrueba.setSoportaDdr4(true);
        this.cpuPrueba.setActivo(true);

        // Lista para pruebas
        for (int i = 0; i < 30; i++) {
            Cpu c = new Cpu();
            c.setId((long) (i + 1));
            c.setSocket(faker.options().option("AM4", "LGA1700", "AM5"));
            c.setNucleos(faker.number().numberBetween(4, 16));
            c.setTdpWatts(faker.number().numberBetween(65, 250));
            c.setGeneracion("Zen " + faker.number().numberBetween(2, 5));
            c.setActivo(true);
            cpuList.add(c);
        }
    }

    @Test
    @DisplayName("Debe guardar un CPU exitosamente cuando el componente base existe")
    public void shouldSaveCpuSuccessfully() {
        when(componentClient.obtenerComponentePorId(100L)).thenReturn(null); // o un objeto del tipo esperado // simula éxito
        when(cpuRepository.save(any(Cpu.class))).thenAnswer(inv -> {
            Cpu saved = inv.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        CpuDTO result = cpuService.guardar(cpuDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(200L);
        assertThat(result.getSocket()).isEqualTo("AM4");
        verify(componentClient, times(1)).obtenerComponentePorId(100L);
        verify(cpuRepository, times(1)).save(any(Cpu.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el componente base no existe")
    public void shouldThrowExceptionWhenComponentNotFound() {
        doThrow(new RuntimeException("Not found")).when(componentClient).obtenerComponentePorId(999L);

        CpuDTO dtoInvalido = new CpuDTO();
        dtoInvalido.setComponenteId(999L);
        dtoInvalido.setSocket("AM4");
        dtoInvalido.setTdpWatts(100);

        assertThatThrownBy(() -> cpuService.guardar(dtoInvalido))
                .isInstanceOf(CpuException.class)
                .hasMessageContaining("El componente base no existe");
    }

    @Test
    @DisplayName("Debe listar todos los CPUs activos")
    public void shouldListAllActiveCpus() {
        when(cpuRepository.findByActivoTrue()).thenReturn(cpuList);

        List<CpuDTO> result = cpuService.listarTodos();

        assertThat(result).hasSize(30);
        verify(cpuRepository, times(1)).findByActivoTrue();
    }

    @Test
    @DisplayName("Debe buscar CPU por ID")
    public void shouldFindCpuById() {
        Long id = 1L;
        when(cpuRepository.findById(id)).thenReturn(Optional.of(cpuPrueba));

        CpuDTO result = cpuService.buscarPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getSocket()).isEqualTo("AM4");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar CPU inexistente o inactivo")
    public void shouldThrowExceptionWhenCpuNotFoundOrInactive() {
        Long id = 999L;
        when(cpuRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cpuService.buscarPorId(id))
                .isInstanceOf(CpuException.class)
                .hasMessageContaining("Procesador no encontrado");
    }

    @Test
    @DisplayName("Debe listar CPUs por socket")
    public void shouldListCpusBySocket() {
        String socket = "AM4";
        when(cpuRepository.findBySocketIgnoreCaseAndActivoTrue(socket)).thenReturn(cpuList.subList(0, 10));

        List<CpuDTO> result = cpuService.listarPorSocket(socket);

        assertThat(result).hasSize(10);
    }

    @Test
    @DisplayName("Debe actualizar un CPU existente")
    public void shouldUpdateCpuSuccessfully() {
        Long id = 1L;
        CpuDTO updateDTO = new CpuDTO();
        updateDTO.setSocket("AM5");
        updateDTO.setNucleos(12);
        updateDTO.setTdpWatts(120);

        when(cpuRepository.findById(id)).thenReturn(Optional.of(cpuPrueba));
        when(cpuRepository.save(any(Cpu.class))).thenAnswer(inv -> inv.getArgument(0));

        CpuDTO result = cpuService.actualizar(id, updateDTO);

        assertThat(result.getSocket()).isEqualTo("AM5");
        assertThat(result.getNucleos()).isEqualTo(12);
    }

    @Test
    @DisplayName("Debe desactivar un CPU")
    public void shouldDeactivateCpu() {
        Long id = 1L;
        when(cpuRepository.findById(id)).thenReturn(Optional.of(cpuPrueba));

        cpuService.desactivar(id);

        assertThat(cpuPrueba.getActivo()).isFalse();
        verify(cpuRepository, times(1)).save(cpuPrueba);
    }
}