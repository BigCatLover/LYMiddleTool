package com.jingyue.apktools.core;

import com.jingyue.apktools.utils.EncryptUtils;
import com.jingyue.apktools.utils.FileHelper;
import com.jingyue.apktools.utils.LogUtils;
import com.jingyue.apktools.utils.ZipUtils;
import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.CertificateMeta;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * Created by zhanglei on 2018/7/5.
 */
public class ShellUtil {

    private static final String PROXY_APPLICATION_NAME = "com.linchaolong.apktoolplus.jiagu.ProxyApplication";
    private static final String METADATA_SRC_APPLICATION = "apktoolplus_jiagu_app";
    private static final String JIAGU_DATA_BIN = "jiagu_data.bin";

    public static boolean updateMenifest(File manifest){
        XMLWriter writer = null;
        try {
            SAXReader reader = new SAXReader();
            // 读取AndroidManifest.xml
            Document document = reader.read(manifest);
            Element rootElement = document.getRootElement();

            Element applicationElement = rootElement.element("application");
            Attribute appNameAttribute = applicationElement.attribute("name");
            if (appNameAttribute != null) {
                String appName = appNameAttribute.getValue();
                // 修改appName为代理Application
                appNameAttribute.setValue(PROXY_APPLICATION_NAME);
                // 添加meta-data保存原有Application Name
                applicationElement.addElement("meta-data")
                        .addAttribute("android:name", METADATA_SRC_APPLICATION)
                        .addAttribute("android:value", appName);
            } else {
                applicationElement.addAttribute("android:name", PROXY_APPLICATION_NAME);
            }
            // 保存AndroidManifest.xml
            // 创建格式器
            OutputFormat format = OutputFormat.createPrettyPrint();// 整齐的格式
            // OutputFormat format = OutputFormat.createCompactFormat();//紧凑的格式
            format.setEncoding("UTF-8");
            // 获取XML写入//使用字节流，字节写入流会查找format中设置的码表
            writer = new XMLWriter(new FileOutputStream(manifest),
                    format);
            writer.write(document);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean jiagu(final File decompileDir) {
        File jiaguZip = AppManager.getShellZip();
        if (!jiaguZip.exists()) {
            return false;
        }

        // apk是否有lib目录
        File lib = new File(decompileDir, "lib");
        String[] platforms = lib.list();
        boolean flag = false;
        if(platforms!=null){
            for(String s:platforms){
                if(!s.startsWith(".")){
                    flag = true;
                    break;
                }
            }
        }

        boolean isHasLib = lib.exists() && flag;

        ZipUtils.list(jiaguZip, new ZipUtils.FileFilter() {
            @Override
            public void handle(ZipFile zipFile, FileHeader fileHeader) {
                // 1.替换smali目录
                if (fileHeader.getFileName().startsWith("smali")) {
                    if (!ZipUtils.unzip(zipFile, fileHeader, decompileDir)) {
                        LogUtils.e(fileHeader.getFileName() + " unzip failure from " + zipFile.getFile().getAbsolutePath());
                    }
                    // 2.拷贝lib
                } else if (fileHeader.getFileName().startsWith("libs")) {
                    if (!ZipUtils.unzip(zipFile, fileHeader, decompileDir)) {
                        LogUtils.e(fileHeader.getFileName() + " unzip failure from " + zipFile.getFile().getAbsolutePath());
                    }
                }
            }
        });

        File libs = new File(decompileDir, "libs");
        if (isHasLib) {
            for (String platform : platforms) {
                File libFile = new File(libs, platform + "/libapktoolplus_jiagu.so");
                if (libFile.exists()) {
                    FileHelper.move(libFile, new File(lib, platform + "/" + libFile.getName()));
                }
            }
        } else {
            // 如果没有lib，则拷贝所有平台lib到lib目录
            FileHelper.move(libs, lib);
        }
        FileHelper.delete(libs);

        return true;
    }

    /**
     * 加密dex
     *
     * @param decompileDir
     * @return
     */
    public static boolean encryptDex(File decompileDir) {
        if(decompileDir.isDirectory()){
            for(File f:decompileDir.listFiles()){
                if(f.getName().endsWith(".dex")){
                    File assets = new File(decompileDir, "assets");
                    assets.mkdirs();
                    assets = new File(decompileDir, "assets");

                    File encryptFile = new File(assets, JIAGU_DATA_BIN);
                    if(encryptFile.exists()){
                        encryptFile.delete();
                    }
                    encryptFile.mkdirs();
                    //加密
                    EncryptUtils.encrypt(f,  new File(assets, JIAGU_DATA_BIN+"/"+f.getName()));
                    f.delete();
                }
            }
        }

        return true;
    }

    /**
     * apk签名保护，防止二次打包
     *
     * @param apk
     * @param decompile
     */
    public static void signatureProtect(File apk, File decompile) {
        try(ApkParser parser = new ApkParser(apk)){
            List<CertificateMeta> certList = parser.getCertificateMetaList();
            String certMD5 = certList.get(0).getCertMd5();
            byte[] encryptData = EncryptUtils.encryptXXTEA(certMD5.getBytes());
            FileUtils.writeByteArrayToFile(new File(decompile,"assets/sign.bin"), encryptData);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 该apk是否已经加固
     *
     * @param decompile
     * @return
     */
    public static boolean isEncrypted(File decompile) {
        File f = new File(decompile, "assets/" + JIAGU_DATA_BIN);
        return f.exists();
    }

}
