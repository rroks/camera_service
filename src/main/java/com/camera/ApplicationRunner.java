package com.camera;

import com.camera.entity.CameraStream;
import com.camera.module.CameraService;
import com.camera.module.Login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationRunner {
    public static void main(String[] args) throws IOException {
        CameraService.init();
        CameraService.takeShot();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        while (true) {
            line = reader.readLine();
            if (Integer.valueOf(line) == 0) {
                break;
            }
        }
        CameraService.quit();
        System.exit(0);
    }
}
