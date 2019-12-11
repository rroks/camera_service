package com.camera.entity;

import lombok.Data;

@Data
public class LoginInfo {
    private String ip = "192.168.11.12";
    private Integer port = 37777;
    private String userId = "admin";
    private String password = "admin123";
}
