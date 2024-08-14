package com.onecandy.ruleengine.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse<T> {
    private String status;
    private int StatusCode;
    private String statusMessage;
    private T response;

    public static <T> CustomResponse<T> success(T response){
        return new CustomResponse<>("true", 200, "SUCCESS", response);
    }

    public static <T> CustomResponse<T> error(int StatusCode, String statusMessage, T response){
        return new CustomResponse<>("false", StatusCode, statusMessage, response);
    }
}
