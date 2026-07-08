package com.BuildMyPC.msvc_quotation_service.Clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "msvc-compatibility", url = "localhost:8088/api/v1/compatibility")
public interface CompatibilityClient {
    @GetMapping("/build/{buildId}")
    Map<String, Object> obtenerEstadoCompatibilidad(@PathVariable("buildId") Long buildId);
}