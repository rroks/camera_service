package com.camera.module;

import com.camera.lib.NetSDKLib;
import com.camera.lib.ToolKits;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Login {

    public static final NetSDKLib NET_SDK = NetSDKLib.NETSDK_INSTANCE;
    public static final NetSDKLib CONFIG_SDK = NetSDKLib.CONFIG_INSTANCE;

    // 设备信息
    public static NetSDKLib.NET_DEVICEINFO_Ex DEVICE_INFO = new NetSDKLib.NET_DEVICEINFO_Ex();

    // 登陆句柄
    public static NetSDKLib.LLong LOGIN_HANDLE = new NetSDKLib.LLong(0);

    private static boolean initStatus = false;

    /**
     * \if ENGLISH_LANG
     * Init
     * \else
     * 初始化
     * \endif
     */
    public static boolean init(NetSDKLib.fDisConnect disConnect, NetSDKLib.fHaveReConnect haveReConnect) {
        initStatus = NET_SDK.CLIENT_Init(disConnect, null);
        if (!initStatus) {
            log.info("Initialize SDK failed");
            return false;
        }

        // 设置断线重连回调接口，设置过断线重连成功回调函数后，当设备出现断线情况，SDK内部会自动进行重连操作
        // 此操作为可选操作，但建议用户进行设置
        NET_SDK.CLIENT_SetAutoReconnect(haveReConnect, null);

        //设置登录超时时间和尝试次数，可选
        //登录请求响应超时时间设置为5S
        int waitTime = 5000;
        //登录时尝试建立链接1次
        int tryTimes = 1;
        NET_SDK.CLIENT_SetConnectTime(waitTime, tryTimes);

        // 设置更多网络参数，NET_PARAM的nWaittime，nConnectTryNum成员与CLIENT_SetConnectTime 
        // 接口设置的登录设备超时时间和尝试次数意义相同,可选
        NetSDKLib.NET_PARAM netParam = new NetSDKLib.NET_PARAM();
        // 登录时尝试建立链接的超时时间
        netParam.nConnectTime = 10000;
        // 设置子连接的超时时间
        netParam.nGetConnInfoTime = 3000;
        NET_SDK.CLIENT_SetNetworkParam(netParam);

        return true;
    }

    public static void stop() {
        if (initStatus) {
            NET_SDK.CLIENT_Cleanup();
        }
    }

    /**
     * \if ENGLISH_LANG
     * Login Device
     * \else
     * 登录设备
     * \endif
     */
    public static boolean login(String ip, int port, String user, String password) {
        IntByReference nError = new IntByReference(0);
        LOGIN_HANDLE = NET_SDK.CLIENT_LoginEx2(ip, port, user, password, 0, null, DEVICE_INFO, nError);
        if (LOGIN_HANDLE.longValue() == 0) {
            System.out.println(String.format("Login Device[%s] Port[%d]Failed. %s\n", ip, port, ToolKits.getErrorCodePrint()));
            log.info(String.format("Login Device[%s] Port[%d]Failed. %s\n", ip, port, ToolKits.getErrorCodePrint()));
        } else {
            System.out.println("Login Success [ " + ip + " ]");
            log.info("Login Success [ " + ip + " ]");
        }

        return LOGIN_HANDLE.longValue() != 0;
    }

    /**
     * \if ENGLISH_LANG
     * Logout Device
     * \else
     * 登出设备
     * \endif
     */
    public static boolean logout() {
        boolean bRet = NET_SDK.CLIENT_Logout(LOGIN_HANDLE);
        if (bRet) {
            LOGIN_HANDLE.setValue(0);
        }
        return bRet;
    }
}
