package com.jingyue.apktools.core.log;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.core.AppManager;
import com.jingyue.apktools.utils.HistoryUtil;
import com.jingyue.apktools.utils.TaskManager;
import javafx.application.Platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 自定义控制台
 * <p>
 * Created by linchaolong on 2015/9/7.
 */
public class LogManager {

    public static final String TAG = LogManager.class.getSimpleName();

    /**
     * 是否把输出到文件
     **/
    private boolean isLogFileOutput = true;

    /**
     * 日志打印输出代理
     **/
    private OutputDelegate outputDelegate = null;
    private OutputDelegate errOutputDelegate = null;

    /**
     * 日志文件输出流
     **/
    private FileOutputStream logFileOut = null;

    /**
     * 单例
     **/
    private static LogManager instance = null;
    private OutputListener outputListener = null;

    // 单例
    private LogManager() {
        outputRedirect();
    }

    /**
     * 日志输出重定向
     */
    private void outputRedirect() {
        try {
            // log file
            File logFile;
            File logOutDir = AppManager.getLogDir();

            // 按日期管理log
            String logFileName = "error.log";
            logFile = new File(logOutDir, logFileName);

            // log文件输出流
            logFileOut = new FileOutputStream(logFile, true);

            // 创建代理类
            outputDelegate = new OutputDelegate(System.out);
            errOutputDelegate = new OutputDelegate(System.err);

            // 设置日志输出监听
            outputDelegate.setOutputListener(new OutputListener() {
                @Override
                public void write(int b) {
                    try {
                        if (isLogFileOutput()) {
                            logFileOut.write(b);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void write(byte[] buf, int off, int len) {
                    String s = new String(buf);
                    int level = HistoryUtil.getInt(Config.kLogLevel);
                    try {
                        if (outputListener != null&&(level<=1)&&s.contains(": INFO: ")) {
                            outputListener.write(buf, off, len);
                            if (isLogFileOutput()) {
                                logFileOut.write(buf,off,len);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            errOutputDelegate.setOutputListener(new OutputListener() {
                @Override
                public void write(int b) {
                    try {
                        if (isLogFileOutput()) {
                            logFileOut.write(b);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void write(byte[] buf, int off, int len) {
                    try {
                        int level = HistoryUtil.getInt(Config.kLogLevel);
                        String s = new String(buf);
                        if (outputListener != null&&(level==2||level==0)&&s.contains(": WARNING: ")) {
                            outputListener.write(buf, off, len);
                        }
//                        if(s.contains(": WARNING: ")||s.contains(": ERROR: ")){
                            if (isLogFileOutput()) {
                                logFileOut.write(buf, off, len);
                            }
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            System.setOut(outputDelegate);
            System.setErr(errOutputDelegate);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    /**
     * 是否输出日志到文件，默认为true
     *
     * @return
     */
    public boolean isLogFileOutput() {
        return this.isLogFileOutput;
    }

    /**
     * 设置是否输出日志到文件
     *
     * @param isLogFileOutput
     */
    public void setIsLogFileOutput(boolean isLogFileOutput) {
        this.isLogFileOutput = isLogFileOutput;
    }

    /**
     * 设置日志输出监听
     *
     * @param listener OutputListener
     */
    public void setOutputListener(OutputListener listener) {
        outputListener = listener;
    }

    public void closeOutputFile(){
        try {
            if(logFileOut!=null){
                logFileOut.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

