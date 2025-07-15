package com.example.chatplatform.controller;

import com.example.chatplatform.dto.SendMessageRequest;
import com.example.chatplatform.entity.Message;
import com.example.chatplatform.service.MessageService;
import com.example.chatplatform.util.JwtUtil;
import com.example.chatplatform.repository.MessageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> sendMessage(@Valid @RequestBody SendMessageRequest request,
                                         @RequestHeader("Authorization") String token) {
        try {
            Long senderId = jwtUtil.extractUserId(token.substring(7));
            Message message = messageService.sendMessage(request, senderId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Message envoyé avec succès");
            response.put("data", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getMessagesForUser(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            List<Message> messages = messageService.getMessagesForUser(userId);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getMessagesForGroup(@PathVariable Long groupId,
                                                 @RequestHeader("Authorization") String token) {
        try {
            List<Message> messages = messageService.getMessagesForGroup(groupId);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = messageService.uploadFile(file);
            Map<String, String> response = new HashMap<>();
            response.put("filePath", filePath);
            response.put("message", "Fichier uploadé avec succès");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de l'upload du fichier : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/download/{messageId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long messageId) {
        try {
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message non trouvé"));
            if (!"file".equals(message.getType()) || message.getFilePath() == null) {
                throw new RuntimeException("Ce message n'est pas un fichier");
            }
            File file = new File(message.getFilePath());
            if (!file.exists()) {
                throw new RuntimeException("Fichier non trouvé sur le serveur");
            }
            FileInputStream fis = new FileInputStream(file);
            byte[] fileContent = fis.readAllBytes();
            fis.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", file.getName());
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<?> getMessagesSentByUser(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            List<Message> messages = messageService.getMessagesSentByUser(userId);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
} 