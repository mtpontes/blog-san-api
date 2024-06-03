package br.com.blogsanapi.integration;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class MySQLTestContainer implements BeforeAllCallback {

    private static GenericContainer<?> mysql;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
            mysql = new GenericContainer("mysql:8.0.36")
                .withExposedPorts(3306)
                .withEnv("MYSQL_ROOT_PASSWORD", "root");
            mysql.start();

            Integer mappedPort = mysql.getMappedPort(3306);
            System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
            System.setProperty("spring.datasource.url", "jdbc:mysql://localhost:" + mappedPort + "/" + context.getDisplayName() + "?createDatabaseIfNotExist=true");
            System.setProperty("spring.datasource.username", "root");
            System.setProperty("spring.datasource.password", "root");
            
            System.setProperty("spring.jpa.show-sql", "false");
            System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
    }
}