package com.camera.module;

import com.camera.lib.NetSDKLib;
import com.camera.lib.ToolKits;

public class SubscribeModule {
    /**
     * 订阅实时上传智能分析数据
     * @return
     */
    public static NetSDKLib.LLong attachIVSEvent(int ChannelId, NetSDKLib.fAnalyzerDataCallBack m_AnalyzerDataCB) {
        /**
         * 说明：
         * 	通道数可以在有登录是返回的信息 m_stDeviceInfo.byChanNum 获取
         *  下列仅订阅了0通道的智能事件.
         */
        int bNeedPicture = 1; // 是否需要图片

        NetSDKLib.LLong subscribeHandle = Login.NET_SDK.CLIENT_RealLoadPictureEx(Login.LOGIN_HANDLE, ChannelId,  NetSDKLib.EVENT_IVS_ALL,
                bNeedPicture , m_AnalyzerDataCB , null , null);
        if( subscribeHandle.longValue() != 0  ) {
            System.out.println("CLIENT_RealLoadPictureEx Success  ChannelId : \n" + ChannelId);
        } else {
            System.err.println("CLIENT_RealLoadPictureEx Failed!" + ToolKits.getErrorCodePrint());
            return null;
        }

        return subscribeHandle;
    }

    /**
     * 停止上传智能分析数据－图片
     */
    public static void detachIVSEvent(NetSDKLib.LLong subscribeHandle) {
        if (0 != subscribeHandle.longValue()) {
            Login.NET_SDK.CLIENT_StopLoadPic(subscribeHandle);
            System.out.println("Stop detach IVS event");
            subscribeHandle.setValue(0);
        }
    }
}
