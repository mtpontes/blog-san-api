package br.com.blogsanapi.integration;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class H2Test implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
            System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
            System.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb");
            System.setProperty("spring.datasource.username", "sa");
            System.setProperty("spring.datasource.password", "password");

            System.setProperty("spring.jpa.show-sql", "false");
            System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
    }
}