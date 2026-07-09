package com.BuildMyPC.msvc_ram_service.Services;

import com.BuildMyPC.msvc_ram_service.Exceptions.RamException;
import com.BuildMyPC.msvc_ram_service.Models.Dtos.RamDTO;
import com.BuildMyPC.msvc_ram_service.Models.Ram;
import com.BuildMyPC.msvc_ram_service.Repositories.RamRepository;
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
 * Pruebas unitarias de {@link RamServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class RamServiceTests {

    @Mock
    private RamRepository repository;

    @InjectMocks
    private RamServiceImpl ramService;

    private RamDTO ramDTOPrueba;
    private Ram ramPrueba;
    private List<Ram> ramList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.ramDTOPrueba = new RamDTO();
        this.ramDTOPrueba.setComponenteId(500L);
        this.ramDTOPrueba.setTipoDdr("DDR5");
        this.ramDTOPrueba.setCapacidadGb(32);
        this.ramDTOPrueba.setFrecuenciaMhz(6000);
        this.ramDTOPrueba.setLatenciaCl(36.0);
        this.ramDTOPrueba.setModulos(2.0);
        this.ramDTOPrueba.setVoltaje(1);

        this.ramPrueba = new Ram();
        this.ramPrueba.setId(1L);
        this.ramPrueba.setComponenteId(500L);
        this.ramPrueba.setTipoDdr("DDR5");
        this.ramPrueba.setCapacidadGb(32);
        this.ramPrueba.setFrecuenciaMhz(6000);
        this.ramPrueba.setLatenciaCl(36.0);
        this.ramPrueba.setModulos(2.0);
        this.ramPrueba.setVoltaje(1);

        for (int i = 0; i < 28; i++) {
            Ram r = new Ram();
            r.setId((long) (i + 1));
            r.setTipoDdr(faker.options().option("DDR4", "DDR5"));
            r.setCapacidadGb(faker.number().numberBetween(8, 64));
            r.setFrecuenciaMhz(faker.number().numberBetween(3200, 8000));
            ramList.add(r);
        }
    }

    @Test
    @DisplayName("Debe crear RAM exitosamente")
    public void shouldCreateRamSuccessfully() {
        when(repository.save(any(Ram.class))).thenAnswer(inv -> {
            Ram saved = inv.getArgument(0);
            saved.setId(600L);
            return saved;
        });

        Ram result = ramService.crearRam(ramDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(600L);
        assertThat(result.getTipoDdr()).isEqualTo("DDR5");
        verify(repository, times(1)).save(any(Ram.class));
    }

    @Test
    @DisplayName("Debe listar todas las RAMs")
    public void shouldListAllRams() {
        when(repository.findAll()).thenReturn(ramList);

        List<Ram> result = ramService.listarTodas();

        assertThat(result).hasSize(28);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar RAM por ID")
    public void shouldFindRamById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(ramPrueba));

        Ram result = ramService.buscarPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getTipoDdr()).isEqualTo("DDR5");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar RAM inexistente")
    public void shouldThrowExceptionWhenRamNotFound() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ramService.buscarPorId(id))
                .isInstanceOf(RamException.class)
                .hasMessage("Memoria RAM no encontrada con ID: 999");
    }
}