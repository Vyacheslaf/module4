package com.epam.esm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

//    @Sql({"classpath:schema.sql"})
    @Test
    void findAllTest() {
        String url = "http://localhost:" + port + "/jpa/certificate";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(URI.create(url), String.class);
        assertNotEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        url = "http://localhost:" + port + "/jpa/tag";
        responseEntity = this.restTemplate.getForEntity(URI.create(url), String.class);
        assertNotEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
