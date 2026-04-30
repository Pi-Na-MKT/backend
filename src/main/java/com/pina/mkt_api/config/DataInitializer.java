package com.pina.mkt_api.config;

import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {

            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(new Role(
                        "USER",
                        "ROLE_USER",
                        "Usuário padrão do sistema"
                ));
            }

            if (roleRepository.findByName("GESTOR").isEmpty()) {
                roleRepository.save(new Role(
                        "GESTOR",
                        "ROLE_MANAGER",
                        "Gestor de equipe"
                ));
            }

            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(new Role(
                        "ADMIN",
                        "ROLE_ADMIN",
                        "Administrador do sistema"
                ));
            }
        };
    }
}