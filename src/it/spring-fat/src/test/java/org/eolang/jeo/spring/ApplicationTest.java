/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Integration test for Application.
 * @since 0.2
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

    @Autowired
    private TestRestTemplate template;

    @LocalServerPort
    private int port;

    @Test
    void startupsServerAndMakesGetRequest() {
        final ResponseEntity<String> resp = this.template.getForEntity(
            String.format("http://localhost:%d/hello", this.port),
            String.class
        );
        Assertions.assertEquals(
            HttpStatus.OK,
            resp.getStatusCode(),
            "Status code is not OK"
        );
        Assertions.assertNotNull(
            resp.getBody(),
            "Response body is empty"
        );
        final String expected = "Glad to see you, Fat Spring...";
        Assertions.assertTrue(
            resp.getBody().contains(expected),
            String.format(
                "Hello response is not correct. The body: %s%n should contain '%s'",
                resp.getBody(),
                expected
            )
        );
    }

}
