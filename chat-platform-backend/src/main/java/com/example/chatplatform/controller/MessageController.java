package com.example.chatplatform.controller;

import com.example.chatplatform.dto.SendMessageRequest;
import com.example.chatplatform.entity.Message;
import com.example.chatplatform.service.MessageService;
import com.example.chatplatform.util.JwtUtil;
import com.example.chatplatform.repository.MessageRepository;
import com.example.chatplatform.repository.UserRepository;
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
import java.nio.file.Files;
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
    @Autowired
    private UserRepository userRepository;

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
            // Enrichir chaque message avec senderUsername
            List<Map<String, Object>> enriched = messages.stream().map(msg -> {
                Map<String, Object> m = new HashMap<>();
                // m.putAll(msg.toMap != null ? msg.toMap() : new HashMap<>()); // supprimé car toMap n'existe pas
                m.put("id", msg.getId());
                m.put("senderId", msg.getSenderId());
                m.put("content", msg.getContent());
                m.put("type", msg.getType());
                m.put("sentAt", msg.getSentAt());
                m.put("groupId", msg.getGroupId());
                m.put("filePath", msg.getFilePath());
                m.put("mimeType", msg.getMimeType());
                m.put("thumbnailPath", msg.getThumbnailPath());
                m.put("senderUsername", userRepository.findById(msg.getSenderId()).map(u -> u.getDisplayName() != null && !u.getDisplayName().isEmpty() ? u.getDisplayName() : u.getUsername()).orElse("?"));
                return m;
            }).toList();
            return ResponseEntity.ok(enriched);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            String fileMeta = messageService.uploadFile(file, userId);
            // fileMeta = filePath|mimeType|fileSize|width|height|duration|thumbnailPath
            String[] parts = fileMeta.split("\\|");
            Map<String, Object> response = new HashMap<>();
            response.put("filePath", parts[0]);
            response.put("mimeType", parts[1]);
            response.put("fileSize", parts[2]);
            response.put("width", parts[3]);
            response.put("height", parts[4]);
            response.put("duration", parts[5]);
            response.put("thumbnailPath", parts.length > 6 ? parts[6] : null);
            response.put("message", "Fichier uploadé avec succès");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de l'upload du fichier : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/send-file")
    public ResponseEntity<?> sendFileMessage(@Valid @RequestBody SendMessageRequest request,
                                             @RequestHeader("Authorization") String token,
                                             @RequestParam("fileMeta") String fileMeta) {
        try {
            Long senderId = jwtUtil.extractUserId(token.substring(7));
            Message message = messageService.sendFileMessage(request, senderId, fileMeta);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Message fichier envoyé avec succès");
            response.put("data", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/thumbnail/{messageId}")
    public ResponseEntity<?> getThumbnail(@PathVariable Long messageId, @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message non trouvé"));
            // Vérification des droits : l'utilisateur doit être destinataire, expéditeur ou membre du groupe
            if (!userId.equals(message.getSenderId()) &&
                (message.getReceiverId() == null || !userId.equals(message.getReceiverId())) &&
                (message.getGroupId() == null)) {
                throw new RuntimeException("Accès refusé à la miniature");
            }
            if (message.getThumbnailPath() == null) {
                throw new RuntimeException("Pas de miniature disponible pour ce message");
            }
            File file = new File(message.getThumbnailPath());
            if (!file.exists()) {
                throw new RuntimeException("Miniature non trouvée sur le serveur");
            }
            byte[] fileContent = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/preview/{messageId}")
    public ResponseEntity<?> previewMedia(@PathVariable Long messageId, @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message non trouvé"));
            // Vérification des droits : l'utilisateur doit être destinataire, expéditeur ou membre du groupe
            if (!userId.equals(message.getSenderId()) &&
                (message.getReceiverId() == null || !userId.equals(message.getReceiverId())) &&
                (message.getGroupId() == null)) {
                throw new RuntimeException("Accès refusé à la prévisualisation");
            }
            if (!"file".equals(message.getType()) || message.getFilePath() == null) {
                throw new RuntimeException("Ce message n'est pas un fichier média");
            }
            File file = new File(message.getFilePath());
            if (!file.exists()) {
                throw new RuntimeException("Fichier non trouvé sur le serveur");
            }
            String mimeType = message.getMimeType();
            byte[] fileContent = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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

    @PostMapping("/{messageId}/delivered")
    public ResponseEntity<?> markAsDelivered(@PathVariable Long messageId,
                                             @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            Message message = messageService.markAsDelivered(messageId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Message marqué comme livré");
            response.put("data", message);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long messageId,
                                        @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            Message message = messageService.markAsRead(messageId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Message marqué comme lu");
            response.put("data", message);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<?> editMessage(@PathVariable Long messageId,
                                         @RequestHeader("Authorization") String token,
                                         @RequestBody Map<String, String> body) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            String newContent = body.get("content");
            Message message = messageService.editMessage(messageId, userId, newContent);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Message édité avec succès");
            response.put("data", message);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId,
                                           @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            messageService.deleteMessage(messageId, userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/user/paged")
    public ResponseEntity<?> getMessagesForUserPaged(@RequestHeader("Authorization") String token,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            var messages = messageService.getMessagesForUserPaged(userId, page, size);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/group/{groupId}/paged")
    public ResponseEntity<?> getMessagesForGroupPaged(@PathVariable Long groupId,
                                                      @RequestHeader("Authorization") String token,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        try {
            var messagesPage = messageService.getMessagesForGroupPaged(groupId, page, size);
            // Enrichir chaque message avec senderUsername
            var enriched = messagesPage.getContent().stream().map(msg -> {
                Map<String, Object> m = new HashMap<>();
                // m.putAll(msg.toMap != null ? msg.toMap() : new HashMap<>()); // supprimé car toMap n'existe pas
                m.put("id", msg.getId());
                m.put("senderId", msg.getSenderId());
                m.put("content", msg.getContent());
                m.put("type", msg.getType());
                m.put("sentAt", msg.getSentAt());
                m.put("groupId", msg.getGroupId());
                m.put("filePath", msg.getFilePath());
                m.put("mimeType", msg.getMimeType());
                m.put("thumbnailPath", msg.getThumbnailPath());
                m.put("senderUsername", userRepository.findById(msg.getSenderId()).map(u -> u.getDisplayName() != null && !u.getDisplayName().isEmpty() ? u.getDisplayName() : u.getUsername()).orElse("?"));
                return m;
            }).toList();
            // Remplacer le contenu de la page par la liste enrichie
            Map<String, Object> pageMap = new HashMap<>();
            pageMap.put("content", enriched);
            pageMap.put("last", messagesPage.isLast());
            pageMap.put("totalPages", messagesPage.getTotalPages());
            pageMap.put("totalElements", messagesPage.getTotalElements());
            pageMap.put("number", messagesPage.getNumber());
            pageMap.put("size", messagesPage.getSize());
            return ResponseEntity.ok(pageMap);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/sent/paged")
    public ResponseEntity<?> getMessagesSentByUserPaged(@RequestHeader("Authorization") String token,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            var messages = messageService.getMessagesSentByUserPaged(userId, page, size);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/between")
    public ResponseEntity<?> getMessagesBetweenUsers(@RequestParam Long user1, @RequestParam Long user2, @RequestHeader("Authorization") String token) {
        try {
            // Optionnel : vérifier que l'utilisateur connecté est bien user1 ou user2
            Long connectedUserId = jwtUtil.extractUserId(token.substring(7));
            if (!connectedUserId.equals(user1) && !connectedUserId.equals(user2)) {
                throw new RuntimeException("Accès interdit à cette conversation");
            }
            List<Message> messages = messageService.getMessagesBetweenUsers(user1, user2);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
} 