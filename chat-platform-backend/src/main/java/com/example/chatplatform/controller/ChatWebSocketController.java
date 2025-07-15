package com.example.chatplatform.controller;

import com.example.chatplatform.entity.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {
    // Quand un client envoie un message à /app/chat, il est diffusé à tous les abonnés de /topic/messages
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message broadcastMessage(Message message) {
        // Ici, on peut ajouter de la logique (ex: enrichir le message, logs, etc.)
        return message;
    }
} 