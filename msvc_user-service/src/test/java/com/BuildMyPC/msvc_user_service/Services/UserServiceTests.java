package com.BuildMyPC.msvc_user_service.Services;

import com.BuildMyPC.msvc_user_service.Exceptions.UserException;
import com.BuildMyPC.msvc_user_service.Models.Dtos.UserDTO;
import com.BuildMyPC.msvc_user_service.Models.User;
import com.BuildMyPC.msvc_user_service.Repositories.UserRepository;
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

/**
 * Pruebas unitarias de {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTOPrueba;
    private User userPrueba;
    private List<User> userList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        this.userDTOPrueba = new UserDTO();
        this.userDTOPrueba.setNombre("Juan");
        this.userDTOPrueba.setApellido("Pérez");
        this.userDTOPrueba.setEmail("juan.perez@buildmypc.com");
        this.userDTOPrueba.setTelefono("+56912345678");
        this.userDTOPrueba.setRolFuncional("USER");
        this.userDTOPrueba.setEstado("ACTIVO");
        this.userDTOPrueba.setFechaRegistro(LocalDate.now());

        this.userPrueba = new User();
        this.userPrueba.setId(1L);
        this.userPrueba.setNombre("Juan");
        this.userPrueba.setApellido("Pérez");
        this.userPrueba.setEmail("juan.perez@buildmypc.com");
        this.userPrueba.setTelefono("+56912345678");
        this.userPrueba.setRolFuncional("USER");
        this.userPrueba.setEstado("ACTIVO");
        this.userPrueba.setFechaRegistro(LocalDate.now());

        for (int i = 0; i < 25; i++) {
            User u = new User();
            u.setId((long) (i + 1));
            u.setNombre(faker.name().firstName());
            u.setApellido(faker.name().lastName());
            u.setEmail(faker.internet().emailAddress());
            u.setEstado(faker.options().option("ACTIVO", "INACTIVO"));
            userList.add(u);
        }
    }

    @Test
    @DisplayName("Debe crear usuario exitosamente")
    public void shouldCreateUserSuccessfully() {
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        User result = userService.crearUser(userDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getEmail()).isEqualTo("juan.perez@buildmypc.com");
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    public void shouldListAllUsers() {
        when(repository.findAll()).thenReturn(userList);

        List<User> result = userService.listarTodas();

        assertThat(result).hasSize(25);
    }

    @Test
    @DisplayName("Debe buscar usuario por ID")
    public void shouldFindUserById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(userPrueba));

        User result = userService.buscarPorId(id);

        assertThat(result.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar usuario inexistente")
    public void shouldThrowExceptionWhenUserNotFound() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.buscarPorId(id))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("Debe desactivar usuario correctamente")
    public void shouldDeactivateUser() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(userPrueba));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.desactivarUser(id);

        assertThat(result.getEstado()).isEqualTo("INACTIVO");
        verify(repository, times(1)).save(userPrueba);
    }
}