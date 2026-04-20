package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.enums.Role;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findByActiveTrue();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .filter(User::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public User register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email já está em uso.");
        }

        if (user.getRole() == null) {
            user.setRole(Role.FUNCIONARIO);
        }

        user.setActive(true);

        return userRepository.save(user);
    }

    public User createUser(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email já está em uso.");
        }

        if (user.getRole() == null) {
            throw new BusinessRuleException("Perfil do usuário é obrigatório.");
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {

        User user = findById(id);

        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().equals(user.getEmail()) &&
                userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email já está em uso.");
        }

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getPassword() != null) {
            user.setPassword(updatedUser.getPassword());
        }

        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
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