package com.BuildMyPC.msvc_gpu_service.Services;

import com.BuildMyPC.msvc_gpu_service.Clients.ComponentClient;
import com.BuildMyPC.msvc_gpu_service.Exceptions.GpuException;
import com.BuildMyPC.msvc_gpu_service.Models.Dtos.GpuDTO;
import com.BuildMyPC.msvc_gpu_service.Models.Gpu;
import com.BuildMyPC.msvc_gpu_service.Repositories.GpuRepository;
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
 * Pruebas unitarias de {@link GpuServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class GpuServiceTests {

    @Mock
    private GpuRepository repository;

    @Mock
    private ComponentClient componentClient;

    @InjectMocks
    private GpuServiceImpl gpuService;

    private GpuDTO gpuDTOPrueba;
    private Gpu gpuPrueba;
    private List<Gpu> gpuList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.gpuDTOPrueba = new GpuDTO();
        this.gpuDTOPrueba.setComponenteId(200L);
        this.gpuDTOPrueba.setVramGb(12);
        this.gpuDTOPrueba.setTipoMemoria("GDDR6X");
        this.gpuDTOPrueba.setTdpWatts(320);
        this.gpuDTOPrueba.setLargoMm(320.0);
        this.gpuDTOPrueba.setPuntajeBase(14500);
        this.gpuDTOPrueba.setFabricanteChip("NVIDIA");
        this.gpuDTOPrueba.setActivo(true);

        this.gpuPrueba = new Gpu();
        this.gpuPrueba.setId(1L);
        this.gpuPrueba.setComponenteId(200L);
        this.gpuPrueba.setVramGb(12);
        this.gpuPrueba.setTipoMemoria("GDDR6X");
        this.gpuPrueba.setTdpWatts(320);
        this.gpuPrueba.setLargoMm(320.0);
        this.gpuPrueba.setPuntajeBase(14500);
        this.gpuPrueba.setFabricanteChip("NVIDIA");
        this.gpuPrueba.setActivo(true);

        for (int i = 0; i < 25; i++) {
            Gpu g = new Gpu();
            g.setId((long) (i + 1));
            g.setVramGb(faker.number().numberBetween(6, 24));
            g.setFabricanteChip(faker.options().option("NVIDIA", "AMD"));
            g.setTdpWatts(faker.number().numberBetween(150, 450));
            g.setActivo(true);
            gpuList.add(g);
        }
    }

    @Test
    @DisplayName("Debe crear GPU exitosamente")
    public void shouldCreateGpuSuccessfully() {
        // ✅ Solución: Usamos any() o un mock más seguro
        doNothing().when(componentClient).obtenerComponentePorId(anyLong());

        when(repository.save(any(Gpu.class))).thenAnswer(inv -> {
            Gpu saved = inv.getArgument(0);
            saved.setId(300L);
            return saved;
        });

        GpuDTO result = gpuService.crearGpu(gpuDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(300L);
        assertThat(result.getVramGb()).isEqualTo(12);

        verify(componentClient, times(1)).obtenerComponentePorId(200L);
        verify(repository, times(1)).save(any(Gpu.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si componente base no existe")
    public void shouldThrowExceptionWhenComponentNotFound() {
        doThrow(new RuntimeException("Componente no encontrado"))
                .when(componentClient).obtenerComponentePorId(999L);

        GpuDTO dtoInvalido = new GpuDTO();
        dtoInvalido.setComponenteId(999L);
        dtoInvalido.setVramGb(8);
        dtoInvalido.setFabricanteChip("AMD");

        assertThatThrownBy(() -> gpuService.crearGpu(dtoInvalido))
                .isInstanceOf(GpuException.class)
                .hasMessageContaining("Fallo de integridad relacional");
    }

    @Test
    @DisplayName("Debe listar todas las GPUs activas")
    public void shouldListAllActiveGpus() {
        when(repository.findByActivoTrue()).thenReturn(gpuList);

        List<GpuDTO> result = gpuService.listarTodas();

        assertThat(result).hasSize(25);
    }

    @Test
    @DisplayName("Debe buscar GPU por ID")
    public void shouldFindGpuById() {
        Long id = 1L;
        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(gpuPrueba));

        GpuDTO result = gpuService.buscarPorId(id);

        assertThat(result.getFabricanteChip()).isEqualTo("NVIDIA");
    }

    @Test
    @DisplayName("Debe buscar GPUs por fabricante")
    public void shouldFindGpusByFabricante() {
        String fabricante = "NVIDIA";
        when(repository.findByFabricanteChipIgnoreCaseAndActivoTrue(fabricante))
                .thenReturn(gpuList.subList(0, 12));

        List<GpuDTO> result = gpuService.buscarPorFabricante(fabricante);

        assertThat(result).hasSize(12);
    }

    @Test
    @DisplayName("Debe actualizar GPU")
    public void shouldUpdateGpu() {
        Long id = 1L;
        GpuDTO updateDTO = new GpuDTO();
        updateDTO.setVramGb(16);
        updateDTO.setTdpWatts(350);

        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(gpuPrueba));
        when(repository.save(any(Gpu.class))).thenAnswer(inv -> inv.getArgument(0));

        GpuDTO result = gpuService.actualizarGpu(id, updateDTO);

        assertThat(result.getVramGb()).isEqualTo(16);
    }

    @Test
    @DisplayName("Debe desactivar GPU")
    public void shouldDeactivateGpu() {
        Long id = 1L;
        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(gpuPrueba));

        gpuService.desactivarGpu(id);

        assertThat(gpuPrueba.getActivo()).isFalse();
        verify(repository, times(1)).save(gpuPrueba);
    }
}