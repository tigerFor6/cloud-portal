//package com.kuangheng.cloud.configurer;
//
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
////@EnableWebSocketMessageBroker
//@EnableConfigurationProperties(WebSocketStompProperties.class)
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    private final WebSocketStompProperties webSocketStompProperties;
//
//    public WebSocketConfig(WebSocketStompProperties webSocketStompProperties) {
//        this.webSocketStompProperties = webSocketStompProperties;
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        //客户端连接端点
//        registry.addEndpoint("/websocket")
//                .setAllowedOrigins("*")
//                .withSockJS();
//    }
//
//    /**
//     * 设置rabbitmq代理Stomp
//     *
//     * @param registry
//     */
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableStompBrokerRelay(webSocketStompProperties.getDestPrefixes())//允许代理推送的主题
//                .setRelayHost(webSocketStompProperties.getHost())
//                .setRelayPort(webSocketStompProperties.getPort())
//                .setClientLogin(webSocketStompProperties.getUsername())
//                .setClientPasscode(webSocketStompProperties.getPassword());
//        registry.setApplicationDestinationPrefixes(webSocketStompProperties.getAppDestPrefix());//设置客户端发送前缀
//    }
//}
