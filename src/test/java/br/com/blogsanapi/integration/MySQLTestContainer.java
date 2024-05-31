package br.com.blogsanapi.integration;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLTestContainer implements BeforeAllCallback {

    private MySQLContainer<?> mysqlContainer;
    
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        this.criaContainer();
        this.configProperties();
    }

    private void configProperties() {
        System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
        System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
        System.setProperty("spring.datasource.driver-class-name", mysqlContainer.getDriverClassName());
    }
    
    private void criaContainer() {
        this.mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.36"))
                .withDatabaseName("test")
                .withUsername("root")
                .withPassword("root");
        this.mysqlContainer.start();
    }
}