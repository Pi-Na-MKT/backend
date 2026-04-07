package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessRuleException("O email " + user.getEmail() + " já está em uso.");
        }

        return userRepository.save(user);
    }
}