package com.BuildMyPC.msvc_auth_service.Services;

import com.BuildMyPC.msvc_auth_service.Exceptions.AuthException;
import com.BuildMyPC.msvc_auth_service.Models.Auth;
import com.BuildMyPC.msvc_auth_service.Models.Dtos.AuthDTO;
import com.BuildMyPC.msvc_auth_service.Repositories.AuthRepository;
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
public class AuthServiceTests {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthService authService;

    private Auth authPrueba;
    private AuthDTO authDTOPrueba;
    private List<Auth> authList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        this.authPrueba = new Auth();
        this.authPrueba.setId(1L);
        this.authPrueba.setEmail("test@buildmypc.com");
        this.authPrueba.setPasswordHash("$2a$10$exampleHash123456789");
        this.authPrueba.setRol("USER");
        this.authPrueba.setEstado("ACTIVO");

        this.authDTOPrueba = new AuthDTO();
        this.authDTOPrueba.setEmail("test@buildmypc.com");
        this.authDTOPrueba.setPasswordHash("$2a$10$exampleHash123456789");
        this.authDTOPrueba.setRol("USER");

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Auth auth = new Auth();
            auth.setId((long) (i + 1));
            auth.setEmail(faker.internet().emailAddress());
            auth.setPasswordHash("$2a$10$fakeHash" + i);
            auth.setRol(faker.options().option("USER", "ADMIN", "TECNICO"));
            auth.setEstado("ACTIVO");
            authList.add(auth);
        }
    }

    @Test
    @DisplayName("Debe listar todas las auths")
    public void shouldListAllAuths() {
        when(this.authRepository.findAll()).thenReturn(this.authList);

        List<Auth> result = this.authService.listarTodas();

        assertThat(result).hasSize(50);
        verify(this.authRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar una auth por ID exitosamente")
    public void shouldFindAuthById() {
        Long id = 1L;
        when(this.authRepository.findById(id)).thenReturn(Optional.of(this.authPrueba));

        Auth result = this.authService.buscarPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@buildmypc.com");
        verify(this.authRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar auth por ID inexistente")
    public void shouldThrowExceptionWhenAuthByIdNotFound() {
        Long id = 999L;
        when(this.authRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authService.buscarPorId(id))
                .isInstanceOf(AuthException.class)
                .hasMessage("Auth no encontrada con ID: 999");
        verify(this.authRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe buscar una auth por email exitosamente")
    public void shouldFindAuthByEmail() {
        String email = "test@buildmypc.com";
        when(this.authRepository.findByEmail(email)).thenReturn(Optional.of(this.authPrueba));

        Auth result = this.authService.buscarPorEmail(email);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(this.authRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar auth por email inexistente")
    public void shouldThrowExceptionWhenAuthByEmailNotFound() {
        String email = "noexiste@buildmypc.com";
        when(this.authRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authService.buscarPorEmail(email))
                .isInstanceOf(AuthException.class)
                .hasMessage("Auth no encontrada con email: " + email);
        verify(this.authRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Debe crear una auth exitosamente cuando el email no existe")
    public void shouldCreateAuthSuccessfully() {
        when(this.authRepository.findByEmail(this.authDTOPrueba.getEmail())).thenReturn(Optional.empty());
        when(this.authRepository.save(any(Auth.class))).thenAnswer(inv -> {
            Auth saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        Auth result = this.authService.crearAuth(this.authDTOPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getEmail()).isEqualTo(this.authDTOPrueba.getEmail());
        verify(this.authRepository, times(1)).findByEmail(this.authDTOPrueba.getEmail());
        verify(this.authRepository, times(1)).save(any(Auth.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear auth con email duplicado")
    public void shouldThrowExceptionWhenCreatingAuthWithDuplicateEmail() {
        when(this.authRepository.findByEmail(this.authDTOPrueba.getEmail()))
                .thenReturn(Optional.of(this.authPrueba));

        assertThatThrownBy(() -> this.authService.crearAuth(this.authDTOPrueba))
                .isInstanceOf(AuthException.class)
                .hasMessage("Ya existe una cuenta asociada a este correo.");
        verify(this.authRepository, never()).save(any(Auth.class));
    }

    @Test
    @DisplayName("Debe actualizar una auth exitosamente")
    public void shouldUpdateAuthSuccessfully() {
        Long id = 1L;
        AuthDTO dtoUpdate = new AuthDTO();
        dtoUpdate.setEmail("nuevo@buildmypc.com");
        dtoUpdate.setPasswordHash("$2a$10$newHash123");
        dtoUpdate.setRol("ADMIN");

        when(this.authRepository.findById(id)).thenReturn(Optional.of(this.authPrueba));
        when(this.authRepository.findByEmail(dtoUpdate.getEmail())).thenReturn(Optional.empty());
        when(this.authRepository.save(any(Auth.class))).thenAnswer(inv -> inv.getArgument(0));

        Auth result = this.authService.actualizarAuth(id, dtoUpdate);

        assertThat(result.getEmail()).isEqualTo("nuevo@buildmypc.com");
        assertThat(result.getRol()).isEqualTo("ADMIN");
        verify(this.authRepository, times(1)).findById(id);
        verify(this.authRepository, times(1)).save(any(Auth.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar con email ya en uso por otra cuenta")
    public void shouldThrowExceptionWhenUpdatingToDuplicateEmail() {
        Long id = 1L;
        AuthDTO dtoUpdate = new AuthDTO();
        dtoUpdate.setEmail("otro@buildmypc.com");
        dtoUpdate.setPasswordHash("hash");
        dtoUpdate.setRol("USER");

        Auth otraAuth = new Auth();
        otraAuth.setId(2L);
        otraAuth.setEmail("otro@buildmypc.com");

        when(this.authRepository.findById(id)).thenReturn(Optional.of(this.authPrueba));
        when(this.authRepository.findByEmail(dtoUpdate.getEmail())).thenReturn(Optional.of(otraAuth));

        assertThatThrownBy(() -> this.authService.actualizarAuth(id, dtoUpdate))
                .isInstanceOf(AuthException.class)
                .hasMessage("El nuevo correo ya está en uso por otra cuenta.");
    }

    @Test
    @DisplayName("Debe desactivar una auth exitosamente")
    public void shouldDeactivateAuthSuccessfully() {
        Long id = 1L;
        when(this.authRepository.findById(id)).thenReturn(Optional.of(this.authPrueba));
        when(this.authRepository.save(any(Auth.class))).thenAnswer(inv -> inv.getArgument(0));

        Auth result = this.authService.desactivarAuth(id);

        assertThat(result.getEstado()).isEqualTo("INACTIVO");
        verify(this.authRepository, times(1)).findById(id);
        verify(this.authRepository, times(1)).save(any(Auth.class));
    }
}