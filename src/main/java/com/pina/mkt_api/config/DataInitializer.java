package com.pina.mkt_api.config;

import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.repositories.RoleRepository;
import com.pina.mkt_api.repositories.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository  = roleRepository;
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role admin = roleRepository.findByName("ADMIN").orElseGet(() ->
            roleRepository.save(new Role("ADMIN", "ROLE_ADMIN", "Administrador do sistema"))
        );
        roleRepository.findByName("MANAGER").orElseGet(() ->
            roleRepository.save(new Role("MANAGER", "ROLE_MANAGER", "Gestor de projetos"))
        );
        roleRepository.findByName("USER").orElseGet(() ->
            roleRepository.save(new Role("USER", "ROLE_USER", "Usuário padrão"))
        );

        if (userRepository.findByEmail("admin@pina.com").isEmpty()) {
            User user = new User();
            user.setName("Administrador");
            user.setEmail("admin@pina.com");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole(admin);
            user.setActive(true);
            userRepository.save(user);
            System.out.println(">>> Admin criado: admin@pina.com / admin123");
        }
    }
}
