//package com.kuangheng.cloud.activity.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.Map;
//
//
//@Controller
//public class WebsocketController {
//    @Autowired
//    private SimpMessagingTemplate template;
//
//    @MessageMapping("/sendToAll")
//    public String sendToAll(String msg) {
//        return msg;
//    }
//
//    @MessageMapping("/send")
//    @SendTo("/topic")
//    public String say(String msg) {
//        return msg;
//    }
//
//    @ResponseBody
//    @GetMapping("/event/send")
//    public String msgReply(@RequestParam String msg) {
//        template.convertAndSend("/topic", msg);
//        return msg;
//    }
//
//    @ResponseBody
//    @GetMapping("/event/sendToAllByTemplate")
//    @MessageMapping("/sendToAllByTemplate")
//    public void sendToAllByTemplate(@RequestParam String msg) {
//        template.convertAndSend("/topic/chat", msg);
//    }
//
//    @MessageMapping("/sendToUser")
//    public void sendToUser(Map<String, String> params) {
//        String fromUserId = params.get("fromUserId");
//        String toUserId = params.get("toUserId");
//        String msg = "来自" + fromUserId + "消息:" + params.get("msg");
//
//        String dest = "/queue/user_" + toUserId;
//        template.convertAndSend(dest, msg);
//    }
//
//    /**
//     * 给用户发送消息
//     *
//     * @param msg
//     * @param toUserId
//     * @return
//     */
//    @ResponseBody
//    @GetMapping("/event/sendToUser")
//    public String sendToUser(String msg, String toUserId) {
//        String dest = "/queue/user_" + toUserId;
//        template.convertAndSend(dest, msg);
//
//        return "ok";
//    }
//
//}
