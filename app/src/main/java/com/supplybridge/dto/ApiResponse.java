package com.supplybridge.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = true;
        resp.message = message;
        resp.data = data;
        return resp;
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = false;
        resp.message = message;
        resp.errors = errors;
        return resp;
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        return error(message, List.of(error));
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
