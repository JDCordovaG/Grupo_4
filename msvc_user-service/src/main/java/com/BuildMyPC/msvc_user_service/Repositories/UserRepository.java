package com.BuildMyPC.msvc_user_service.Repositories;

import com.BuildMyPC.msvc_user_service.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEstado(String estado);
}