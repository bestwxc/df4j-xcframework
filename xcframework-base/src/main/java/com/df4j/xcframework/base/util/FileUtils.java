package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.XcException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.*;

/**
 * 文件工具类
 */
public class FileUtils {

    private static String defalutCharset = "UTF-8";

    /**
     * 将输入流保存进指定文件
     * @param inputStream
     * @param file
     */
    public static void saveFile(InputStream inputStream,String file){
        saveFile(inputStream,new File(file));
    }

    /**
     * 将输入流保存进指定文件
     * @param inputStream
     * @param file
     */
    public static void saveFile(InputStream inputStream, File file){
        FileOutputStream fos = null;
        Exception exception = null;
        try {
            File folder = file.getParentFile();
            if(folder != null && !folder.exists()){
                folder.mkdirs();
            }
            fos = new FileOutputStream(file);
            int length = 0;
            byte[] buf = new byte[1024];
            while ((length = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, length);
            }
            fos.flush();
        }catch (Exception e){
            exception = e;
            throw new XcException("文件读写异常",exception);
        }finally {
            close(fos,exception);
        }
    }

    public static void close(Closeable closeable) {
        close(closeable, null);
    }

    public static void close(Closeable closeable, Throwable t) {
        if(!ObjectUtils.isEmpty(closeable)) {
            try {
                closeable.close();
            }catch (Exception e){
                if(!ObjectUtils.isEmpty(t)) {
                    t.addSuppressed(e);
                }
            }
        }
    }

    /**
     * 读取文件成字符串
     * @param file
     * @param charset
     * @return
     */
    public static String readFile(File file,String charset){
        Assert.notNull(file, "file对象不能为空！");
        Assert.isTrue(file.exists(), "file不存在！");
        InputStream inputStream = null;
        Exception exception = null;
        try {
            inputStream = new FileInputStream(file);
            int length = inputStream.available();
            byte[] data = new byte[length];
            inputStream.read(data);
            return new String(data, charset);
        }catch (IOException e){
            exception = e;
            throw new XcException("读取文件出错", e);
        }finally {
            close(inputStream, exception);
        }
    }

    /**
     * 读取文件成字符串
     * @param file
     * @return
     */
    public static String readFile(File file){
        return readFile(file, defalutCharset);
    }


    /**
     * 读取文件成字符串
     * @param filePath
     * @param charset
     * @return
     */
    public static String readFile(String filePath, String charset){
        File file = new File(filePath);
        return readFile(file,charset);
    }

    public static String readFile(String filePath) {
        return readFile(filePath,defalutCharset);
    }
}
