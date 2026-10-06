package tech.firas.lowcode.accesscontrol.infrastructure.bootstrap;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessBootstrapConfiguration {

    @Bean
    ApplicationRunner accessBootstrapRunner(AccessBootstrap bootstrap) {
        return args -> bootstrap.ensureAdministratorExists();
    }
}
