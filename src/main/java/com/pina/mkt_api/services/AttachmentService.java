package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Attachment;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.AttachmentRepository;
import com.pina.mkt_api.repositories.CompanyRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public AttachmentService(AttachmentRepository attachmentRepository,
                             CompanyRepository companyRepository,
                             UserRepository userRepository,
                             SecurityUtils securityUtils) {
        this.attachmentRepository = attachmentRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    public Attachment upload(Long companyId, MultipartFile file) throws IOException {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        User uploader = userRepository.findByEmail(securityUtils.getAuthenticatedEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileSize(file.getSize());
        attachment.setContentType(file.getContentType());
        attachment.setFileData(file.getBytes());
        attachment.setCompany(company);
        attachment.setUploadedBy(uploader);

        return attachmentRepository.save(attachment);
    }

    public List<Attachment> findByCompany(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }
        return attachmentRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public Attachment findById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo não encontrado"));
    }

    public void delete(Long id) {
        attachmentRepository.delete(findById(id));
    }
}
