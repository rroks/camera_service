package com.camera.entity;

import com.camera.lib.NetSDKLib;
import lombok.Data;

@Data
public class TrafficInfo {
    // 事件名称
    private String eventName;
    // 车牌号
    private String plateNumber;
    // 车牌类型
    private String plateType;
    // 车牌类型
    private String plateColor;
    // 车身颜色
    private String vehicleColor;
    // 车身类型
    private String vehicleType;
    // 车辆大小
    private String vehicleSize;
    // 文件总数
    private String fileCount;
    // 文件编号
    private String fileIndex;
    // 组ID
    private String groupID;
    // 违法地点
    private String illegalPlace;
    // 通道号
    private String laneNumber;
    // 事件时间
    private NetSDKLib.NET_TIME_EX utcTime;
    // 车牌对应信息，BOOL类型 ?
    private int picEnable;
    // 车牌偏移量
    private int offset;
    // 文件大小
    private int fileLength;
    // 包围盒
    private NetSDKLib.DH_RECT boundingBox;
}
