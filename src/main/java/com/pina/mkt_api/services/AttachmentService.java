package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Attachment;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.AttachmentRepository;
import com.pina.mkt_api.repositories.CompanyRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class AttachmentService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp", "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain", "text/csv");

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB

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
        validateFile(file);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        User uploader = userRepository.findByEmail(securityUtils.getAuthenticatedEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Attachment attachment = new Attachment();
        attachment.setFileName(StringUtils.getFilename(file.getOriginalFilename()));
        attachment.setFileSize(file.getSize());
        attachment.setContentType(file.getContentType());
        attachment.setFileData(file.getBytes());
        attachment.setCompany(company);
        attachment.setUploadedBy(uploader);

        return attachmentRepository.save(attachment);
    }

    // Valida tipo e tamanho do arquivo antes de persistir (OWASP A04 - Insecure Design).
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Arquivo vazio ou ausente.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessRuleException("Arquivo excede o tamanho máximo permitido de 10MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessRuleException("Tipo de arquivo não permitido: " + contentType);
        }
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
