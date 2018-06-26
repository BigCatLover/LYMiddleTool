package com.jingyue.apktools.utils;

import com.jingyue.apktools.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class ImageUtil {
    /**
     * 导入本地图片到缓冲区
     */
    public static BufferedImage loadImageLocal(String imgName) {
        try {
            return ImageIO.read(new File(imgName));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static BufferedImage modifyImagetogeter(BufferedImage icon, BufferedImage sub) {
        try {
            Graphics2D g = icon.createGraphics();
            g.getDeviceConfiguration().createCompatibleImage(icon.getWidth(), icon.getHeight(), Transparency.TRANSLUCENT);
            g.drawImage(sub, 0, 0, null);
            g.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return icon;
    }

    /**
     * 生成新图片到本地
     */
    public static void writeImageLocal(String newImage, BufferedImage img) {
        if (newImage != null && img != null) {
            try {
                File outputfile = new File(newImage);
                ImageIO.write(img, "png", outputfile);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static BufferedImage changeIconSize(String iconPath) {
        try {
            BufferedImage bi = ImageIO.read(new File(iconPath));
            BufferedImage tag = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = tag.createGraphics();
            g.getDeviceConfiguration().createCompatibleImage(512, 512, Transparency.TRANSLUCENT);
            g.dispose();
            Graphics2D g2d = tag.createGraphics();
            //绘制改变尺寸后的图
            g2d.drawImage(bi, 0, 0, 512, 512, null);
            return tag;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return null;
    }

    public static void mergeImages(String icon, String subscript, String output, Map<String, Integer> map) {
        BufferedImage ic = changeIconSize(icon);
        BufferedImage sub = loadImageLocal(subscript);
        File file = new File(output);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        writeImageLocal(output, modifyImagetogeter(ic, sub));
        exportMulti(output, map);
    }

    public static void exportMulti(String icon, Map<String, Integer> map) {
        try {
            File outputDir;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(icon));
            //字节流转图片对象
            BufferedImage bi = ImageIO.read(in);
            for (Map.Entry<String, Integer> sizeEntry : map.entrySet()) {
                outputDir = new File(sizeEntry.getKey(), Config.iconname);
                outputDir.delete();
                outputDir.createNewFile();
                int size = sizeEntry.getValue();
                //构建图片流
                BufferedImage tag = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = tag.createGraphics();
                g.getDeviceConfiguration().createCompatibleImage(512, 512, Transparency.TRANSLUCENT);
                g.dispose();
                g = tag.createGraphics();
                //绘制改变尺寸后的图
                g.drawImage(bi, 0, 0, size, size, null);
                ImageIO.write(tag, "png", outputDir);
            }
            in.close();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
}
