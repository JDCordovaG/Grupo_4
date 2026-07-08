package com.BuildMyPC.msvc_auth_service.Clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-user-service")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    Object obtenerUsuario(@PathVariable Long id);

}