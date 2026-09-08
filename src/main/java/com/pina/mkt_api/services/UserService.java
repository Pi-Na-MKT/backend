package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.UserDTOs.PasswordChangeDTO;
import com.pina.mkt_api.dtos.UserDTOs.UserRequestDTO;
import com.pina.mkt_api.dtos.UserDTOs.UserUpdateDTO;
import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.repositories.CardRepository;
import com.pina.mkt_api.repositories.RoleRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       BoardRepository boardRepository, CardRepository cardRepository,
                       PasswordEncoder passwordEncoder, SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
    }

    public List<User> findAllUsers() {
        if (securityUtils.isAdmin()) {
            return userRepository.findByIsActiveTrue();
        }
        return userRepository.findMembersInSharedBoards(securityUtils.getAuthenticatedEmail());
    }

    public User findById(Long id) {
        User user = userRepository.findById(id)
                .filter(User::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!securityUtils.isAdminOrManager() && !isOwnProfile(id, securityUtils.getAuthenticatedEmail())) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        return user;
    }

    public User findMe(String email) {
        return userRepository.findByEmail(email)
                .filter(User::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public User register(UserRequestDTO request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessRuleException("Este e-mail já está em uso.");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setJobTitle(request.jobTitle());
        user.setSeniority(request.seniority());
        user.setResponsibility(request.responsibility());
        user.setBio(request.bio());
        user.setLinkedin(request.linkedin());

        // Auto-registro sempre recebe o papel padrão USER. O papel não pode ser
        // definido pelo cliente, evitando escalação de privilégio (OWASP A01).
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Cadastre a role USER no banco"));

        user.setRole(role);
        return userRepository.save(user);
    }

    public User createUser(User user, Long roleId) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email já está em uso.");
        }

        if (roleId == null) {
            throw new BusinessRuleException("O ID do perfil (Role) é obrigatório para criação administrativa.");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado com o ID fornecido."));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserUpdateDTO dto) {
        User user = findById(id);

        if (dto.name() != null) user.setName(dto.name());
        if (dto.phone() != null) user.setPhone(dto.phone());
        if (dto.jobTitle() != null) user.setJobTitle(dto.jobTitle());
        if (dto.seniority() != null) user.setSeniority(dto.seniority());
        if (dto.bio() != null) user.setBio(dto.bio());
        if (dto.responsibility() != null) user.setResponsibility(dto.responsibility());
        if (dto.linkedin() != null) user.setLinkedin(dto.linkedin());
        if (dto.avatarUrl() != null) user.setAvatarUrl(dto.avatarUrl());

        return userRepository.save(user);
    }

    public void changePassword(String email, PasswordChangeDTO dto) {
        User user = userRepository.findByEmail(email)
                .filter(User::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessRuleException("Senha atual incorreta.");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    public User changeRole(Long id, Long roleId) {
        User user = userRepository.findById(id)
                .filter(User::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado com o ID: " + roleId));
        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        boardRepository.removeUserFromAllBoards(id);
        cardRepository.removeUserFromAllCards(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("E-mail não encontrado."));

        if (!user.getActive()) {
            throw new BusinessRuleException("Usuário desativado.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessRuleException("Senha incorreta.");
        }

        return user;
    }

    public boolean isOwnProfile(Long id, String email) {
        return userRepository.findByEmail(email)
                .map(u -> u.getId().equals(id))
                .orElse(false);
    }
}
