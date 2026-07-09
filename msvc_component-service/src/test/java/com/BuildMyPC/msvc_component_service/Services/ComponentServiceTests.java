package com.BuildMyPC.msvc_component_service.Services;

import com.BuildMyPC.msvc_component_service.Exceptions.ComponentException;
import com.BuildMyPC.msvc_component_service.Models.Component;
import com.BuildMyPC.msvc_component_service.Models.Dtos.ComponentDTO;
import com.BuildMyPC.msvc_component_service.Repositories.ComponentRepository;
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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentServiceTests {

    @Mock
    private ComponentRepository repository;

    @InjectMocks
    private ComponentService componentService;

    private Component componentPrueba;
    private ComponentDTO componentDTOPrueba;
    private List<Component> componentList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.componentDTOPrueba = new ComponentDTO();
        this.componentDTOPrueba.setTipo("CPU");
        this.componentDTOPrueba.setMarca("AMD");
        this.componentDTOPrueba.setModelo("Ryzen 7 5800X");
        this.componentDTOPrueba.setPrecioBase(450);
        this.componentDTOPrueba.setDescripcion("Procesador 8 núcleos alto rendimiento");
        this.componentDTOPrueba.setFechaLanzamiento(LocalDate.of(2020, 11, 5));

        this.componentPrueba = new Component();
        this.componentPrueba.setId(1L);
        this.componentPrueba.setTipo("CPU");
        this.componentPrueba.setMarca("AMD");
        this.componentPrueba.setModelo("Ryzen 7 5800X");
        this.componentPrueba.setPrecioBase(450);
        this.componentPrueba.setEstado("ACTIVO");
        this.componentPrueba.setDescripcion("Procesador 8 núcleos alto rendimiento");

        for (int i = 0; i < 35; i++) {
            Component c = new Component();
            c.setId((long) (i + 1));
            c.setTipo(faker.options().option("CPU", "GPU", "RAM", "MOTHERBOARD"));
            c.setMarca(faker.company().name());
            c.setModelo(faker.commerce().productName());
            c.setPrecioBase(faker.number().numberBetween(50, 1500));
            c.setEstado(faker.options().option("ACTIVO", "DESCONTINUADO"));
            componentList.add(c);
        }
    }

    @Test
    @DisplayName("Debe crear un componente exitosamente")
    public void shouldCreateComponentSuccessfully() {
        when(repository.save(any(Component.class))).thenAnswer(inv -> {
            Component saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        Component result = componentService.crearComponent(componentDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getTipo()).isEqualTo("CPU");
        verify(repository, times(1)).save(any(Component.class));
    }

    @Test
    @DisplayName("Debe listar todos los componentes")
    public void shouldListAllComponents() {
        when(repository.findAll()).thenReturn(componentList);

        List<Component> result = componentService.listarTodas();

        assertThat(result).hasSize(35);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar componente por ID")
    public void shouldFindComponentById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(componentPrueba));

        Component result = componentService.buscarPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getMarca()).isEqualTo("AMD");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar componente inexistente")
    public void shouldThrowExceptionWhenComponentNotFound() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> componentService.buscarPorId(id))
                .isInstanceOf(ComponentException.class)
                .hasMessage("Componente no encontrado con ID: 999");
    }

    @Test
    @DisplayName("Debe buscar componentes por tipo")
    public void shouldFindComponentsByTipo() {
        String tipo = "CPU";
        List<Component> cpus = componentList.stream()
                .filter(c -> "CPU".equals(c.getTipo()))
                .toList();

        when(repository.findByTipo("CPU")).thenReturn(cpus);

        List<Component> result = componentService.buscarPorTipo(tipo);

        assertThat(result).isNotEmpty();
        verify(repository, times(1)).findByTipo("CPU");
    }

    @Test
    @DisplayName("Debe buscar componentes por marca")
    public void shouldFindComponentsByMarca() {
        String marca = "AMD";
        when(repository.findByMarca(marca)).thenReturn(componentList.subList(0, 10));

        List<Component> result = componentService.buscarPorMarca(marca);

        assertThat(result).hasSize(10);
        verify(repository, times(1)).findByMarca(marca);
    }

    @Test
    @DisplayName("Debe buscar componentes por estado")
    public void shouldFindComponentsByEstado() {
        String estado = "ACTIVO";
        when(repository.findByEstado(estado)).thenReturn(componentList.subList(0, 20));

        List<Component> result = componentService.buscarPorEstado(estado);

        assertThat(result).hasSize(20);
    }

    @Test
    @DisplayName("Debe actualizar precio y descripción de un componente")
    public void shouldUpdateComponent() {
        Long id = 1L;
        ComponentDTO updateDTO = new ComponentDTO();
        updateDTO.setPrecioBase(520);
        updateDTO.setDescripcion("Nueva descripción actualizada");

        when(repository.findById(id)).thenReturn(Optional.of(componentPrueba));
        when(repository.save(any(Component.class))).thenAnswer(inv -> inv.getArgument(0));

        Component result = componentService.actualizarComponent(id, updateDTO);

        assertThat(result.getPrecioBase()).isEqualTo(520);
        assertThat(result.getDescripcion()).contains("actualizada");
        verify(repository, times(1)).save(any(Component.class));
    }

    @Test
    @DisplayName("Debe desactivar (descontinuar) un componente")
    public void shouldDeactivateComponent() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(componentPrueba));
        when(repository.save(any(Component.class))).thenAnswer(inv -> inv.getArgument(0));

        Component result = componentService.desactivarComponent(id);

        assertThat(result.getEstado()).isEqualTo("DESCONTINUADO");
        verify(repository, times(1)).save(any(Component.class));
    }
}