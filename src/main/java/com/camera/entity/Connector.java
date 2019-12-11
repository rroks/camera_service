package com.camera.entity;

import com.camera.lib.NetSDKLib;
import com.camera.lib.ToolKits;
import com.sun.jna.ptr.IntByReference;
import lombok.Data;

@Data
public class Connector {
    private NetSDKLib netsdkInstance;
    private NetSDKLib configInstance;

    // 设备信息
    private NetSDKLib.NET_DEVICEINFO_Ex deviceInfo;

    // 登陆句柄
    private NetSDKLib.LLong connectionHandle;

    private boolean initStatus;
    private int waitTime;
    private int tryTimes;

    public void Connector() {
        this.netsdkInstance = NetSDKLib.NETSDK_INSTANCE;
        this.configInstance = NetSDKLib.CONFIG_INSTANCE;
        this.deviceInfo = new NetSDKLib.NET_DEVICEINFO_Ex();
        this.connectionHandle = new NetSDKLib.LLong(0);
        this.initStatus = false;
        this.waitTime = 5000;
        this.tryTimes = 1;
    }

    public boolean init(NetSDKLib.fDisConnect disConnect, NetSDKLib.fHaveReConnect haveReConnect) {
        this.initStatus = NET_SDK.CLIENT_Init(disConnect, null);
        if (!initStatus) {
            log.info("Initialize SDK failed");
            return initStatus;
        }

        // 设置断线重连回调接口，设置过断线重连成功回调函数后，当设备出现断线情况，SDK内部会自动进行重连操作
        // 此操作为可选操作，但建议用户进行设置
        NET_SDK.CLIENT_SetAutoReconnect(haveReConnect, null);

        NET_SDK.CLIENT_SetConnectTime(this.waitTime, this.tryTimes);

        // 设置更多网络参数，NET_PARAM的nWaittime，nConnectTryNum成员与CLIENT_SetConnectTime
        // 接口设置的登录设备超时时间和尝试次数意义相同,可选
        NetSDKLib.NET_PARAM netParam = new NetSDKLib.NET_PARAM();
        // 登录时尝试建立链接的超时时间
        netParam.nConnectTime = 10000;
        // 设置子连接的超时时间
        netParam.nGetConnInfoTime = 3000;
        NET_SDK.CLIENT_SetNetworkParam(netParam);

        return initStatus;
    }

    public void stop() {
        if (initStatus) {
            NET_SDK.CLIENT_Cleanup();
        }
    }

    public boolean login(String ip, int port, String user, String password) {
        IntByReference nError = new IntByReference(0);
        this.connectionHandle = NET_SDK.CLIENT_LoginEx2(ip, port, user, password, 0, null, deviceInfo, nError);
        if (this.connectionHandle.longValue() == 0) {
            System.out.println(String.format("Login Device[%s] Port[%d]Failed. %s\n", ip, port, ToolKits.getErrorCodePrint()));
            log.info(String.format("Login Device[%s] Port[%d]Failed. %s\n", ip, port, ToolKits.getErrorCodePrint()));
        } else {
            System.out.println("Login Success [ " + ip + " ]");
            log.info("Login Success [ " + ip + " ]");
        }

        return this.connectionHandle.longValue() != 0;
    }

    public boolean logout() {
        boolean bRet = NET_SDK.CLIENT_Logout(LOGIN_HANDLE);
        if (bRet) {
            this.initStatus = false;
            this.connectionHandle.setValue(0);
        }
        return bRet;
    }
}
