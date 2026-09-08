package com.pina.mkt_api.config;

import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.repositories.RoleRepository;
import com.pina.mkt_api.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${admin.email:admin@pina.com}") String adminEmail,
                           @Value("${admin.password:}") String adminPassword) {
        this.roleRepository  = roleRepository;
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail      = adminEmail;
        this.adminPassword   = adminPassword;
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

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            // A senha do admin inicial não é fixa no código: vem da variável de ambiente
            // ADMIN_PASSWORD. Sem ela, a conta não é provisionada (OWASP A07).
            if (adminPassword == null || adminPassword.isBlank()) {
                log.warn("Administrador inicial não provisionado: defina a variável de ambiente ADMIN_PASSWORD para criar a conta {}.", adminEmail);
                return;
            }
            User user = new User();
            user.setName("Administrador");
            user.setEmail(adminEmail);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole(admin);
            user.setActive(true);
            userRepository.save(user);
            // Não logar credenciais em texto puro.
            log.info("Administrador inicial criado: {}", adminEmail);
        }
    }
}
