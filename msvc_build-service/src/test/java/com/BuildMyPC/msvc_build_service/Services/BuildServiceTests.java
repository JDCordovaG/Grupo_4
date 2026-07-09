package com.BuildMyPC.msvc_build_service.Services;

import com.BuildMyPC.msvc_build_service.Clients.CompatibilityClient;
import com.BuildMyPC.msvc_build_service.Clients.UserClient;
import com.BuildMyPC.msvc_build_service.Exceptions.BuildException;
import com.BuildMyPC.msvc_build_service.Models.Build;
import com.BuildMyPC.msvc_build_service.Models.Dtos.BuildDTO;
import com.BuildMyPC.msvc_build_service.Repositories.BuildRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BuildServiceTests {

    @Mock
    private BuildRepository repository;

    @Mock
    private UserClient userClient;

    @Mock
    private CompatibilityClient compatibilityClient;

    @InjectMocks
    private BuildService buildService;

    private Build buildPrueba;
    private BuildDTO buildDTOPrueba;
    private List<Build> buildList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.buildDTOPrueba = new BuildDTO();
        this.buildDTOPrueba.setUsuarioId(1L);
        this.buildDTOPrueba.setCpuId(10L);
        this.buildDTOPrueba.setGpuId(20L);
        this.buildDTOPrueba.setMotherboardId(5L);
        this.buildDTOPrueba.setRamId(8L);
        this.buildDTOPrueba.setFuenteId(15L);

        this.buildPrueba = new Build();
        this.buildPrueba.setId(100L);
        this.buildPrueba.setUsuarioId(1L);
        this.buildPrueba.setCpuId(10L);
        this.buildPrueba.setGpuId(20L);
        this.buildPrueba.setMotherboardId(5L);
        this.buildPrueba.setRamId(8L);
        this.buildPrueba.setFuenteId(15L);
        this.buildPrueba.setEstado("VALIDADA");

        for (int i = 0; i < 40; i++) {
            Build b = new Build();
            b.setId((long) (i + 1));
            b.setUsuarioId((long) (faker.number().numberBetween(1, 50)));
            b.setEstado(faker.options().option("BORRADOR", "VALIDADA", "INCOMPATIBLE"));
            buildList.add(b);
        }
    }

    @Test
    @DisplayName("Debe crear una Build exitosamente con validación de compatibilidad")
    public void shouldCreateBuildSuccessfully() {
        Map<String, Object> compatResponse = new HashMap<>();
        compatResponse.put("compatible", true);

        when(userClient.obtenerUsuario(1L)).thenReturn(new Object());
        when(repository.save(any(Build.class))).thenAnswer(inv -> {
            Build saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });
        when(compatibilityClient.validarBuild(anyMap())).thenReturn(compatResponse);

        Build result = buildService.crearBuild(buildDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getEstado()).isEqualTo("VALIDADA");

        verify(userClient, times(1)).obtenerUsuario(1L);
        verify(compatibilityClient, times(1)).validarBuild(anyMap());
        verify(repository, times(2)).save(any(Build.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    public void shouldThrowExceptionWhenUserDoesNotExist() {
        doThrow(new RuntimeException("User not found")).when(userClient).obtenerUsuario(999L);

        BuildDTO dtoInvalido = new BuildDTO();
        dtoInvalido.setUsuarioId(999L);

        assertThatThrownBy(() -> buildService.crearBuild(dtoInvalido))
                .isInstanceOf(BuildException.class)
                .hasMessageContaining("El usuario no existe");
    }

    @Test
    @DisplayName("Debe listar todas las Builds")
    public void shouldListAllBuilds() {
        when(repository.findAll()).thenReturn(buildList);

        List<Build> result = buildService.listarTodas();

        assertThat(result).hasSize(40);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar Build por ID exitosamente")
    public void shouldFindBuildById() {
        Long id = 100L;
        when(repository.findById(id)).thenReturn(Optional.of(buildPrueba));

        Build result = buildService.buscarPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar Build inexistente")
    public void shouldThrowExceptionWhenBuildNotFound() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildService.buscarPorId(id))
                .isInstanceOf(BuildException.class)
                .hasMessage("Build no encontrada con ID: 999");
    }

    @Test
    @DisplayName("Debe actualizar una Build y resetear estado a BORRADOR")
    public void shouldUpdateBuildAndResetToDraft() {
        Long id = 100L;
        BuildDTO updateDTO = new BuildDTO();
        updateDTO.setUsuarioId(1L);
        updateDTO.setCpuId(11L);
        updateDTO.setGpuId(21L);
        updateDTO.setMotherboardId(6L);
        updateDTO.setRamId(9L);
        updateDTO.setFuenteId(16L);

        when(repository.findById(id)).thenReturn(Optional.of(buildPrueba));
        when(repository.save(any(Build.class))).thenAnswer(inv -> inv.getArgument(0));

        Build result = buildService.actualizarBuild(id, updateDTO);

        assertThat(result.getEstado()).isEqualTo("BORRADOR");
        assertThat(result.getCpuId()).isEqualTo(11L);
        verify(repository, times(1)).save(any(Build.class));
    }

    @Test
    @DisplayName("Debe eliminar una Build en estado BORRADOR")
    public void shouldDeleteDraftBuild() {
        Long id = 50L;
        Build borrador = new Build();
        borrador.setId(id);
        borrador.setEstado("BORRADOR");

        when(repository.findById(id)).thenReturn(Optional.of(borrador));

        buildService.eliminarBuildBorrador(id);

        verify(repository, times(1)).delete(borrador);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar Build que no está en BORRADOR")
    public void shouldThrowExceptionWhenDeletingNonDraftBuild() {
        Long id = 100L;
        when(repository.findById(id)).thenReturn(Optional.of(buildPrueba));

        assertThatThrownBy(() -> buildService.eliminarBuildBorrador(id))
                .isInstanceOf(BuildException.class)
                .hasMessageContaining("Solo se pueden eliminar o archivar builds en estado BORRADOR");
    }
}