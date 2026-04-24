package com.thecheatschool.thecheatschool.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;
    private T data;
    private String message;

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
        this.message = null;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", null, message);
    }
}