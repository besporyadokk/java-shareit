package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BaseClient baseClient;

    @BeforeEach
    void setUp() {
        baseClient = new BaseClient(restTemplate);
    }

    @Test
    @DisplayName("GET запрос без параметров и userId")
    void get_WithoutParamsAndUserId_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    @DisplayName("GET запрос с userId")
    void get_WithUserId_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    @DisplayName("GET запрос с параметрами")
    void get_WithParams_ReturnsResponse() {
        Map<String, Object> params = Map.of("param", "value");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test?param={param}"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(params)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test?param={param}", 1, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST запрос")
    void post_WithBody_ReturnsResponse() {
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test", body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST запрос с userId")
    void post_WithUserId_ReturnsResponse() {
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test", 1, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST запрос с параметрами")
    void post_WithParams_ReturnsResponse() {
        Map<String, Object> params = Map.of("param", "value");
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test?param={param}"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class), eq(params)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test?param={param}", 1, params, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("PUT запрос")
    void put_WithUserIdAndBody_ReturnsResponse() {
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.put("/test", 1, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("PUT запрос с параметрами")
    void put_WithParams_ReturnsResponse() {
        Map<String, Object> params = Map.of("param", "value");
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test?param={param}"), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class), eq(params)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.put("/test?param={param}", 1, params, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("PATCH запрос без тела")
    void patch_WithoutBody_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("PATCH запрос с телом")
    void patch_WithBody_ReturnsResponse() {
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test", 1, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("PATCH запрос с параметрами")
    void patch_WithParams_ReturnsResponse() {
        Map<String, Object> params = Map.of("param", "value");
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");
        when(restTemplate.exchange(eq("/test?param={param}"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(params)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test?param={param}", 1, params, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("DELETE запрос")
    void delete_WithUserId_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("DELETE запрос с параметрами")
    void delete_WithParams_ReturnsResponse() {
        Map<String, Object> params = Map.of("param", "value");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(eq("/test?param={param}"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class), eq(params)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test?param={param}", 1, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Обработка исключения HttpStatusCodeException")
    void makeAndSendRequest_WhenException_ReturnsErrorResponse() {
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(exception.getResponseBodyAsByteArray()).thenReturn("error".getBytes());

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = baseClient.get("/test");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("error".getBytes());
    }
}