package com.ryuhi.util.dubbo.test.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.InetAddresses;
import com.ryuhi.util.dubbo.test.core.Node;

import lombok.Data;

@Data
public class UrlParserUtil {

    private String url;

    private Node node;

    private Map<String, String> keyValues = new ConcurrentHashMap<>(16);

    private List<String> methods = null;

    public UrlParserUtil() {}

    /**
     *  url解析工具
     * @param url url地址，有可能来自zookeeper的返回值，也有可能是用户输入的zookee地址
     * @param isReturnData  true来自zookeeper的返回值，false是用户输入的zookee地址
     * @throws IOException 抛出IO异常
     */
    public UrlParserUtil(String url, boolean isReturnData) throws IOException {
        if (isReturnData) {
            String decodedStr = URLDecoder.decode(url, "UTF-8");
            this.url = decodedStr;
            decodedStr = decodedStr.replaceAll("://", "**");
            int index = decodedStr.indexOf("/");
            int index1 = decodedStr.indexOf("**");
            String nodeStr = decodedStr.substring(index1 + 2, index);
            this.node = generateNode(nodeStr);
            String allKvStr = decodedStr.substring(index);
            int index2 = allKvStr.indexOf("?");
            String kvStr = allKvStr.substring(index2 + 1);
            /*String[] kvArr = kvStr.split("&");
            for (String str : kvArr) {
                String[] everyKv = str.split("=");
                keyValues.put(everyKv[0], everyKv[1]);
            }*/
            keyValues.putAll(com.ryuhi.util.dubbo.test.util.StringUtils.parseQueryString(kvStr));
            this.methods = new CopyOnWriteArrayList<>(Arrays.asList(keyValues.getOrDefault("methods", "").split(",")));
        } else {
            this.url = url;
            this.node = generateNode(url);
            this.methods = Collections.emptyList();
        }
    }

    private Node generateNode(String url) throws IOException {
        Node node = new Node();
        if (StringUtils.isEmpty(url)) {
            throw new RuntimeException("Zookeeper服务器节点地址为空");
        }
        String[] ipArr = url.split(":");
        if (ipArr.length != 2) {
            throw new RuntimeException("Zookeeper服务器节点地址缺少ip地址或者端口号");
        }
        String host = ipArr[0];
        host = host.replaceAll("zookeeper://","");
        if (!InetAddresses.isInetAddress(host)) {
            throw new RuntimeException("Zookeeper服务器节点地址不是一个合法的ip地址或者域名");
        }
        Properties prop = PropertiesReader.getInstance().getLists("/ip.properties");
        String externalHost = prop.getProperty(host);
        if (StringUtils.isNotEmpty(externalHost)) {
            host = externalHost;
        }
        if (!StringUtils.isNumeric(ipArr[1])) {
            throw new RuntimeException("Zookeeper服务器节点端口号不是数字");
        }
        int port = Integer.valueOf(ipArr[1]);
        if (port < 1 || port > 65535) {
            throw new RuntimeException("Zookeeper服务器节点端口号超出端口号范围");
        }
        node.setHost(host);
        node.setPort(port);
        return node;
    }
}
