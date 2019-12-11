package com.camera.entity;

import com.camera.lib.NetSDKLib;
import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class CameraStream {
    private ByteBuffer buffer;
    private NetSDKLib.LLong playHandle;
}
