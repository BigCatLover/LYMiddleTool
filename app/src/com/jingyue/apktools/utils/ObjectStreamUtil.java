package com.jingyue.apktools.utils;

import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.bean.SignConfig;
import com.jingyue.apktools.bean.SubscriptBean;
import com.jingyue.apktools.bean.UserBean;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ObjectStreamUtil {

    public static void saveSignInfo(String path, List<SignConfig> list) {
        File dir = new File(path);
        try {
            if(!dir.getParentFile().exists()){
                dir.getParentFile().mkdirs();
            }
            if(dir.exists()){
                dir.delete();
            }
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(dir));
            fos.writeInt(list.size());
            for(int i=0;i<list.size();i++){
                SignConfig sign = list.get(i);
                fos.writeUTF(sign.getSignPath());
                fos.writeUTF(sign.getAlias());
                fos.writeUTF(sign.getAlisPass());
                fos.writeUTF(sign.getKsPass());
                fos.writeBoolean(sign.isChecked());
                fos.writeUTF(sign.getCreateTime().get());
                fos.writeUTF(sign.getSignName());
            }
            fos.close();
        }catch (Exception e){
            LogUtils.e(e);
        }

    }

    public static void DeleteSign(String path,SignConfig sign){
        File dir = new File(path);
        List<SignConfig> list = getSignList(path);

        try {
            if(dir.exists()){
                dir.delete();
            }
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(dir));
            fos.writeInt(list.size());
            for(int i=0;i<list.size();i++){
                if(!list.get(i).getCreateTime().get().equals(sign.getCreateTime().get())){
                    SignConfig config = list.get(i);
                    fos.writeUTF(config.getSignPath());
                    fos.writeUTF(config.getAlias());
                    fos.writeUTF(config.getAlisPass());
                    fos.writeUTF(config.getKsPass());
                    fos.writeBoolean(config.isChecked());
                    fos.writeUTF(config.getCreateTime().get());
                    fos.writeUTF(config.getSignName());
                }

            }
            fos.close();
        }catch (Exception e){
            LogUtils.e(e);
        }
    }

    public static List<SignConfig> getSignList(String path){
        File file = new File(path);
        List<SignConfig> list = new ArrayList<>();
        if(file.exists()){
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
                int size = ois.readInt();
                for(int i =0;i<size;i++){
                    SignConfig config = new SignConfig();
                    config.setSignPath(ois.readUTF());
                    config.setAlias(ois.readUTF());
                    config.setAlisPass(ois.readUTF());
                    config.setKsPass(ois.readUTF());
                    config.setChecked(ois.readBoolean());
                    config.setCreateTime(ois.readUTF());
                    config.setSignName(ois.readUTF());
                    list.add(config);
                }
                return list;
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return list;
    }

    public static void saveDownCache(File path,Set<String> cache){
        File parent = new File(path.getParent());
        if(!parent.exists()){
            parent.mkdirs();
        }
        if(path.exists()){
            path.delete();
        }
        try {
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(path));
            fos.writeInt(cache.size());
            for(String key:cache){
                fos.writeUTF(key);
            }

            fos.close();
        }catch (Exception e){
            LogUtils.e(e);
        }
    }

    public static List<String> getDownCache(File path){
        List<String> list = new ArrayList<>();
        if(!path.exists()){
            return list;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            int size = ois.readInt();
            for(int i =0;i<size;i++){
                list.add(ois.readUTF());
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return list;
    }

    public static void saveSubscriptSetting(String path, String selectPos, HashMap<String,String> map){
        File dir = new File(path);
        File parent = new File(dir.getParent());
        if(!parent.exists()){
            dir.mkdirs();
        }
        try {
            if(dir.exists()){
               dir.delete();
            }
            dir.createNewFile();
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(dir));
            fos.writeUTF(selectPos);
            fos.writeInt(map.size());
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String)entry.getKey();
                String value = (String)entry.getValue();
                fos.writeUTF(key);
                fos.writeUTF(value);
            }
            fos.close();
        }catch (Exception e){
            LogUtils.e(e);
        }

    }
    public static SubscriptBean getSubscriptSetting(String path){
        File dir = new File(path);
        if(!dir.exists()){
            return null;
        }
        SubscriptBean bean = new SubscriptBean();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            bean.setPos(ois.readUTF());
            int size = ois.readInt();
            HashMap<String,String> map = new HashMap<>();
            for(int i=0;i<size;i++){
                map.put(ois.readUTF(),ois.readUTF());
            }
            bean.setCustSetting(map);
            ois.close();
            return bean;
        }catch (Exception e){
            LogUtils.e(e);
        }
        return bean;
    }

    public static Map<String, String> getSplash(File dir){
        Map<String, String> map = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                });
        if(dir.exists()&&dir.isDirectory()){
            for(File f:dir.listFiles()){
                if(f.isFile()&&f.getName().startsWith("sfonlie_splash_image")&&FileHelper.isSuffix(f,"png")){
                    map.put(f.getName(),f.getPath());
                }
            }
        }
        return map;
    }

    public static void saveSplash(String path,Map<String, String> map){
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            String val = (String)entry.getValue();
            FileHelper.copyFile(new File(val),new File(path,key));
        }
    }

    public static void unZip(File zipFile) {
        File pathFile = new File(zipFile.getParent());
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            //指定编码，否则压缩包里面不能有中文目录
            zip = new ZipFile(zipFile, Charset.forName("gbk"));
            for(Enumeration entries = zip.entries(); entries.hasMoreElements();){
                ZipEntry entry = (ZipEntry)entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (zipFile.getParent()+"/"+zipEntryName).replace("/", File.separator);
                //判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                if(!file.exists()){
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if(new File(outPath).isDirectory()){
                    continue;
                }

                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[2048];
                int len;
                while((len=in.read(buf1))>0){
                    out.write(buf1,0,len);
                }
                in.close();
                out.close();
            }
            //必须关闭，否则无法删除该zip文件
            zip.close();
            zipFile.delete();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    public static List<LocalPluginBean> loadLocalPlugins(String path){
        List<LocalPluginBean> list = new ArrayList<>();
        File sdks = new File(path);
        if (sdks.exists()) {
            for (File f : sdks.listFiles()) {
                if (f.isDirectory()) {
                    parsePlugin(f, list,false);
                }
            }
        }
        return list;
    }

    //解析插件信息
    public static void parsePlugin(File f, List<LocalPluginBean> list,boolean downfinish) {
        boolean flag = false;
        LocalPluginBean bean = contain(f.getName(),list);
        if(bean!=null){
            if(hasHighVer(f.getPath(),bean)){
                flag = true;
            }else {
                return;
            }
        }else {
            bean = new LocalPluginBean();
        }

        File xml = new File(f, "plugin.xml");
        if (xml.exists()) {
            bean.setPath(f.getPath());
            DomParseUtil.parsePluginXml(xml.getPath(), bean);
        }else {
            return;
        }
        File res = new File(f, "res");
        if (res.exists()) {
            if (bean.getTag() != null) {
                File icon = new File(res, bean.getTag() + ".png");
                if (icon.exists()) {
                    bean.setIcon(icon.getPath());
                }
            }
        }
        if(downfinish){
            bean.setUpdate(false);
        }

        if(!flag){
            list.add(bean);
        }

    }

    public static boolean hasHighVer(String name,LocalPluginBean bean){
        String ver = name.substring(name.lastIndexOf("(")+1,name.length()-1);
        String name1 = bean.getPath();
        String ver1 = name1.substring(name1.lastIndexOf("(")+1,name1.length()-1);
        if(ver.compareTo(ver1)>0){
            return true;
        }
        return false;
    }

    public static LocalPluginBean contain(String name,List<LocalPluginBean> list){
        LocalPluginBean b = null;
        if(name.contains("}")){
            String s = name.substring(0,name.lastIndexOf("}")+1);
            for(LocalPluginBean bean:list){
                if(bean.getPath().contains(s)){
                    b = bean;
                   break;
                }
            }
        }
        return b;
    }

    public static void saveUserInfo(File path,UserBean bean){
        File parent = new File(path.getParent());
        if(!parent.exists()){
            parent.mkdirs();
        }
        if(path.exists()){
            path.delete();
        }
        try {
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(path));
            fos.writeUTF(bean.getAccount());
            fos.writeUTF(bean.getPassword());
            fos.writeBoolean(bean.isAutoLogin());
            fos.writeBoolean(bean.isRememberPass());
            fos.close();
        }catch (Exception e){
            LogUtils.e(e);
        }
    }

    public static UserBean getUserInfo(File path){
        UserBean bean = new UserBean();
        if(path.exists()){
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
                bean.setAccount(ois.readUTF());
                bean.setPassword(ois.readUTF());
                bean.setAutoLogin(ois.readBoolean());
                bean.setRememberPass(ois.readBoolean());
                ois.close();
                return bean;
            }catch (Exception e){
                LogUtils.e(e);
            }
        }

        return bean;
    }
}

