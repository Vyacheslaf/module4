package com.epam.esm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationContainerTest {
    @Container
    public static PostgreSQLContainer postgreSQLContainer ;

    static  {
        postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
                .withDatabaseName("test-db")
                .withPassword("test")
                .withUsername("test");
        postgreSQLContainer.withInitScript("schema.sql");
        postgreSQLContainer.start();
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }
    @Test
    void findAllTest() {
        String url = "http://localhost:" + port + "/jpa/certificate";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(URI.create(url), String.class);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        url = "http://localhost:" + port + "/jpa/tag";
        responseEntity = this.restTemplate.getForEntity(URI.create(url), String.class);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
}
