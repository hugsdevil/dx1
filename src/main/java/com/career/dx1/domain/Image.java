package com.career.dx1.domain;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;

public class Image {
    private int width;
    private int height;
    private OutputStream os;
    private Exception exception;

    public static Image valueOf(BufferedImage bi, OutputStream os) {
        Image v = new Image();
        v.width = bi.getWidth();
        v.height = bi.getHeight();
        v.os = os;
        return v;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getBytes() throws IOException {
        if (os instanceof ByteArrayOutputStream) {
            return ((ByteArrayOutputStream)os).toByteArray();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeTo(os);
        return baos.toByteArray();
    }

    public boolean isFail() {
        return exception != null;
    }

    public Exception getException() {
        return exception;
    }
    
    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> getHttpEntity(String token) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(getBytes());
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", resource)
            .contentType(MediaType.IMAGE_JPEG)
            .filename("file.jpg");
        MultiValueMap<String, HttpEntity<?>> body = builder.build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);
        headers.add("token", token);
        return new HttpEntity<>(body, headers);
    }

    public static Image exception(Exception e, OutputStream os) {
        Image v = new Image();
        v.width = 0;
        v.height = 0;
        v.os = os;
        v.exception = e;
        return v;
    }
}
