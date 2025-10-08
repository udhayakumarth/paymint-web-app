package com.example.springapp;

public class BaseResponceDto {
    private Object message;
    private Object data;


    public BaseResponceDto(Object message, Object data) {
        this.message = message;
        this.data = data;
    }

    public BaseResponceDto(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
