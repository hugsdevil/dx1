package com.career.dx1.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    RestTemplate restTemplate() {
        HttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(100)
            .setMaxConnPerRoute(5)
            .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);    // 5 sec
        factory.setConnectTimeout(3000); // 3 sec
        factory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new RestTemplateLoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}

class RestTemplateLoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    private Logger logger = LoggerFactory.getLogger(RestTemplateLoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        StringBuilder sb = new StringBuilder();

        loggingRequest(sb, request, body);
        ClientHttpResponse response = execution.execute(request, body);
        loggingResponse(sb, response);

        logger.info(sb.toString());
        return response;
    }

    private void loggingRequest(StringBuilder sb, HttpRequest request, byte[] body) throws IOException {
        sb.append("\n========================= Request Begin =========================");
        sb.append("\nMethod      : ").append(request.getMethod());
        sb.append("\nURI         : ").append(request.getURI());
        sb.append("\nHeaders     : ").append(request.getHeaders());

        if (isPrintableBody(request.getHeaders())) {
            sb.append("\nBody        : \n").append(new String(body, "UTF-8"));
        }
        sb.append("\n========================= Request End ===========================");
    }

    private void loggingResponse(StringBuilder sb, ClientHttpResponse response) throws IOException {
        sb.append("\n========================= Response Begin ========================");
        sb.append("\nStatus      : ").append(response.getStatusCode());
        sb.append("\nHeaders     : ").append(response.getHeaders());

        if (isPrintableBody(response.getHeaders())) {
            sb.append("\nBody        : \n").append(StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
        }
        sb.append("\n========================= Response End ==========================");
    }

    private boolean isPrintableBody(HttpHeaders headers) {
        MediaType mediaType = headers.getContentType();
        if (MediaType.APPLICATION_JSON.includes(mediaType) || MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType)) {
            return true;
        }
        return false;
    }
}