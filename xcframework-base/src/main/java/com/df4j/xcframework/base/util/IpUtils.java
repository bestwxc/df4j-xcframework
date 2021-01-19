package com.df4j.xcframework.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpUtils {

    private static Logger logger = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取本机的多个IP
     *
     * @return
     */
    public List<String> getIpv4IP() {
        List<String> list = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        list.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取本机IP出错", e);
        }
        return list;
    }
}
