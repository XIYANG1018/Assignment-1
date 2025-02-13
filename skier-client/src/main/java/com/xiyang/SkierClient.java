package com.xiyang;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SkierClient {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .executor(Executors.newFixedThreadPool(200))
            .build();
    private static final String BASE_URL = "http://34.219.46.66:8080/skiers-server_war";

    public void postLiftRideEvent(LiftRideEvent event) throws Exception {
        int retries = 5;
        while (retries > 0) {
            try {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + event.getUrlPath()))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(event.toJson()))
                        .build();

                long startTime = System.currentTimeMillis();
                HttpResponse<String> response = HTTP_CLIENT.send(request,
                        HttpResponse.BodyHandlers.ofString());
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;
                if (response.statusCode() == 200 || response.statusCode() == 201) {

                    return;
                } else {
                    throw new RuntimeException("HTTP Error: " + response.statusCode());
                }
            } catch (Exception e) {
                retries--;
                if (retries == 0) {
                    throw e;
                }
                Thread.sleep(100);
            }
        }
    }
}