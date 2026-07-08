package com.BuildMyPC.msvc_ram_service.Repositories;

import com.BuildMyPC.msvc_ram_service.Models.Ram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RamRepository extends JpaRepository<Ram, Long> {
    List<Ram> findByComponenteId(Long componenteId);

}