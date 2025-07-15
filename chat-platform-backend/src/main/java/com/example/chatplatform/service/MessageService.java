package com.example.chatplatform.service;

import com.example.chatplatform.dto.SendMessageRequest;
import com.example.chatplatform.entity.Message;
import com.example.chatplatform.entity.User;
import com.example.chatplatform.repository.MessageRepository;
import com.example.chatplatform.repository.UserRepository;
import com.example.chatplatform.util.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private XmlUtils xmlUtils;

    private static final String MESSAGES_XML_FILE = "messages.xml";
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + java.io.File.separator + "uploads" + java.io.File.separator;

    @Transactional
    public Message sendMessage(SendMessageRequest request, Long senderId) {
        // Vérifier que l'expéditeur existe
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        // Vérifier le destinataire ou le groupe
        if (request.getReceiverId() == null && request.getGroupId() == null) {
            throw new RuntimeException("Un destinataire ou un groupe est requis");
        }
        Message message = new Message(
                senderId,
                request.getReceiverId(),
                request.getGroupId(),
                request.getContent(),
                request.getType(),
                request.getFilePath()
        );
        Message saved = messageRepository.save(message);
        saveMessagesToXml();
        return saved;
    }

    public List<Message> getMessagesForUser(Long userId) {
        return messageRepository.findByReceiverId(userId);
    }

    public List<Message> getMessagesForGroup(Long groupId) {
        return messageRepository.findByGroupId(groupId);
    }

    public List<Message> getMessagesSentByUser(Long userId) {
        return messageRepository.findBySenderId(userId);
    }

    // Récupérer tous les messages entre deux utilisateurs (dans les deux sens)
    public List<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        return messageRepository.findPrivateConversation(userId1, userId2);
    }

    @Transactional
    public String uploadFile(MultipartFile file, Long userId) throws IOException {
        // Validation du type MIME
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase() : "";
        // Extensions interdites
        String[] forbidden = {"exe", "bat", "sh", "js", "php", "pl", "py", "jar", "com", "msi"};
        for (String ext : forbidden) {
            if (extension.equals(ext)) {
                throw new IOException("Extension de fichier interdite : " + extension);
            }
        }
        // Détection du type MIME
        String mimeType = file.getContentType();
        if (mimeType == null || mimeType.equals("application/octet-stream")) {
            // Tentative de détection via extension
            mimeType = Files.probeContentType(Paths.get(originalFilename));
        }
        if (mimeType == null) {
            throw new IOException("Impossible de déterminer le type MIME du fichier");
        }
        // Limitation de taille (ex: 20 Mo)
        long maxSize = 20 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IOException("Fichier trop volumineux (max 20 Mo)");
        }
        // Stockage structuré
        LocalDate now = LocalDate.now();
        String dirPath = UPLOAD_DIR + userId + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue()) + "/";
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        String uniqueName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
        String filePath = dirPath + uniqueName;
        file.transferTo(new File(filePath));
        // Métadonnées
        Long fileSize = file.getSize();
        Integer width = null;
        Integer height = null;
        Double duration = null;
        String thumbnailPath = null;
        // Si image, générer miniature et extraire dimensions
        if (mimeType.startsWith("image/")) {
            BufferedImage img = ImageIO.read(new File(filePath));
            if (img != null) {
                width = img.getWidth();
                height = img.getHeight();
                // Générer miniature
                String thumbName = "thumb_" + uniqueName;
                thumbnailPath = dirPath + thumbName;
                createImageThumbnail(img, thumbnailPath, 200, 200);
            }
        }
        // TODO: Pour vidéo/audio, extraire la durée (nécessite dépendance externe si besoin)
        // Retourner toutes les infos nécessaires
        return String.join("|", filePath, mimeType, String.valueOf(fileSize),
                width != null ? width.toString() : "", height != null ? height.toString() : "",
                duration != null ? duration.toString() : "", thumbnailPath != null ? thumbnailPath : "");
    }

    // Génération de miniature pour image
    private void createImageThumbnail(BufferedImage img, String thumbnailPath, int maxWidth, int maxHeight) throws IOException {
        int width = img.getWidth();
        int height = img.getHeight();
        float ratio = Math.min((float)maxWidth / width, (float)maxHeight / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);
        Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage thumb = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumb.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        ImageIO.write(thumb, "jpg", new File(thumbnailPath));
    }

    @Transactional
    public Message markAsDelivered(Long messageId, Long userId) {
        Message message = messageRepository.findByIdAndReceiverId(messageId, userId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé ou accès interdit"));
        if (!Boolean.TRUE.equals(message.getDelivered())) {
            message.setDelivered(true);
            message.setDeliveredAt(java.time.LocalDateTime.now());
            Message saved = messageRepository.save(message);
            saveMessagesToXml();
            return saved;
        }
        return message;
    }

    @Transactional
    public Message markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findByIdAndReceiverId(messageId, userId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé ou accès interdit"));
        if (!Boolean.TRUE.equals(message.getRead())) {
            message.setRead(true);
            message.setReadAt(java.time.LocalDateTime.now());
            Message saved = messageRepository.save(message);
            saveMessagesToXml();
            return saved;
        }
        return message;
    }

    @Transactional
    public Message editMessage(Long messageId, Long userId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez éditer que vos propres messages");
        }
        message.setContent(newContent);
        message.setEditedAt(java.time.LocalDateTime.now());
        Message saved = messageRepository.save(message);
        saveMessagesToXml();
        return saved;
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres messages");
        }
        message.setDeleted(true);
        messageRepository.save(message);
        saveMessagesToXml();
    }

    public Page<Message> getMessagesForUserPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByReceiverIdAndDeletedFalseOrderBySentAtDesc(userId, pageable);
    }

    public Page<Message> getMessagesForGroupPaged(Long groupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByGroupIdAndDeletedFalseOrderBySentAtDesc(groupId, pageable);
    }

    public Page<Message> getMessagesSentByUserPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findBySenderIdAndDeletedFalseOrderBySentAtDesc(userId, pageable);
    }

    private void saveMessagesToXml() {
        List<Message> allMessages = messageRepository.findAll();
        xmlUtils.saveToXml(allMessages, MESSAGES_XML_FILE, Message.class);
    }

    // Adapter sendMessage pour prendre en compte les nouvelles métadonnées si type = file
    @Transactional
    public Message sendFileMessage(SendMessageRequest request, Long senderId, String fileMeta) {
        // fileMeta = filePath|mimeType|fileSize|width|height|duration|thumbnailPath
        String[] parts = fileMeta.split("\\|");
        String filePath = parts[0];
        String mimeType = parts[1];
        Long fileSize = parts[2].isEmpty() ? null : Long.parseLong(parts[2]);
        Integer width = parts[3].isEmpty() ? null : Integer.parseInt(parts[3]);
        Integer height = parts[4].isEmpty() ? null : Integer.parseInt(parts[4]);
        Double duration = parts[5].isEmpty() ? null : Double.parseDouble(parts[5]);
        String thumbnailPath = parts.length > 6 && !parts[6].isEmpty() ? parts[6] : null;
        Message message = new Message(
                senderId,
                request.getReceiverId(),
                request.getGroupId(),
                request.getContent(),
                request.getType(),
                filePath
        );
        message.setMimeType(mimeType);
        message.setFileSize(fileSize);
        message.setWidth(width);
        message.setHeight(height);
        message.setDuration(duration);
        message.setThumbnailPath(thumbnailPath);
        Message saved = messageRepository.save(message);
        saveMessagesToXml();
        return saved;
    }
} 