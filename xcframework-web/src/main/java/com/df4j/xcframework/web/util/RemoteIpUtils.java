package com.df4j.xcframework.web.util;


import javax.servlet.http.HttpServletRequest;

/**
 * 获取客户端IP工具类
 */
public class RemoteIpUtils {

    /**
     * 获取客户端IP
     * @param request {@link HttpServletRequest}对象
     * @return 客户端Ip
     */
    public static String getRemoteIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }
}
