package com.BuildMyPC.msvc_user_service.Services;

import com.BuildMyPC.msvc_user_service.Models.Dtos.UserDTO;
import com.BuildMyPC.msvc_user_service.Models.User;
import java.util.List;

public interface UserService {
    User crearUser(UserDTO dto);
    List<User> listarTodas();
    User buscarPorId(Long id);
    User desactivarUser(Long id);
}