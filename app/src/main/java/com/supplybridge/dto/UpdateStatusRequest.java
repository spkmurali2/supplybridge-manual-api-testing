package com.supplybridge.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateStatusRequest {

    @NotBlank(message = "status is required")
    private String status;

    public UpdateStatusRequest() {}

    public UpdateStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
