package com.BuildMyPC.msvc_benchmark_service.Services;

import com.BuildMyPC.msvc_benchmark_service.Clients.BuildClient;
import com.BuildMyPC.msvc_benchmark_service.Clients.CpuClient;
import com.BuildMyPC.msvc_benchmark_service.Clients.GpuClient;
import com.BuildMyPC.msvc_benchmark_service.Exceptions.BenchmarkException;
import com.BuildMyPC.msvc_benchmark_service.Models.Benchmark;
import com.BuildMyPC.msvc_benchmark_service.Models.Dtos.BenchmarkDTO;
import com.BuildMyPC.msvc_benchmark_service.Models.Dtos.BuildDTO;
import com.BuildMyPC.msvc_benchmark_service.Models.Dtos.CpuDTO;
import com.BuildMyPC.msvc_benchmark_service.Models.Dtos.GpuDTO;
import com.BuildMyPC.msvc_benchmark_service.Repositories.BenchmarkRepository;
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

@ExtendWith(MockitoExtension.class)
public class BenchmarkServiceTests {

    @Mock
    private BenchmarkRepository repository;

    @Mock
    private BuildClient buildClient;

    @Mock
    private CpuClient cpuClient;

    @Mock
    private GpuClient gpuClient;

    @InjectMocks
    private BenchmarkServiceImpl benchmarkService;

    private Benchmark benchmarkPrueba;
    private BenchmarkDTO benchmarkDTOPrueba;
    private BuildDTO buildDTOPrueba;
    private CpuDTO cpuDTOPrueba;
    private GpuDTO gpuDTOPrueba;
    private List<Benchmark> benchmarkList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.benchmarkDTOPrueba = new BenchmarkDTO();
        this.benchmarkDTOPrueba.setBuildId(10L);
        this.benchmarkDTOPrueba.setCategoriaUso("GAMING");

        this.buildDTOPrueba = new BuildDTO();
        this.buildDTOPrueba.setBuildId(10L);
        this.buildDTOPrueba.setEstado("COMPLETA");
        this.buildDTOPrueba.setCpuId(5L);
        this.buildDTOPrueba.setGpuId(7L);

        this.cpuDTOPrueba = new CpuDTO();
        this.cpuDTOPrueba.setNucleos(8);
        this.cpuDTOPrueba.setFrecuenciaBase(4.2);
        this.cpuDTOPrueba.setTdpWatts(125);

        this.gpuDTOPrueba = new GpuDTO();
        this.gpuDTOPrueba.setPuntajeBase(8500);
        this.gpuDTOPrueba.setVramGb(12);

        this.benchmarkPrueba = Benchmark.builder()
                .id(1L)
                .buildId(10L)
                .puntajeCpu(920.0)
                .puntajeGpu(1250.0)
                .puntajeTotal(1085.0)
                .categoriaUso("GAMING")
                .build();

        for (int i = 0; i < 30; i++) {
            Benchmark b = Benchmark.builder()
                    .id((long) (i + 1))
                    .buildId(10L)
                    .puntajeCpu(800.0 + i * 10)
                    .puntajeGpu(1000.0 + i * 15)
                    .puntajeTotal(900.0 + i * 12)
                    .categoriaUso(faker.options().option("GAMING", "OFIMATICA", "EDICION"))
                    .build();
            benchmarkList.add(b);
        }
    }

    @Test
    @DisplayName("Debe calcular y crear un benchmark exitosamente")
    public void shouldCalculateAndCreateBenchmarkSuccessfully() {
        when(buildClient.getBuildById(10L)).thenReturn(buildDTOPrueba);
        when(cpuClient.getCpuById(5L)).thenReturn(cpuDTOPrueba);
        when(gpuClient.getGpuById(7L)).thenReturn(gpuDTOPrueba);
        when(repository.save(any(Benchmark.class))).thenAnswer(inv -> inv.getArgument(0));

        Benchmark result = benchmarkService.calcularYCrearBenchmark(benchmarkDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getBuildId()).isEqualTo(10L);
        assertThat(result.getPuntajeTotal()).isPositive();
        assertThat(result.getCategoriaUso()).isEqualTo("GAMING");

        verify(buildClient, times(1)).getBuildById(10L);
        verify(cpuClient, times(1)).getCpuById(5L);
        verify(gpuClient, times(1)).getGpuById(7L);
        verify(repository, times(1)).save(any(Benchmark.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la build no está en estado COMPLETA")
    public void shouldThrowExceptionWhenBuildNotComplete() {
        BuildDTO buildIncompleta = new BuildDTO();
        buildIncompleta.setBuildId(10L);
        buildIncompleta.setEstado("EN_PROCESO");

        when(buildClient.getBuildById(10L)).thenReturn(buildIncompleta);

        assertThatThrownBy(() -> benchmarkService.calcularYCrearBenchmark(benchmarkDTOPrueba))
                .isInstanceOf(BenchmarkException.class)
                .hasMessageContaining("no está en estado COMPLETA");
    }

    @Test
    @DisplayName("Debe listar benchmarks por ID de build")
    public void shouldListBenchmarksByBuildId() {
        Long buildId = 10L;
        when(repository.findByBuildId(buildId)).thenReturn(benchmarkList);

        List<Benchmark> result = benchmarkService.listarPorBuild(buildId);

        assertThat(result).hasSize(30);
        verify(repository, times(1)).findByBuildId(buildId);
    }

    @Test
    @DisplayName("Debe buscar un benchmark por ID exitosamente")
    public void shouldFindBenchmarkById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(benchmarkPrueba));

        Benchmark result = benchmarkService.buscarPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar benchmark inexistente")
    public void shouldThrowExceptionWhenBenchmarkNotFound() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> benchmarkService.buscarPorId(id))
                .isInstanceOf(BenchmarkException.class)
                .hasMessage("Benchmark no encontrado con ID: 999");
    }

    @Test
    @DisplayName("Debe eliminar un benchmark existente")
    public void shouldDeleteExistingBenchmark() {
        Long id = 5L;
        when(repository.existsById(id)).thenReturn(true);

        benchmarkService.eliminarBenchmark(id);

        verify(repository, times(1)).existsById(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar benchmark inexistente")
    public void shouldThrowExceptionWhenDeletingNonExistentBenchmark() {
        Long id = 999L;
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> benchmarkService.eliminarBenchmark(id))
                .isInstanceOf(BenchmarkException.class)
                .hasMessageContaining("Benchmark no encontrado");
    }
}