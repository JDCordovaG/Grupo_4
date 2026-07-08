package com.BuildMyPC.msvc_quotation_service.Repositories;

import com.BuildMyPC.msvc_quotation_service.Models.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByEstado(String estado);
    List<Quotation> findByUsuarioId(Long usuarioId);
}