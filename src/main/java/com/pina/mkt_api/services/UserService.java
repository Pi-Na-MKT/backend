package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.UserDTOs.UserRequestDTO;
import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.RoleRepository;
import com.pina.mkt_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findByIsActiveTrue();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
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
        user.setPassword(request.password());

        user.setPhone(request.phone());
        user.setJobTitle(request.jobTitle());
        user.setDepartment(request.department());
        user.setSeniority(request.seniority());

        user.setResponsibility(request.responsibility());
        user.setBio(request.bio());
        user.setLinkedin(request.linkedin());

        Role role = roleRepository
                .findByName(request.role() != null ? request.role().toUpperCase() : "USER")
                .orElseGet(() -> roleRepository.findByName("USER")
                        .orElseThrow(() -> new RuntimeException("Cadastre a role USER no banco")));

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

        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser, Long roleId) {
        User user = findById(id);

        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().equals(user.getEmail()) &&
                userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email já está em uso.");
        }

        if (updatedUser.getName() != null) user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null) user.setPassword(updatedUser.getPassword());
        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado."));
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("E-mail não encontrado."));

        if (!user.getPassword().equals(password)) {
            throw new BusinessRuleException("Senha incorreta.");
        }

        if (!user.getActive()) {
            throw new BusinessRuleException("Usuário desativado.");
        }

        return user;
    }
}