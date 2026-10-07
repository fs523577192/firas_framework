package tech.firas.lowcode;

import java.util.List;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import tech.firas.lowcode.accesscontrol.application.AccessAdministrationService;
import tech.firas.lowcode.accesscontrol.domain.model.AccessPermissions;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

@SpringBootTest(
    classes = {
        IntegrationTest.TestConfig.class
    }
)
public abstract class IntegrationTest {

    protected static final String ADMINISTER_ACCESS = AccessPermissions.ADMINISTER;

    @Autowired
    private DataSource dataSource;

    @Autowired
    protected AccessAdministrationService administration;

    @BeforeEach
    void emptyTheAccessControlTables() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        List<String> tables = jdbc.queryForList(
                "SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename LIKE 'ac\\_%'",
                String.class);
        if (!tables.isEmpty()) {
            String quoted = tables.stream().map(table -> "\"" + table + "\"").reduce((a, b) -> a + ", " + b).orElseThrow();
            jdbc.execute("TRUNCATE TABLE " + quoted + " CASCADE");
        }
    }

    protected void givenAnAdministrator(String username, String password) {
        administration.registerUser(username, password);
        administration.createRole("super-admin", "Full control over access administration");
        administration.definePermission(PermissionCode.of(ADMINISTER_ACCESS), "Administer access control");
        administration.grantPermission("super-admin", PermissionCode.of(ADMINISTER_ACCESS));
        administration.assignRole(username, "super-admin");
    }

    protected void givenAUserHoldingNoRole(String username, String password) {
        administration.registerUser(username, password);
    }

    @Configuration
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackages = "tech.firas.lowcode.accesscontrol.domain.repository")
    @EntityScan(basePackages = "tech.firas.lowcode.accesscontrol.domain.model")
    @ComponentScan(basePackages = "tech.firas.lowcode.accesscontrol")
    static class TestConfig {
        
    }
}
