//package com.kuangheng.cloud.configurer;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
//
//import java.io.Serializable;
//
//@ConfigurationProperties("stomp")
//@ConfigurationPropertiesScan("classpath*:/")
//public class WebSocketStompProperties implements Serializable {
//
//    private String host;
//
//    private int port;
//
//    private String username;
//
//    private String password;
//
//    private String appDestPrefix;
//
//    private String userDestPrefix;
//
//    private String[] destPrefixes;
//
//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getAppDestPrefix() {
//        return appDestPrefix;
//    }
//
//    public void setAppDestPrefix(String appDestPrefix) {
//        this.appDestPrefix = appDestPrefix;
//    }
//
//    public String getUserDestPrefix() {
//        return userDestPrefix;
//    }
//
//    public void setUserDestPrefix(String userDestPrefix) {
//        this.userDestPrefix = userDestPrefix;
//    }
//
//    public String[] getDestPrefixes() {
//        return destPrefixes;
//    }
//
//    public void setDestPrefixes(String[] destPrefixes) {
//        this.destPrefixes = destPrefixes;
//    }
//}