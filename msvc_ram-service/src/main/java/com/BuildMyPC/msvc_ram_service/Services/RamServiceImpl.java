package com.BuildMyPC.msvc_ram_service.Services;

import com.BuildMyPC.msvc_ram_service.Exceptions.RamException;
import com.BuildMyPC.msvc_ram_service.Models.Dtos.RamDTO;
import com.BuildMyPC.msvc_ram_service.Models.Ram;
import com.BuildMyPC.msvc_ram_service.Repositories.RamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RamServiceImpl implements RamService {

    private static final Logger log = LoggerFactory.getLogger(RamServiceImpl.class);
    private final RamRepository repository;

    public RamServiceImpl(RamRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Ram crearRam(RamDTO dto) {
        log.info("Iniciando creación de memoria RAM con tipo DDR: {}", dto.getTipoDdr());
        Ram ram = new Ram();
        ram.setComponenteId(dto.getComponenteId());
        ram.setTipoDdr(dto.getTipoDdr());
        ram.setCapacidadGb(dto.getCapacidadGb());
        ram.setFrecuenciaMhz(dto.getFrecuenciaMhz());
        ram.setLatenciaCl(dto.getLatenciaCl());
        ram.setModulos(dto.getModulos());
        ram.setVoltaje(dto.getVoltaje());

        Ram guardada = repository.save(ram);
        log.info("Memoria RAM guardada exitosamente con ID: {}", guardada.getId());
        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ram> listarTodas() {
        log.info("Solicitando listado de todas las memorias RAM");
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Ram buscarPorId(Long id) {
        log.info("Buscando memoria RAM por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error: Memoria RAM no encontrada con ID: {}", id);
                    return new RamException("Memoria RAM no encontrada con ID: " + id);
                });
    }
}