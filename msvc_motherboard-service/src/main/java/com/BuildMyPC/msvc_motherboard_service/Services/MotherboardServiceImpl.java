package com.BuildMyPC.msvc_motherboard_service.Services;

import com.BuildMyPC.msvc_motherboard_service.Clients.ComponentClient;
import com.BuildMyPC.msvc_motherboard_service.Exceptions.MotherboardException;
import com.BuildMyPC.msvc_motherboard_service.Models.Dtos.MotherboardDTO;
import com.BuildMyPC.msvc_motherboard_service.Models.Motherboard;
import com.BuildMyPC.msvc_motherboard_service.Repositories.MotherboardRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MotherboardServiceImpl implements MotherboardService {

    private static final Logger log = LoggerFactory.getLogger(MotherboardServiceImpl.class);
    private final MotherboardRepository repository;
    private final ComponentClient componentClient;

    public MotherboardServiceImpl(MotherboardRepository repository, ComponentClient componentClient) {
        this.repository = repository;
        this.componentClient = componentClient;
    }

    @Override
    @Transactional
    public MotherboardDTO crearPlacamadre(MotherboardDTO dto) {
        log.info("Sondeando Component-Service para Placa Madre con ID: {}", dto.getComponenteId());

        try {
            componentClient.obtenerComponentePorId(dto.getComponenteId());
        } catch (FeignException e) {
            throw new MotherboardException("No se puede registrar las especificaciones. El componente base ID: " + dto.getComponenteId() + " no fue encontrado.");
        }

        Motherboard board = mapToEntity(dto);
        board.setActivo(true);
        return mapToDTO(repository.save(board));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotherboardDTO> listarTodas() {
        log.info("Consultando listado de placas madre activas");
        return repository.findByActivoTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MotherboardDTO buscarPorId(Long id) {
        log.info("Buscando placa madre con ID: {}", id);
        return repository.findByIdAndActivoTrue(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new MotherboardException("Placa madre no encontrada o descontinuada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotherboardDTO> buscarPorSocket(String socket) {
        log.info("Filtrando placas madre compatibles con el socket: {}", socket);
        return repository.findBySocketIgnoreCaseAndActivoTrue(socket).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MotherboardDTO actualizarPlacamadre(Long id, MotherboardDTO dto) {
        log.info("Actualizando especificaciones técnicas de Placa Madre ID: {}", id);
        Motherboard board = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new MotherboardException("Imposible actualizar: Placa madre inexistente"));

        board.setSocket(dto.getSocket());
        board.setChipset(dto.getChipset());
        board.setTipoRamSoportada(dto.getTipoRamSoportada());
        board.setSlotsRam(dto.getSlotsRam());
        board.setMaxRamGb(dto.getMaxRamGb());
        board.setFormato(dto.getFormato());

        return mapToDTO(repository.save(board));
    }

    @Override
    @Transactional
    public void desactivarPlacamadre(Long id) {
        log.warn("Procesando baja lógica de Placa Madre ID: {}", id);
        Motherboard board = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new MotherboardException("Imposible dar de baja: Placa madre no encontrada"));
        board.setActivo(false);
        repository.save(board);
        log.info("Placa madre ID: {} marcada como INACTIVA", id);
    }

    private Motherboard mapToEntity(MotherboardDTO dto) {
        Motherboard entity = new Motherboard();
        entity.setComponenteId(dto.getComponenteId());
        entity.setSocket(dto.getSocket());
        entity.setChipset(dto.getChipset());
        entity.setTipoRamSoportada(dto.getTipoRamSoportada());
        entity.setSlotsRam(dto.getSlotsRam());
        entity.setMaxRamGb(dto.getMaxRamGb());
        entity.setFormato(dto.getFormato());
        return entity;
    }

    private MotherboardDTO mapToDTO(Motherboard entity) {
        MotherboardDTO dto = new MotherboardDTO();
        dto.setId(entity.getId());
        dto.setComponenteId(entity.getComponenteId());
        dto.setSocket(entity.getSocket());
        dto.setChipset(entity.getChipset());
        dto.setTipoRamSoportada(entity.getTipoRamSoportada());
        dto.setSlotsRam(entity.getSlotsRam());
        dto.setMaxRamGb(entity.getMaxRamGb());
        dto.setFormato(entity.getFormato());
        dto.setActivo(entity.getActivo());
        return dto;
    }
}