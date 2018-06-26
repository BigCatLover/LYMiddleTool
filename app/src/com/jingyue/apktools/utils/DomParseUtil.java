package com.jingyue.apktools.utils;


import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.LYApkInfo;
import com.jingyue.apktools.bean.LocalPluginBean;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Created by zhanglei on 2018/4/10.
 */

public class DomParseUtil {

    public static boolean parsePluginXml(String path, LocalPluginBean bean) {
        boolean isSuccess = true;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(path));//必须指定文件的绝对路径

            //获取根节点对象
            Element rootElement = document.getRootElement();
            List<Element> list = rootElement.elements();

            for (org.dom4j.Element e : list) {
//获取属性值
                String key = e.attribute("name").getText();
                String value = e.attribute("value").getText();
                if (key.equalsIgnoreCase("id")) {
                    bean.setSdkid(value);
                }
                if (key.equalsIgnoreCase("versionname")) {
                    bean.setVersion(value);
                }
                if (key.equalsIgnoreCase("sdkversion")) {
                    bean.setSdkver(value);
                }

                if (key.equalsIgnoreCase("name")) {
                    bean.setName(value);
                }
                if (key.equalsIgnoreCase("tag")) {
                    bean.setTag(value);
                }
                if (key.equalsIgnoreCase("key")) {
                    bean.setSrc(value);
                }
            }

        } catch (Exception e) {
            LogUtils.e(e);
            isSuccess = false;
        }

        return isSuccess;
    }


    public static String getLastInputPath(File path){
        String lastpath = "";
        if(path.exists()){
            try {
                Document document = null;
                Map<String, String> history = new HashMap<>();
                SAXReader saxReader = new SAXReader();
                document = saxReader.read(path);
                Element rootElement = document.getRootElement();
                List<Element> list = rootElement.elements();
                for (org.dom4j.Element e : list) {
                    String key = e.attribute("key").getText();
                    if(key.equalsIgnoreCase("LastInputGamePath")){
                        lastpath = e.getText();
                        break;
                    }
                }

            }catch (Exception e){
               LogUtils.e(e);
            }

        }
        return lastpath;

    }
    public static void changeHistory(File path, String value,boolean isSdk) {
        if(!path.getParentFile().exists()){
            path.getParentFile().mkdirs();
        }
        try {
            Document document = null;
            if (!path.exists()) {
                document = DocumentHelper.createDocument();
                Element root = document.addElement("properties");
                if (!isSdk) {
                    Element child1 = root.addElement("entry");
                    child1.addAttribute("key","LastInputGamePath");
                    child1.setText(value);
                }
                for(int i=7;i>-1;i--){
                    Element child = root.addElement("entry");
                    child.addAttribute("key", String.valueOf(i));
                    if(i==0){
                        child.setText(value);
                    }
                }

            } else {
                Map<String, String> history = new HashMap<>();
                SAXReader saxReader = new SAXReader();
                document = saxReader.read(path);

                //获取根节点对象
                Element rootElement = document.getRootElement();
                List<Element> list = rootElement.elements();
                for (org.dom4j.Element e : list) {
                    String key = e.attribute("key").getText();
                    if(!key.equalsIgnoreCase("LastInputGamePath")){
                        String val = e.getText();
                        history.put(key, val);
                    }else {
                        e.setText(value);
                    }
                }

                if (history.containsValue(value)) {
                    Map<String, String> newList = new HashMap<>();
                    newList.put("0", value);
                    Iterator it = history.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        String val = (String)entry.getValue();
                        if(!val.equals(value)){
                            newList.put(String.valueOf(newList.size()),val);
                        }
                    }

                    for (int i = 1; i < list.size(); i++) {
                        Element e = list.get(i);
                        int key = Integer.valueOf(e.attribute("key").getText());
                        e.setText(newList.get(String.valueOf(key)));
                    }
                } else {
                    for (int i = 1; i < list.size(); i++) {
                        Element e = list.get(i);
                        String key = e.attribute("key").getText();
                        int k = Integer.valueOf(key);
                        if(key.equalsIgnoreCase("0")){
                            e.setText(value);
                        }else{
                            e.setText(history.get(String.valueOf(k-1)));
                        }
                    }
                }

            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
            writer.write(document);
            writer.close();

        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public static Map<String, String> loadHistory(File file) {
        Map<String, String> history = new HashMap<>();
        if(file.exists()){
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(file);//必须指定文件的绝对路径

                //获取根节点对象
                Element rootElement = document.getRootElement();
                List<Element> list = rootElement.elements();
                for (org.dom4j.Element e : list) {
                    String key = e.attribute("key").getText();
                    String value = e.getText();
                    if(!key.equals("LastInputGamePath")&&(!value.isEmpty())){
                        history.put(key, value);
                    }
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }

        return history;
    }

    public static String getValueFromXML(File path, String key) {
        String value = "";
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(path);//必须指定文件的绝对路径

            //获取根节点对象
            Element rootElement = document.getRootElement();
            Node node = rootElement.selectSingleNode("string[@name='" + key + "']");
            value = node.getText();

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return value;
    }

    public static LYApkInfo getApkInfoFromManifest(String path) {
        LYApkInfo info = new LYApkInfo();
        boolean hasAppid = false;
        boolean hasSdkid = false;
        try {
            Config.iconPaths.clear();
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(path));//必须指定文件的绝对路径
            //获取根节点对象
            Element rootElement = document.getRootElement();
            String pkgname = rootElement.attribute("package").getText();
            Config.GAME_PKG = pkgname;
            info.setPkgname(pkgname);
            boolean flag = false;
            Element application = rootElement.element("application");
            if (application != null) {
                String icon = application.attribute("icon").getText();
                String appname = application.attribute("label").getText();
                String[] split = icon.split("/");
                String name = split[1];
                String from = split[0].substring(1);
                File res = new File(new File(path).getParent(), "res");
                for (File f : res.listFiles()) {
                    if (f.isDirectory() && f.getName().startsWith(from + "-")) {
                        for (File png : f.listFiles()) {
                            String filename = png.getName().substring(0, png.getName().lastIndexOf("."));
                            if (filename.equals(name)) {
                                if (!flag) {
                                    info.setIconname(png.getName());
                                    Config.iconname = png.getName();
                                    flag = true;
                                }
                                Config.iconPaths.add(f.getPath());
                                break;
                            }
                        }
                    }
                }
                Collections.sort(Config.iconPaths);
                int size = Config.iconPaths.size();
                info.setIconpath(Config.iconPaths.get(size - 1));
                String[] split1 = appname.split("/");
                File strings = new File(new File(path).getParent(), "res" + File.separator +"values" + File.separator +"strings.xml");
                info.setApkname(getValueFromXML(strings, split1[1]));
                List<Element> meta_datas = application.elements("meta-data");
                for (Element e : meta_datas) {
                    String key = e.attribute("name").getText();
                    String value = e.attribute("value").getText();
                    if (key.equals("com.lysdk.middlesdk.appid")) {
                        Config.GAMEID = value;
                        info.setAppid(value);
                        hasAppid = true;
                    }
                    if (key.equals("com.lysdk.middlesdk.sdkid")) {
                        hasSdkid = true;
                    }
                }
            }
            if(hasAppid&&hasSdkid){
                info.setChecked(true);
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }

        return info;
    }

    public static boolean mergeManifest(String apk, String sdk, String pkg) {
        List<Element> sdkApplication = new ArrayList<>();//Manifest节点下除application节点外的所有节点
        List<Element> sdkOthers = new ArrayList<>();//application节点下的所有节点；
        List<Element> apkOthers = new ArrayList<>();//application节点下的所有节点；
        try {
            SAXReader saxReader = new SAXReader();
            Document document_sdk = saxReader.read(new File(sdk));
            Document document_apk = saxReader.read(new File(apk));

            Element root_sdk = document_sdk.getRootElement();
            Element root_apk = document_apk.getRootElement();
            String pkgname = root_apk.attribute("package").getText();
            root_apk.attribute("package").setValue(pkg);
            List<Element> list_sdk = root_sdk.elements();
            for (Element e1 : list_sdk) {
                if (e1.getName().equals("application")) {
                    sdkApplication = e1.elements();
                } else {
                    sdkOthers.add(e1);
                }
            }

            List<Element> list_apk = root_apk.elements();
            for (Element e : list_apk) {
                if (!e.getName().equals("application")) {
                    apkOthers.add(e);
                }
            }

            //合并manifest下的权限等
            for (Element e : sdkOthers) {
                if (contain(apkOthers, e)) {
                    root_apk.remove(same);
                }
                root_apk.add((Element) e.clone());
            }
            //合并application节点下的meta-data,activity等；
            Element application = root_apk.element("application");
            List<Element> temp = application.elements();
            formatManifest(temp, pkgname);
            for (Element e : sdkApplication) {
                if (contain(temp, e)) {
                    application.remove(same);
                }
                application.add((Element) e.clone());
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(apk)), format);
            writer.write(document_apk);
            writer.close();
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        }
        return true;
    }

    public static void formatManifest(List<Element> temp, String pkgname) {
        for (Element e : temp) {
            String name = e.attribute("name").getText();
            if (name.startsWith(".")) {
                e.attribute("name").setValue(pkgname + name);
            }
        }
    }


    private static Element same;

    public static boolean contain(List<Element> list, Element element) {
        boolean flag = false;
        if (element.getName().equals("uses-sdk")) {
            for (Element e : list) {
                if (e.getName().equals("uses-sdk")) {
                    flag = true;
                    same = e;
                    break;
                }
            }
        } else {
            for (Element e : list) {
                if (element.getName().equals(e.getName()) && element.attribute("name").getText().equals(e.attribute("name").getText())) {
                    flag = true;
                    same = e;
                    break;
                }
            }
        }

        return flag;
    }

    public static boolean mergeMeta_data(String path, Map<String, String> map,String sdkid) {
        boolean flag = false;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(path));//必须指定文件的绝对路径
            //获取根节点对象
            Element rootElement = document.getRootElement();
            List<Element> metas = rootElement.element("application").elements("meta-data");
            for(Element e:metas){
                String s = e.attribute("name").getText();
                if(s.equals("com.lysdk.middlesdk.sdkid")){
                    e.attribute("value").setText(sdkid);
                    break;
                }
            }

            Element application = rootElement.element("application");
            for (String key : map.keySet()) {
                Element elem = DocumentHelper.createElement("meta-data");
                elem.addAttribute("android:name", key);
                elem.addAttribute("android:value", map.get(key));
                application.add(elem);
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(path)), format);
            writer.write(document);
            writer.close();
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return flag;
    }

    public static void saveSplashSetting(String path, String orientation) {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("properties");
            Element child = root.addElement("entry");
            child.addAttribute("key", "orientation");
            child.setText(orientation);
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public static String loadSplashSetting(String path) {
        String history = "";
        File file = new File(path);
        if (!file.exists()) {
            return "";
        }
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(path));//必须指定文件的绝对路径

            //获取根节点对象
            Element rootElement = document.getRootElement();
            List<Element> list = rootElement.elements();
            for (org.dom4j.Element e : list) {
                String key = e.attribute("key").getText();
                if (key.equals("orientation")) {
                    history = e.getText();
                    break;
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return history;
    }

    public static void save(File path,String key,String value){
        try {
            Document document = null;
            if (!path.exists()) {
                document = DocumentHelper.createDocument();
                Element root = document.addElement("properties");
                Element child = root.addElement("entry");
                child.addAttribute("key", key);
                child.setText(value);
            } else {
                SAXReader saxReader = new SAXReader();
                document = saxReader.read(path);

                //获取根节点对象
                Element rootElement = document.getRootElement();
                List<Element> list = rootElement.elements();
                for (org.dom4j.Element e : list) {
                    String k = e.attribute("key").getText();
                    if(k.equalsIgnoreCase(key)){
                        e.setText(value);
                        break;
                    }
                }

            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
            writer.write(document);
            writer.close();

        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public static String get(File file,String key){
        String result = "";
        if(file.exists()){
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(file);//必须指定文件的绝对路径

                //获取根节点对象
                Element rootElement = document.getRootElement();
                List<Element> list = rootElement.elements();
                for (org.dom4j.Element e : list) {
                    String k = e.attribute("key").getText();
                    if(k.equalsIgnoreCase(key)){
                        String value = e.getText();
                        result = value;
                        break;
                    }

                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return result;
    }

}
