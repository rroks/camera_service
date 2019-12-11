package com.camera.module;

import com.camera.entity.CameraStream;
import com.camera.entity.PlateAnalyzer;
import com.camera.entity.SysConstant;
import com.camera.entity.TrafficInfo;
import com.camera.lib.NetSDKLib;
import com.camera.lib.ToolKits;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

@Slf4j
public class CameraService {
    private static final long serialVersionUID = 1L;
    private Vector<String> channelList = new Vector<String>();

    private static PlateAnalyzer analyzerDataCB = new PlateAnalyzer();
    // 设备断线通知回调
    private static DisConnect disConnect = new DisConnect();

    // 网络连接恢复
    private static HaveReConnect haveReConnect = new HaveReConnect();

    // 预览句柄
    public static NetSDKLib.LLong playHandle = new NetSDKLib.LLong(0);
    public static NetSDKLib.LLong subscribeHandle;

    boolean b_openStrobe = false;
    boolean realPlayStatus = false;
    boolean b_attach = false;

    private final TrafficInfo trafficInfo = new TrafficInfo();
    private BufferedImage snapImage = null;
    private BufferedImage plateImage = null;

    private static ByteBuffer buffer;

    public static CameraStream cameraStream = new CameraStream();

    public static void init() throws IOException {
        Login.init(disConnect, haveReConnect);

        Native.setCallbackThreadInitializer(analyzerDataCB, new CallbackThreadInitializer(false, false, "traffic callback thread"));
        Login.login(SysConstant.TARGET_IP, SysConstant.TARGET_PORT, SysConstant.USER_ID, SysConstant.PASSWORD);
        subscribeHandle = SubscribeModule.attachIVSEvent(0, analyzerDataCB);
        cameraStream.setBuffer(ByteBuffer.allocateDirect(1024));
        cameraStream.setPlayHandle(RealPlayModule.startRealPlay(1, 0, cameraStream.getBuffer()));
        // pass m_hPlayHandle data to stream
    }

    public static void quit() {
        RealPlayModule.stopRealPlay(playHandle);
        SubscribeModule.detachIVSEvent(subscribeHandle);
        Login.logout();
    }

    public static void takeShot() {
        NetSDKLib.MANUAL_SNAP_PARAMETER snapParam = new NetSDKLib.MANUAL_SNAP_PARAMETER();
        snapParam.nChannel = 1;
        // 抓图序列号，必须用数组拷贝
        String sequence = "11111";
        System.arraycopy(sequence.getBytes(), 0, snapParam.bySequence, 0, sequence.getBytes().length);

        snapParam.write();
        boolean bRet = Login.NET_SDK.CLIENT_ControlDeviceEx(Login.LOGIN_HANDLE, NetSDKLib.CtrlType.CTRLTYPE_MANUAL_SNAP, snapParam.getPointer(), null, 5000);
        if (!bRet) {
            System.err.println("Failed to manual snap, last error " + ToolKits.getErrorCodePrint());
        } else {
            System.out.println("Seccessed to manual snap");
        }
        snapParam.read();
    }

    /////////////////面板///////////////////
    // 设备断线回调: 通过 CLIENT_Init 设置该回调函数，当设备出现断线时，SDK会调用该函数
    private static class DisConnect implements NetSDKLib.fDisConnect {
        public void invoke(NetSDKLib.LLong loginHandle, String dvrIp, int dvrPort, Pointer dwUser) {
            log.info(String.format("Device[%s] Port[%d] DisConnect!", dvrIp, dvrPort));
            // 断线提示
        }
    }

    // 网络连接恢复，设备重连成功回调
    // 通过 CLIENT_SetAutoReconnect 设置该回调函数，当已断线的设备重连成功时，SDK会调用该函数
    private static class HaveReConnect implements NetSDKLib.fHaveReConnect {
        public void invoke(NetSDKLib.LLong loginHandle, String dvrIp, int dvrPort, Pointer user) {
            log.info(String.format("ReConnect Device[%s] Port[%d]", dvrIp, dvrPort));
            // 重连提示
        }
    }
}
