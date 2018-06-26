package com.jingyue.apktools.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
	
	/** 日志级别 **/
	public static final int VERBOSE = 0;
	public static final int INFO = 1;
	public static final int WARING = 2;
    public static final int ERROR = 3;
    public static StringBuilder verboseSbuild = new StringBuilder();
    public static StringBuilder infoSbuild = new StringBuilder();
    public static StringBuilder warningSbuild = new StringBuilder();
	
	/** 当前日志级别 **/
	private static int mLogLevel = VERBOSE;
	public static void setLogLevel(int logLevel){
		mLogLevel = logLevel;
	}
	public static int getLogLevel(){
		return mLogLevel;
	}

    // 得到日期时间的DateFormat对象
    private static DateFormat localDateFromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取当前堆栈元素
	 *
	 * @return
	 */
	private static StackTraceElement getCurrentStackTraceElement() {
        return Thread.currentThread().getStackTrace()[6]; // 0是调用的栈顶元素，也就是当前方法，索引0-5都是Debug里的方法，6是调用Logger的所在方法
	}

    /**
     * 日期Log
     *
     * @return
     */
    public static String getDateLog(){
        StringBuilder sBuff = new StringBuilder();
        sBuff.append(localDateFromat.format(new Date()));
        return sBuff.toString();
    }

    public static String format(StackTraceElement s){
        //return String.format("%s.%s(%s:%s)%n", s.getClassName(), s.getMethodName(),s.getFileName(), s.getLineNumber());
        return String.format(".(%s:%s)",s.getFileName(), s.getLineNumber());
    }

	/** 
	 * 格式化log
	 * 
	 * @param logLevel		log级别
	 * @param log				log
	 * @return
	 */
	private static String format(int logLevel, String log) {
        StringBuilder sBuff = new StringBuilder();
        sBuff.append(getDateLog()).append(": ");
        // 日志级别
        switch (logLevel){
            case VERBOSE:
                sBuff.append("VERBOSE");
            break;
            case INFO:
                sBuff.append("INFO");
                break;
            case WARING:
                sBuff.append("WARNING");
                break;
            case ERROR:
                sBuff.append("ERROR");
                break;
        }
        sBuff.append(": ");
        // log
        if (log != null){
            sBuff.append(log);
        }else{
            sBuff.append("null");
        }
		return sBuff.toString();
	}

	/**
	 * 日志输出
	 *
	 * @param log
	 */
	private static void stdOutput(String log,int logLevel){
	    if(logLevel==INFO){
	        infoSbuild.append(log).append("\n");
            verboseSbuild.append(log).append("\n");
        }
		System.out.println(log);
	}

	/**
	 * 输出错误日志（红色）
	 *
	 * @param log
	 */
	private static void errOutput(String log,int logLevel){
	    if(logLevel==WARING){
	        warningSbuild.append(log).append("\n");
            verboseSbuild.append(log).append("\n");
        }
		System.err.println(log);
	}

	public static void clear(){
	    warningSbuild = new StringBuilder();
        infoSbuild = new StringBuilder();
        verboseSbuild = new StringBuilder();
	}

	/**
	 * 格式化并输出
	 * 
	 * @param logLevel		log级别
	 * @param log				log
	 */
	private static void formatOutput(int logLevel, String log) {
        if(logLevel <= INFO){
            stdOutput(format(logLevel, log),logLevel);
        }else{
            errOutput(format(logLevel, log),logLevel);
        }
	}

	// 不同级别的日志输出函数

    public static void i(String msg){
        if (INFO >= mLogLevel) {
            formatOutput(INFO, msg);
        }
    }

	public static void w(String msg){
		if (WARING >= mLogLevel) {
            formatOutput(WARING, msg);
		}
	}
    public static void e(String msg){
        if (ERROR >= mLogLevel) {
            formatOutput(ERROR, msg);
        }
    }

    public static void e(Throwable e){
        if(ERROR >= mLogLevel && e != null ){
            errOutput(e.getMessage(),ERROR);
        }
    }

    public static String getVerbose(){
	    return verboseSbuild.toString();
    }
    public static String getInfo(){
        return infoSbuild.toString();
    }
    public static String getWarning(){
        return warningSbuild.toString();
    }
}
