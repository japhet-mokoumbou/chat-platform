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

    @Transactional
    public String uploadFile(MultipartFile file) throws IOException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();
        String filePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }

    private void saveMessagesToXml() {
        List<Message> allMessages = messageRepository.findAll();
        xmlUtils.saveToXml(allMessages, MESSAGES_XML_FILE, Message.class);
    }
} 