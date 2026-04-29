package com.pina.mkt_api.services;

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
                .filter(User::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email já está em uso.");
        }

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new BusinessRuleException("Cargo padrão 'USER' não configurado no banco de dados."));

        user.setRole(defaultRole);
        user.setIsActive(true);

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
        user.setIsActive(true);

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
        user.setIsActive(false);
        userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("E-mail não encontrado."));

        if (!user.getPassword().equals(password)) {
            throw new BusinessRuleException("Senha incorreta.");
        }

        if (!user.getIsActive()) {
            throw new BusinessRuleException("Usuário desativado.");
        }

        return user;
    }
}