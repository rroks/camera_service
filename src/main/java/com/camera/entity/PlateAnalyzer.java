package com.camera.entity;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONString;
import cn.hutool.json.JSONUtil;
import com.camera.common.Res;
import com.camera.lib.NetSDKLib;
import com.camera.lib.ToolKits;
import com.camera.utils.ImageUtils;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

@Slf4j
public class PlateAnalyzer implements NetSDKLib.fAnalyzerDataCallBack {

    public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
                      Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
                      Pointer dwUser, int nSequence, Pointer reserved) {
        if (lAnalyzerHandle.longValue() == 0) {
            return -1;
        }

        if (dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFICJUNCTION
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_RUNREDLIGHT
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_OVERLINE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_RETROGRADE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_TURNLEFT
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_TURNRIGHT
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_UTURN
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_OVERSPEED
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_UNDERSPEED
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_PARKING
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_WRONGROUTE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_CROSSLANE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_OVERYELLOWLINE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_YELLOWPLATEINLANE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_PEDESTRAINPRIORITY
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_MANUALSNAP
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_VEHICLEINROUTE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_VEHICLEINBUSROUTE
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_BACKING
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_PARKINGSPACEPARKING
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_PARKINGSPACENOPARKING
                || dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_WITHOUT_SAFEBELT) {

            // 获取识别对象 车身对象 事件发生时间 车道号等信息
            TrafficInfo trafficInfo = GetStuObject(dwAlarmType, pAlarmInfo);

            // 保存图片，获取图片缓存
            ImageUtils.savePlatePic(pBuffer, dwBufSize, trafficInfo);

            // 列表、图片界面显示
//                EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
//                if (eventQueue != null)
//                {
//                    eventQueue.postEvent( new CameraInit(target,
//                            snapImage,
//                            plateImage,
//                            trafficInfo));
//                }
        }

        return 0;
    }

    // 获取识别对象 车身对象 事件发生时间 车道号等信息
    private TrafficInfo GetStuObject(int dwAlarmType, Pointer pAlarmInfo) {
        if (pAlarmInfo == null) {
            return null;
        }

        TrafficInfo trafficInfo = new TrafficInfo();

//        if (dwAlarmType == NetSDKLib.EVENT_IVS_TRAFFIC_MANUALSNAP) {
            NetSDKLib.DEV_EVENT_TRAFFICJUNCTION_INFO msg = new NetSDKLib.DEV_EVENT_TRAFFICJUNCTION_INFO();
            ToolKits.GetPointerData(pAlarmInfo, msg);
//            trafficInfo.setEventName(Res.string().getEventName(NetSDKLib.EVENT_IVS_TRAFFICJUNCTION));
            trafficInfo.setEventName("" + NetSDKLib.EVENT_IVS_TRAFFICJUNCTION);
            try {
                trafficInfo.setPlateNumber(new String(msg.stuObject.szText, "GBK").trim());
                System.out.println(msg.stuObject.szText);
            } catch (UnsupportedEncodingException e) {
                log.info(e.getMessage());
            }
            trafficInfo.setPlateType(new String(msg.stTrafficCar.szPlateType).trim());
            trafficInfo.setFileCount(String.valueOf(msg.stuFileInfo.bCount));
            trafficInfo.setFileIndex(String.valueOf(msg.stuFileInfo.bIndex));
            trafficInfo.setGroupID(String.valueOf(msg.stuFileInfo.nGroupId));
            trafficInfo.setIllegalPlace(ToolKits.GetPointerDataToByteArr(msg.stTrafficCar.szDeviceAddress));
            trafficInfo.setLaneNumber(String.valueOf(msg.nLane));
            trafficInfo.setPlateColor(new String(msg.stTrafficCar.szPlateColor).trim());
            trafficInfo.setVehicleColor(new String(msg.stTrafficCar.szVehicleColor).trim());
            trafficInfo.setVehicleType(new String(msg.stuVehicle.szObjectSubType).trim());
//            trafficInfo.setVehicleSize(Res.string().getTrafficSize(msg.stTrafficCar.nVehicleSize));
            trafficInfo.setVehicleSize("" + msg.stTrafficCar.nVehicleSize);
            trafficInfo.setUtcTime(msg.UTC);
            trafficInfo.setPicEnable(msg.stuObject.bPicEnble);
            trafficInfo.setOffset(msg.stuObject.stPicInfo.dwOffSet);
            trafficInfo.setFileLength(msg.stuObject.stPicInfo.dwFileLenth);
            trafficInfo.setBoundingBox(msg.stuObject.BoundingBox);
//        }
        System.out.println(JSONUtil.parseObj(trafficInfo));
        return trafficInfo;
    }
}
