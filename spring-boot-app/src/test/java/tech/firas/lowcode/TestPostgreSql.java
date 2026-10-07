package tech.firas.lowcode;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class TestPostgreSql {

    static final String IMAGE = "postgres:17.11-bookworm";

    private static final PostgreSQLContainer CONTAINER =
            new PostgreSQLContainer(DockerImageName.parse(IMAGE))
                    .withDatabaseName("lowcode")
                    .withUsername("lowcode")
                    .withPassword("lowcode");

    static {
        CONTAINER.start();
    }

    private TestPostgreSql() {
    }

    public static String jdbcUrl() {
        return CONTAINER.getJdbcUrl();
    }

    public static String username() {
        return CONTAINER.getUsername();
    }

    public static String password() {
        return CONTAINER.getPassword();
    }
}
