package com.BuildMyPC.msvc_power_supply_service.Services;

import com.BuildMyPC.msvc_power_supply_service.Clients.ComponentClient;
import com.BuildMyPC.msvc_power_supply_service.Exceptions.PowersupplyException;
import com.BuildMyPC.msvc_power_supply_service.Models.Dtos.PowersupplyDTO;
import com.BuildMyPC.msvc_power_supply_service.Models.Powersupply;
import com.BuildMyPC.msvc_power_supply_service.Repositories.PowersupplyRepository;
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
 * Pruebas unitarias de {@link PowersupplyServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class PowerSupplyServiceTests {  // Nombre de clase ajustado al archivo

    @Mock
    private PowersupplyRepository repository;

    @Mock
    private ComponentClient componentClient;

    @InjectMocks
    private PowersupplyServiceImpl powerSupplyService;

    private PowersupplyDTO powersupplyDTOPrueba;
    private Powersupply powersupplyPrueba;
    private List<Powersupply> powersupplyList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.powersupplyDTOPrueba = new PowersupplyDTO();
        this.powersupplyDTOPrueba.setComponenteId(400L);
        this.powersupplyDTOPrueba.setPotenciaWatts(750);
        this.powersupplyDTOPrueba.setCertificacion("80 Plus Gold");
        this.powersupplyDTOPrueba.setFormato("ATX");
        this.powersupplyDTOPrueba.setEsModular(true);
        this.powersupplyDTOPrueba.setActivo(true);

        this.powersupplyPrueba = new Powersupply();
        this.powersupplyPrueba.setId(1L);
        this.powersupplyPrueba.setComponenteId(400L);
        this.powersupplyPrueba.setPotenciaWatts(750);
        this.powersupplyPrueba.setCertificacion("80 Plus Gold");
        this.powersupplyPrueba.setFormato("ATX");
        this.powersupplyPrueba.setEsModular(true);
        this.powersupplyPrueba.setActivo(true);

        for (int i = 0; i < 22; i++) {
            Powersupply p = new Powersupply();
            p.setId((long) (i + 1));
            p.setPotenciaWatts(faker.number().numberBetween(500, 1200));
            p.setCertificacion(faker.options().option("80 Plus Bronze", "80 Plus Gold", "80 Plus Platinum"));
            p.setActivo(true);
            powersupplyList.add(p);
        }
    }

    @Test
    @DisplayName("Debe crear fuente de poder exitosamente")
    public void shouldCreatePowerSupplySuccessfully() {
        doNothing().when(componentClient).obtenerComponentePorId(anyLong());

        when(repository.save(any(Powersupply.class))).thenAnswer(inv -> {
            Powersupply saved = inv.getArgument(0);
            saved.setId(500L);
            return saved;
        });

        PowersupplyDTO result = powerSupplyService.crearFuentePoder(powersupplyDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(500L);
        assertThat(result.getPotenciaWatts()).isEqualTo(750);
        verify(componentClient, times(1)).obtenerComponentePorId(400L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si componente base no existe")
    public void shouldThrowExceptionWhenComponentNotFound() {
        doThrow(new RuntimeException("Componente no encontrado"))
                .when(componentClient).obtenerComponentePorId(999L);

        PowersupplyDTO dtoInvalido = new PowersupplyDTO();
        dtoInvalido.setComponenteId(999L);
        dtoInvalido.setPotenciaWatts(650);

        assertThatThrownBy(() -> powerSupplyService.crearFuentePoder(dtoInvalido))
                .isInstanceOf(PowersupplyException.class)
                .hasMessageContaining("Denegado");
    }

    @Test
    @DisplayName("Debe listar todas las fuentes de poder activas")
    public void shouldListAllActivePowerSupplies() {
        when(repository.findByActivoTrue()).thenReturn(powersupplyList);

        List<PowersupplyDTO> result = powerSupplyService.listarTodas();

        assertThat(result).hasSize(22);
    }

    @Test
    @DisplayName("Debe buscar fuente por ID")
    public void shouldFindPowerSupplyById() {
        Long id = 1L;
        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(powersupplyPrueba));

        PowersupplyDTO result = powerSupplyService.buscarPorId(id);

        assertThat(result.getCertificacion()).isEqualTo("80 Plus Gold");
    }

    @Test
    @DisplayName("Debe buscar fuentes por certificación")
    public void shouldFindPowerSuppliesByCertification() {
        String cert = "80 Plus Gold";
        when(repository.findByCertificacionIgnoreCaseAndActivoTrue(cert))
                .thenReturn(powersupplyList.subList(0, 10));

        List<PowersupplyDTO> result = powerSupplyService.buscarPorCertificacion(cert);

        assertThat(result).hasSize(10);
    }

    @Test
    @DisplayName("Debe actualizar fuente de poder")
    public void shouldUpdatePowerSupply() {
        Long id = 1L;
        PowersupplyDTO updateDTO = new PowersupplyDTO();
        updateDTO.setPotenciaWatts(850);
        updateDTO.setCertificacion("80 Plus Platinum");

        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(powersupplyPrueba));
        when(repository.save(any(Powersupply.class))).thenAnswer(inv -> inv.getArgument(0));

        PowersupplyDTO result = powerSupplyService.actualizarFuentePoder(id, updateDTO);

        assertThat(result.getPotenciaWatts()).isEqualTo(850);
        assertThat(result.getCertificacion()).isEqualTo("80 Plus Platinum");
    }

    @Test
    @DisplayName("Debe desactivar fuente de poder")
    public void shouldDeactivatePowerSupply() {
        Long id = 1L;
        when(repository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(powersupplyPrueba));

        powerSupplyService.desactivarFuentePoder(id);

        assertThat(powersupplyPrueba.getActivo()).isFalse();
        verify(repository, times(1)).save(powersupplyPrueba);
    }
}