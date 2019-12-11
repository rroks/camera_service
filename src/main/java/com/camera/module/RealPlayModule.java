package com.camera.module;

import com.camera.lib.NetSDKLib;
import com.camera.lib.ToolKits;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class RealPlayModule {
    /**
     * \if ENGLISH_LANG
     * Start RealPlay
     * \else
     * 开始预览
     * \endif
     */
    public static NetSDKLib.LLong startRealPlay(int channel, int stream, ByteBuffer byteBuffer) {
        NetSDKLib.LLong playHandle = Login.NET_SDK.CLIENT_RealPlayEx(Login.LOGIN_HANDLE, channel, Native.getDirectBufferPointer(byteBuffer), stream);

        if(playHandle.longValue() == 0) {
            log.info("开始实时监视失败，错误码" + ToolKits.getErrorCodePrint());
        } else {
            log.info("Success to start real play");
        }

        return playHandle;
    }

    /**
     * \if ENGLISH_LANG
     * Start RealPlay
     * \else
     * 停止预览
     * \endif
     */
    public static void stopRealPlay(NetSDKLib.LLong realPlayHandle) {
        if(realPlayHandle.longValue() == 0) {
            return;
        }

        boolean bRet = Login.NET_SDK.CLIENT_StopRealPlayEx(realPlayHandle);
        if(bRet) {
            realPlayHandle.setValue(0);
        }
    }
}
