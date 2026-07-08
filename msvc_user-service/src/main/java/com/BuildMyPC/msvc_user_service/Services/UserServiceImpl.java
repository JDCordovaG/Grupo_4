package com.BuildMyPC.msvc_user_service.Services;

import com.BuildMyPC.msvc_user_service.Exceptions.UserException;
import com.BuildMyPC.msvc_user_service.Models.Dtos.UserDTO;
import com.BuildMyPC.msvc_user_service.Models.User;
import com.BuildMyPC.msvc_user_service.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User crearUser(UserDTO dto) {
        log.info("Iniciando creación de Usuario con Email: {}", dto.getEmail());
        User user = new User();
        user.setNombre(dto.getNombre());
        user.setApellido(dto.getApellido());
        user.setEmail(dto.getEmail());
        user.setTelefono(dto.getTelefono());
        user.setRolFuncional(dto.getRolFuncional());
        user.setFechaRegistro(dto.getFechaRegistro());
        user.setEstado("ACTIVO");

        User guardado = repository.save(user);
        log.info("Usuario guardado exitosamente con ID: {}", guardado.getId());
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listarTodas() {
        log.info("Solicitando listado de todos los usuarios");
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User buscarPorId(Long id) {
        log.info("Buscando usuario por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error: Usuario no encontrado con ID: {}", id);
                    return new UserException("Usuario no encontrado con ID: " + id);
                });
    }

    @Override
    @Transactional
    public User desactivarUser(Long id) {
        log.info("Intentando desactivar Usuario con ID: {}", id);
        User user = buscarPorId(id);
        user.setEstado("INACTIVO");
        User guardado = repository.save(user);
        log.info("Usuario desactivado exitosamente con ID: {}", guardado.getId());
        return guardado;
    }
}