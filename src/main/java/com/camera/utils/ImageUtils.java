package com.camera.utils;

import com.camera.common.SavePath;
import com.camera.entity.TrafficInfo;
import com.camera.lib.NetSDKLib;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Slf4j
public class ImageUtils {
    public static void savePlatePic(Pointer pBuffer, int dwBufferSize, TrafficInfo trafficInfo) {

        BufferedImage snapImage = null;
        BufferedImage plateImage = null;

        String bigPicturePath; // 大图
        String platePicturePath; // 车牌图

        if (pBuffer == null || dwBufferSize <= 0 ) {
            return;
        }

        // 保存大图
        byte[] buffer = pBuffer.getByteArray(0, dwBufferSize);
        ByteArrayInputStream byteArrInput = new ByteArrayInputStream(buffer);

        bigPicturePath = SavePath.getSavePath().getSaveTrafficImagePath() + "Big_" + trafficInfo.getUtcTime().toStringTitle() + "_" +
                trafficInfo.getFileCount() + "-" + trafficInfo.getFileIndex() + "-" + trafficInfo.getGroupID() + ".jpg";
        System.out.println(bigPicturePath);

        try {
            snapImage = ImageIO.read(byteArrInput);
            if(snapImage == null) {
                return;
            }
            ImageIO.write(snapImage, "jpg", new File(bigPicturePath));
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        if(bigPicturePath == null || bigPicturePath.equals("")) {
            return;
        }

        if (trafficInfo.getPicEnable() == 1) {
            //根据pBuffer中数据偏移保存小图图片文件
            if (trafficInfo.getFileLength() > 0) {
                platePicturePath = SavePath.getSavePath().getSaveTrafficImagePath() + "plate_" + trafficInfo.getUtcTime().toStringTitle() + "_" +
                        trafficInfo.getFileCount() + "-" + trafficInfo.getFileIndex() + "-" + trafficInfo.getGroupID() + ".jpg";

                int size = 0;
                if(dwBufferSize <= trafficInfo.getOffset()) {
                    return;
                }

                if(trafficInfo.getFileLength() <= dwBufferSize - trafficInfo.getOffset()) {
                    size = trafficInfo.getFileLength();
                } else {
                    size = dwBufferSize - trafficInfo.getOffset();
                }
                byte[] bufPlate = pBuffer. getByteArray(trafficInfo.getOffset(), size);
                ByteArrayInputStream byteArrInputPlate = new ByteArrayInputStream(bufPlate);
                try {
                    plateImage = ImageIO.read(byteArrInputPlate);
                    if(plateImage == null) {
                        return;
                    }
                    ImageIO.write(plateImage, "jpg", new File(platePicturePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            if(trafficInfo.getBoundingBox() == null) {
                return;
            }
            //根据大图中的坐标偏移计算显示车牌小图

            NetSDKLib.DH_RECT dhRect = trafficInfo.getBoundingBox();
            //1.BoundingBox的值是在8192*8192坐标系下的值，必须转化为图片中的坐标
            //2.OSD在图片中占了64行,如果没有OSD，下面的关于OSD的处理需要去掉(把OSD_HEIGHT置为0)
            final int OSD_HEIGHT = 0;

            long nWidth = snapImage.getWidth(null);
            long nHeight = snapImage.getHeight(null);

            nHeight = nHeight - OSD_HEIGHT;
            if ((nWidth <= 0) || (nHeight <= 0)) {
                return ;
            }

            NetSDKLib.DH_RECT dstRect = new NetSDKLib.DH_RECT();

            dstRect.left.setValue((long)((double)(nWidth * dhRect.left.longValue()) / 8192.0));
            dstRect.right.setValue((long)((double)(nWidth * dhRect.right.longValue()) / 8192.0));
            dstRect.bottom.setValue((long)((double)(nHeight * dhRect.bottom.longValue()) / 8192.0));
            dstRect.top.setValue((long)((double)(nHeight * dhRect.top.longValue()) / 8192.0));

            int x = dstRect.left.intValue();
            int y = dstRect.top.intValue() + OSD_HEIGHT;
            int w = dstRect.right.intValue() - dstRect.left.intValue();
            int h = dstRect.bottom.intValue() - dstRect.top.intValue();

            if(x == 0 || y == 0 || w <= 0 || h <= 0) {
                return;
            }

            try {
                plateImage = snapImage.getSubimage(x, y, w, h);
                platePicturePath = SavePath.getSavePath().getSaveTrafficImagePath() + "plate_" + trafficInfo.getUtcTime().toStringTitle() + "_" +
                        trafficInfo.getFileCount() + "-" + trafficInfo.getFileIndex() + "-" + trafficInfo.getGroupID() + ".jpg";
                if(plateImage == null) {
                    return;
                }
                ImageIO.write(plateImage, "jpg", new File(platePicturePath));
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
    }
}
