package uk.gov.hmcts.reform.opal.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SampleSmokeTest {

    private static final String TEST_URL = System.getenv().getOrDefault("TEST_URL", "http://localhost:4553");
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Test
    void smokeTest() throws IOException, InterruptedException {
        HttpResponse<String> response = HTTP_CLIENT.send(
            HttpRequest.newBuilder(URI.create(TEST_URL))
                .header("Accept", "application/json")
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        );

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().startsWith("Welcome"));
    }
}
