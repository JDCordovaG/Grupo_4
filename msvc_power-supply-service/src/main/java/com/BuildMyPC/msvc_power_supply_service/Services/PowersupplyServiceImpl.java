package com.BuildMyPC.msvc_power_supply_service.Services;

import com.BuildMyPC.msvc_power_supply_service.Clients.ComponentClient;
import com.BuildMyPC.msvc_power_supply_service.Exceptions.PowersupplyException;
import com.BuildMyPC.msvc_power_supply_service.Models.Dtos.PowersupplyDTO;
import com.BuildMyPC.msvc_power_supply_service.Models.Powersupply;
import com.BuildMyPC.msvc_power_supply_service.Repositories.PowersupplyRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PowersupplyServiceImpl implements PowersupplyService {

    private static final Logger log = LoggerFactory.getLogger(PowersupplyServiceImpl.class);
    private final PowersupplyRepository repository;
    private final ComponentClient componentClient;

    public PowersupplyServiceImpl(PowersupplyRepository repository, ComponentClient componentClient) {
        this.repository = repository;
        this.componentClient = componentClient;
    }

    @Override
    @Transactional
    public PowersupplyDTO crearFuentePoder(PowersupplyDTO dto) {
        log.info("Validando integridad de Fuente de Poder con Component-Service...");

        try {
            componentClient.obtenerComponentePorId(dto.getComponenteId());
        } catch (FeignException e) {
            throw new PowersupplyException("Denegado: La fuente de poder requiere un ID de componente base válido y existente.");
        }

        Powersupply fuente = mapToEntity(dto);
        fuente.setActivo(true);
        return mapToDTO(repository.save(fuente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PowersupplyDTO> listarTodas() {
        log.info("Consultando catálogo de fuentes de poder activas");
        return repository.findByActivoTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PowersupplyDTO buscarPorId(Long id) {
        log.info("Buscando fuente de poder con ID: {}", id);
        return repository.findByIdAndActivoTrue(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new PowersupplyException("Fuente de poder no encontrada o inactiva con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PowersupplyDTO> buscarPorCertificacion(String certificacion) {
        log.info("Filtrando fuentes de poder por certificación: {}", certificacion);
        return repository.findByCertificacionIgnoreCaseAndActivoTrue(certificacion).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PowersupplyDTO actualizarFuentePoder(Long id, PowersupplyDTO dto) {
        log.info("Actualizando especificaciones técnicas de la Fuente de Poder ID: {}", id);
        Powersupply fuente = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new PowersupplyException("Imposible actualizar: Fuente de Poder inexistente"));

        fuente.setPotenciaWatts(dto.getPotenciaWatts());
        fuente.setCertificacion(dto.getCertificacion());
        fuente.setFormato(dto.getFormato());
        fuente.setEsModular(dto.getEsModular());

        return mapToDTO(repository.save(fuente));
    }

    @Override
    @Transactional
    public void desactivarFuentePoder(Long id) {
        log.warn("Procesando baja lógica de Fuente de Poder ID: {}", id);
        Powersupply fuente = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new PowersupplyException("Imposible dar de baja: Fuente de Poder no encontrada"));
        fuente.setActivo(false);
        repository.save(fuente);
        log.info("Fuente de Poder ID: {} marcada como INACTIVA correctamente", id);
    }

    private Powersupply mapToEntity(PowersupplyDTO dto) {
        Powersupply entity = new Powersupply();
        entity.setComponenteId(dto.getComponenteId());
        entity.setPotenciaWatts(dto.getPotenciaWatts());
        entity.setCertificacion(dto.getCertificacion());
        entity.setFormato(dto.getFormato());
        entity.setEsModular(dto.getEsModular());
        return entity;
    }

    private PowersupplyDTO mapToDTO(Powersupply entity) {
        PowersupplyDTO dto = new PowersupplyDTO();
        dto.setId(entity.getId());
        dto.setComponenteId(entity.getComponenteId());
        dto.setPotenciaWatts(entity.getPotenciaWatts());
        dto.setCertificacion(entity.getCertificacion());
        dto.setFormato(entity.getFormato());
        dto.setEsModular(entity.getEsModular());
        dto.setActivo(entity.getActivo());
        return dto;
    }
}