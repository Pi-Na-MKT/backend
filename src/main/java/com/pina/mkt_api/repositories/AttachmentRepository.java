package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
