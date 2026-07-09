package com.BuildMyPC.msvc_motherboard_service.Services;

import com.BuildMyPC.msvc_motherboard_service.Clients.ComponentClient;
import com.BuildMyPC.msvc_motherboard_service.Exceptions.MotherboardException;
import com.BuildMyPC.msvc_motherboard_service.Models.Dtos.MotherboardDTO;
import com.BuildMyPC.msvc_motherboard_service.Models.Motherboard;
import com.BuildMyPC.msvc_motherboard_service.Repositories.MotherboardRepository;
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
 * Pruebas unitarias de {@link MotherboardServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class MotherboardServiceTests {

    @Mock
    private MotherboardRepository repository;

    @Mock
    private ComponentClient componentClient;

    @InjectMocks
    private MotherboardServiceImpl motherboardService;

    private MotherboardDTO motherboardDTOPrueba;
    private Motherboard motherboardPrueba;
    private List<Motherboard> motherboardList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.motherboardDTOPrueba = new MotherboardDTO();
        this.motherboardDTOPrueba.setComponenteId(300L);
        this.motherboardDTOPrueba.setSocket("AM4");
        this.motherboardDTOPrueba.setChipset("B550");
        this.motherboardDTOPrueba.setTipoRamSoportada("DDR4");
        this.motherboardDTOPrueba.setSlotsRam(4);
        this.motherboardDTOPrueba.setMaxRamGb(128);
        this.motherboardDTOPrueba.setFormato("ATX");
        this.motherboardDTOPrueba.setActivo(true);

        this.motherboardPrueba = new Motherboard();
        this.motherboardPrueba.setId(1L);
        this.motherboardPrueba.setComponenteId(300L);
        this.motherboardPrueba.setSocket("AM4");
        this.motherboardPrueba.setChipset("B550");
        this.motherboardPrueba.setTipoRamSoportada("DDR4");
        this.motherboardPrueba.setSlotsRam(4);
        this.motherboardPrueba.setMaxRamGb(128);
        this.motherboardPrueba.setFormato("ATX");
        this.motherboardPrueba.setActivo(true);

        for (int i = 0; i < 20; i++) {
            Motherboard m = new Motherboard();
            m.setId((long) (i + 1));
            m.setSocket(faker.options().option("AM4", "LGA1700", "AM5"));
            m.setChipset(faker.options().option("B550", "Z690", "X670"));
            m.setFormato(faker.options().option("ATX", "Micro-ATX"));
            m.setActivo(true);
            motherboardList.add(m);
        }
    }

    @Test
    @DisplayName("Debe crear placa madre exitosamente")
    public void shouldCreateMotherboardSuccessfully() {
        doNothing().when(componentClient).obtenerComponentePorId(anyLong());

        when(repository.save(any(Motherboard.class))).thenAnswer(inv -> {
            Motherboard saved = inv.getArgument(0);
            saved.setId(400L);
            return saved;
        });

        MotherboardDTO result = motherboardService.crearPlacamadre(motherboardDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(400L);
        assertThat(result.getSocket()).isEqualTo("AM4");
        verify(componentClient, times(1)).obtenerComponentePorId(300L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el componente base no existe")
    public void shouldThrowExceptionWhenComponentNotFound() {
        doThrow(new RuntimeException("Componente no encontrado"))
                .when(componentClient).obtenerComponentePorId(999L);

        MotherboardDTO dtoInvalido = new MotherboardDTO();
        dtoInvalido.setComponenteId(999L);
        dtoInvalido.setSocket("AM4");

        assertThatThrownBy(() -> motherboardService.crearPlacamadre(dtoInvalido))
                .isInstanceOf(MotherboardException.class)
                .hasMessageContaining("Fallo de integridad relacional");
    }

    @Test
    @DisplayName("Debe listar todas las placas madre activas")
    public void shouldListAllActiveMotherboards() {
        when(repository.findByActivoTrue()).thenReturn(motherboardList);

        List<MotherboardDTO> result = motherboardService.listarTodas();

        assertThat(result).hasSize(20);
    }

    @Test
    @DisplayName("Debe buscar placa madre por ID")
    public void shouldFindMotherboardById() {
        Long id = 1L;
        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(motherboardPrueba));

        MotherboardDTO result = motherboardService.buscarPorId(id);

        assertThat(result.getSocket()).isEqualTo("AM4");
    }

    @Test
    @DisplayName("Debe buscar placas madre por socket")
    public void shouldFindMotherboardsBySocket() {
        String socket = "AM4";
        when(repository.findBySocketIgnoreCaseAndActivoTrue(socket))
                .thenReturn(motherboardList.subList(0, 8));

        List<MotherboardDTO> result = motherboardService.buscarPorSocket(socket);

        assertThat(result).hasSize(8);
    }

    @Test
    @DisplayName("Debe actualizar placa madre")
    public void shouldUpdateMotherboard() {
        Long id = 1L;
        MotherboardDTO updateDTO = new MotherboardDTO();
        updateDTO.setSocket("AM5");
        updateDTO.setChipset("X670");

        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(motherboardPrueba));
        when(repository.save(any(Motherboard.class))).thenAnswer(inv -> inv.getArgument(0));

        MotherboardDTO result = motherboardService.actualizarPlacamadre(id, updateDTO);

        assertThat(result.getSocket()).isEqualTo("AM5");
        assertThat(result.getChipset()).isEqualTo("X670");
    }

    @Test
    @DisplayName("Debe desactivar placa madre")
    public void shouldDeactivateMotherboard() {
        Long id = 1L;
        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(motherboardPrueba));

        motherboardService.desactivarPlacamadre(id);

        assertThat(motherboardPrueba.getActivo()).isFalse();
        verify(repository, times(1)).save(motherboardPrueba);
    }
}