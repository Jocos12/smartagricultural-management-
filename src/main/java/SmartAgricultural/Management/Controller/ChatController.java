package SmartAgricultural.Management.Controller;


import SmartAgricultural.Management.Model.ChatMessage;
import SmartAgricultural.Management.Model.ChatNotification;
import SmartAgricultural.Management.Service.ChatService;
import SmartAgricultural.Management.Service.JwtService;
import SmartAgricultural.Management.Service.NotificationService;
import SmartAgricultural.Management.dto.ChatMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final JwtService jwtService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final NotificationService notificationService;

    public ChatController(JwtService jwtService, ChatService chatService,
                          SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.jwtService = jwtService;
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatService.saveMessage(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderId());
        return chatMessage;
    }

    @MessageMapping("/user.online")
    public void userOnline(@Payload Map<String, String> payload, Principal principal) {
        try {
            String userId = payload.get("userId");
            if (userId != null && principal != null) {
                chatService.updateUserStatus(userId, "ONLINE");
                messagingTemplate.convertAndSend("/topic/user.status", Map.of(
                        "userId", userId,
                        "status", "ONLINE",
                        "lastSeen", LocalDateTime.now()
                ));
            }
        } catch (Exception e) {
            logger.error("Error handling user online status", e);
        }
    }

    @MessageMapping("/user.offline")
    public void userOffline(@Payload Map<String, String> payload, Principal principal) {
        try {
            String userId = payload.get("userId");
            if (userId != null) {
                chatService.updateUserStatus(userId, "OFFLINE");
                messagingTemplate.convertAndSend("/topic/user.status", Map.of(
                        "userId", userId,
                        "status", "OFFLINE",
                        "lastSeen", LocalDateTime.now()
                ));
            }
        } catch (Exception e) {
            logger.error("Error handling user offline status", e);
        }
    }

    @GetMapping("/media/{messageId}")
    public ResponseEntity<?> getMediaFile(@PathVariable Long messageId,
                                          @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            ChatMessage message = chatService.getMessageById(messageId);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }

            if (!message.getSenderId().equals(username) && !message.getRecipientId().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            if (message.getFileUrl() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(Map.of(
                    "messageId", messageId,
                    "fileName", message.getFileName(),
                    "fileUrl", message.getFileUrl(),
                    "fileSize", message.getFileSize(),
                    "contentType", getContentTypeFromMessage(message),
                    "type", message.getType().toString()
            ));

        } catch (Exception e) {
            logger.error("Error fetching media file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching media file"));
        }
    }

    @GetMapping("/media/download/{messageId}")
    public ResponseEntity<Resource> downloadMediaFile(@PathVariable Long messageId,
                                                      @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            ChatMessage message = chatService.getMessageById(messageId);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }

            if (!message.getSenderId().equals(username) && !message.getRecipientId().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (message.getFileUrl() == null) {
                return ResponseEntity.notFound().build();
            }

            Resource fileResource = chatService.loadFileAsResource(message.getFileUrl());
            String contentType = getContentTypeFromMessage(message);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + message.getFileName() + "\"")
                    .body(fileResource);

        } catch (Exception e) {
            logger.error("Error downloading media file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/media/stream/{messageId}")
    public ResponseEntity<Resource> streamMediaFile(@PathVariable Long messageId,
                                                    @RequestHeader(value = "Authorization") String authHeader,
                                                    HttpServletRequest request) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            ChatMessage message = chatService.getMessageById(messageId);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }

            if (!message.getSenderId().equals(username) && !message.getRecipientId().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (message.getFileUrl() == null) {
                return ResponseEntity.notFound().build();
            }

            Resource fileResource = chatService.loadFileAsResource(message.getFileUrl());
            String contentType = getContentTypeFromMessage(message);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + message.getFileName() + "\"")
                    .body(fileResource);

        } catch (Exception e) {
            logger.error("Error streaming media file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/media/thumbnail/{messageId}")
    public ResponseEntity<Resource> getMediaThumbnail(@PathVariable Long messageId,
                                                      @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            ChatMessage message = chatService.getMessageById(messageId);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }

            if (!message.getSenderId().equals(username) && !message.getRecipientId().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (message.getType() == ChatMessage.MessageType.IMAGE) {
                Resource thumbnail = chatService.generateThumbnail(message.getFileUrl());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(thumbnail);
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            logger.error("Error generating thumbnail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversation/{recipientId}/media")
    public ResponseEntity<?> getConversationMedia(@PathVariable String recipientId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size,
                                                  @RequestParam(required = false) String type,
                                                  @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String senderId = jwtService.getEmailFromToken(token);

            List<ChatMessage> mediaMessages = chatService.getConversationMedia(
                    senderId, recipientId, type, page, size);

            List<Map<String, Object>> mediaList = mediaMessages.stream()
                    .map(message -> {
                        Map<String, Object> mediaInfo = new HashMap<>();
                        mediaInfo.put("id", message.getId());
                        mediaInfo.put("messageId", message.getId());
                        mediaInfo.put("fileName", message.getFileName());
                        mediaInfo.put("fileSize", message.getFileSize());
                        mediaInfo.put("type", message.getType().toString());
                        mediaInfo.put("timestamp", message.getTimestamp());
                        mediaInfo.put("senderId", message.getSenderId());
                        mediaInfo.put("fileUrl", "/api/chat/media/stream/" + message.getId());
                        mediaInfo.put("thumbnailUrl", message.getType() == ChatMessage.MessageType.IMAGE ?
                                "/api/chat/media/thumbnail/" + message.getId() : null);
                        mediaInfo.put("downloadUrl", "/api/chat/media/download/" + message.getId());
                        return mediaInfo;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "media", mediaList,
                    "total", mediaMessages.size(),
                    "page", page,
                    "size", size
            ));

        } catch (Exception e) {
            logger.error("Error fetching conversation media", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching media"));
        }
    }

    private String getContentTypeFromMessage(ChatMessage message) {
        switch (message.getType()) {
            case IMAGE:
                return message.getFileName().toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
            case VIDEO:
                return "video/mp4";
            case AUDIO:
                return "audio/mpeg";
            case DOCUMENT:
                if (message.getFileName().toLowerCase().endsWith(".pdf")) {
                    return "application/pdf";
                }
                return "application/octet-stream";
            default:
                return "application/octet-stream";
        }
    }

    @MessageMapping("/message.typing")
    public void userTyping(@Payload Map<String, String> payload, Principal principal) {
        try {
            String recipientId = payload.get("recipientId");
            String senderId = payload.get("senderId");
            boolean isTyping = Boolean.parseBoolean(payload.get("isTyping"));

            if (recipientId != null && senderId != null) {
                messagingTemplate.convertAndSendToUser(recipientId, "/queue/typing", Map.of(
                        "senderId", senderId,
                        "isTyping", isTyping
                ));
            }
        } catch (Exception e) {
            logger.error("Error handling typing indicator", e);
        }
    }

    @GetMapping("/history/{recipientId}")
    public ResponseEntity<?> getChatHistory(@PathVariable String recipientId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "50") int size,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing authentication token"));
            }

            String token = authHeader.substring(7);
            if (token.isEmpty() || token.split("\\.").length != 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Malformed token format"));
            }

            String senderId = jwtService.getEmailFromToken(token);
            if (senderId == null || senderId.isEmpty() || recipientId == null || recipientId.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Sender or recipient ID is invalid"));
            }

            List<ChatMessage> chatHistory = chatService.getChatHistoryPaginated(senderId, recipientId, page, size);
            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            logger.error("Error fetching chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching chat history: " + e.getMessage()));
        }
    }



    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {
        logger.info("╔════════════════════════════════════════╗");
        logger.info("║   RECEIVING PRIVATE MESSAGE VIA WS     ║");
        logger.info("╚════════════════════════════════════════╝");

        try {
            logger.info("📨 DTO Sender: {}", chatMessageDTO.getSenderId());
            logger.info("📨 DTO Recipient: {}", chatMessageDTO.getRecipientId());
            logger.info("📨 DTO Content: {}", chatMessageDTO.getContent());
            logger.info("📨 DTO Type: {}", chatMessageDTO.getType());
            logger.info("📨 Principal: {}", principal != null ? principal.getName() : "NULL");

            if (principal == null) {
                logger.error("❌ Principal is null - authentication required");
                messagingTemplate.convertAndSend("/queue/errors",
                        Map.of("error", "Authentication required"));
                return;
            }

            logger.info("🔨 Creating ChatMessage entity...");
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(chatMessageDTO.getContent());
            chatMessage.setSenderId(chatMessageDTO.getSenderId() != null ? chatMessageDTO.getSenderId() : principal.getName());
            chatMessage.setRecipientId(chatMessageDTO.getRecipientId());
            chatMessage.setType(chatMessageDTO.getType());
            chatMessage.setTimestamp(chatMessageDTO.getTimestamp() != null ? chatMessageDTO.getTimestamp() : LocalDateTime.now());
            chatMessage.setRead(false);
            chatMessage.setStatus("SENT");

            logger.info("✅ ChatMessage created:");
            logger.info("   - Sender: {}", chatMessage.getSenderId());
            logger.info("   - Recipient: {}", chatMessage.getRecipientId());
            logger.info("   - Content: {}", chatMessage.getContent());
            logger.info("   - Type: {}", chatMessage.getType());

            logger.info("🔍 Validating message...");
            validateChatMessage(chatMessage, principal);
            logger.info("✅ Validation passed");

            logger.info("💾 Calling chatService.saveMessage()...");
            ChatMessage savedMessage = chatService.saveMessage(chatMessage);

            if (savedMessage == null) {
                logger.error("❌ CRITICAL: savedMessage is NULL after saveMessage()!");
                throw new RuntimeException("Save returned null");
            }

            if (savedMessage.getId() == null) {
                logger.error("❌ CRITICAL: savedMessage.getId() is NULL!");
                throw new RuntimeException("Saved message has no ID");
            }

            logger.info("✅ Message saved successfully with ID: {}", savedMessage.getId());

            logger.info("📤 Sending to recipient: {}", chatMessage.getRecipientId());
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientId(),
                    "/queue/private",
                    savedMessage
            );

            logger.info("📤 Sending to sender: {}", chatMessage.getSenderId());
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getSenderId(),
                    "/queue/private",
                    savedMessage
            );

            logger.info("📊 Sending status update...");
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getSenderId(),
                    "/queue/message.status",
                    Map.of(
                            "messageId", savedMessage.getId(),
                            "status", "DELIVERED",
                            "timestamp", LocalDateTime.now()
                    )
            );

            logger.info("✅ MESSAGE PROCESSING COMPLETE");
            logger.info("════════════════════════════════════════");

        } catch (Exception e) {
            logger.error("❌❌❌ CRITICAL ERROR in sendPrivateMessage ❌❌❌");
            logger.error("Error type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Stack trace:", e);

            if (principal != null) {
                messagingTemplate.convertAndSendToUser(
                        principal.getName(),
                        "/queue/errors",
                        Map.of("error", "Failed to send message: " + e.getMessage())
                );
            }
        }
    }


    @PostMapping("/upload-media")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "recipientId", required = false) String recipientId,
            @RequestHeader(value = "Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing authentication token"));
            }

            String token = authHeader.substring(7);
            String senderId = jwtService.getEmailFromToken(token);
            if (senderId == null || senderId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No file provided"));
            }

            if (recipientId == null || recipientId.trim().isEmpty()) {
                recipientId = senderId;
            }

            String fileUrl = chatService.saveMediaFile(file);

            ChatMessage mediaMessage = new ChatMessage();
            mediaMessage.setSenderId(senderId);
            mediaMessage.setRecipientId(recipientId);
            mediaMessage.setContent("File sent: " + file.getOriginalFilename());
            mediaMessage.setFileName(file.getOriginalFilename());
            mediaMessage.setFileUrl(fileUrl);
            mediaMessage.setFileSize(file.getSize());
            mediaMessage.setTimestamp(LocalDateTime.now());
            mediaMessage.setStatus("SENT");

            String contentType = file.getContentType();
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    mediaMessage.setType(ChatMessage.MessageType.IMAGE);
                } else if (contentType.startsWith("video/")) {
                    mediaMessage.setType(ChatMessage.MessageType.VIDEO);
                } else if (contentType.startsWith("audio/")) {
                    mediaMessage.setType(ChatMessage.MessageType.AUDIO);
                } else if (contentType.equals("application/pdf")) {
                    mediaMessage.setType(ChatMessage.MessageType.DOCUMENT);
                } else {
                    mediaMessage.setType(ChatMessage.MessageType.FILE);
                }
            }

            ChatMessage savedMessage = chatService.saveMessage(mediaMessage);

            messagingTemplate.convertAndSendToUser(
                    recipientId,
                    "/queue/private",
                    savedMessage
            );

            return ResponseEntity.ok(savedMessage);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error saving file"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @MessageMapping("/message.read")
    public void markMessageAsRead(@Payload Map<String, Object> payload, Principal principal) {
        try {
            Long messageId = Long.valueOf(payload.get("messageId").toString());
            String senderId = payload.get("senderId").toString();

            chatService.markMessageAsRead(messageId, principal.getName());

            messagingTemplate.convertAndSendToUser(
                    senderId,
                    "/queue/message.status",
                    Map.of(
                            "messageId", messageId,
                            "status", "READ",
                            "readBy", principal.getName(),
                            "timestamp", LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            logger.error("Error marking message as read", e);
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadMessages(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing authentication token"));
            }

            String token = authHeader.substring(7);
            if (token.isEmpty() || token.split("\\.").length != 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Malformed token format"));
            }

            String username = jwtService.getEmailFromToken(token);
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user"));
            }

            List<ChatMessage> unreadMessages = chatService.getUnreadMessages(username);
            return ResponseEntity.ok(unreadMessages);
        } catch (Exception e) {
            logger.error("Error fetching unread messages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching unread messages: " + e.getMessage()));
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(@RequestHeader(value = "Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authentication token"));
            }

            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            List<ChatNotification> notifications = notificationService.getUnreadNotifications(username);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error fetching notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching notifications"));
        }
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id,
                                                    @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            notificationService.markNotificationAsRead(id, username);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            logger.error("Error marking notification as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error marking notification as read"));
        }
    }

    @GetMapping("/contacts/status")
    public ResponseEntity<?> getContactsStatus(@RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            Map<String, Object> contactsStatus = chatService.getContactsStatus(username);
            return ResponseEntity.ok(contactsStatus);
        } catch (Exception e) {
            logger.error("Error fetching contacts status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching contacts status"));
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendChatMessage(@RequestBody ChatMessage chatMessage,
                                             @RequestHeader(value = "Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing authentication token"));
            }

            String token = authHeader.substring(7);
            if (token.isEmpty() || token.split("\\.").length != 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Malformed token format"));
            }

            String senderId = jwtService.getEmailFromToken(token);

            if (!senderId.equals(chatMessage.getSenderId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Sender ID mismatch"));
            }

            if (chatMessage.getRecipientId() == null || chatMessage.getRecipientId().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Recipient required"));
            }

            if (chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Message cannot be empty"));
            }

            if (chatMessage.getTimestamp() == null) {
                chatMessage.setTimestamp(LocalDateTime.now());
            }

            chatMessage.setStatus("SENT");
            ChatMessage savedMessage = chatService.saveMessage(chatMessage);

            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientId(),
                    "/queue/private",
                    savedMessage
            );

            notificationService.createNotification(
                    senderId,
                    chatMessage.getRecipientId(),
                    "New message: " + chatMessage.getContent(),
                    ChatNotification.NotificationType.MESSAGE
            );

            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            logger.error("Error sending message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error sending message: " + e.getMessage()));
        }
    }

    @PostMapping("/mark-read")
    public ResponseEntity<?> markMessagesAsRead(@RequestBody Map<String, String> request,
                                                @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);
            String senderId = request.get("senderId");

            chatService.markAllMessagesAsRead(username, senderId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            logger.error("Error marking messages as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error marking messages as read"));
        }
    }

    @PostMapping("/mark-read/{messageId}")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId,
                                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing authentication token"));
            }

            String token = authHeader.substring(7);
            if (token.isEmpty() || token.split("\\.").length != 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Malformed token format"));
            }

            String username = jwtService.getEmailFromToken(token);
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user"));
            }

            chatService.markMessageAsRead(messageId, username);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            logger.error("Error marking message as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error marking message as read: " + e.getMessage()));
        }
    }

    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId,
                                           @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            boolean deleted = chatService.deleteMessage(messageId, username);
            if (deleted) {
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Cannot delete this message"));
            }
        } catch (Exception e) {
            logger.error("Error deleting message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting message"));
        }
    }

    @GetMapping("/media/preview/{messageId}")
    public ResponseEntity<Resource> getMediaPreview(
            @PathVariable Long messageId,
            @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            ChatMessage message = chatService.getMessageById(messageId);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }

            if (!message.getSenderId().equals(username) && !message.getRecipientId().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource previewResource = chatService.generateMediaPreview(message);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(previewResource);

        } catch (Exception e) {
            logger.error("Error generating media preview", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/media/metadata/{messageId}")
    public ResponseEntity<?> getMediaMetadata(
            @PathVariable Long messageId,
            @RequestHeader(value = "Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.getEmailFromToken(token);

            ChatMessage message = chatService.getMessageById(messageId);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }

            if (!message.getSenderId().equals(username) && !message.getRecipientId().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            return ResponseEntity.ok(Map.of(
                    "messageId", messageId,
                    "fileName", message.getFileName(),
                    "fileSize", message.getFileSize(),
                    "type", message.getType().toString(),
                    "timestamp", message.getTimestamp(),
                    "senderId", message.getSenderId()
            ));

        } catch (Exception e) {
            logger.error("Error fetching media metadata", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching media metadata"));
        }
    }

    private void validateChatMessage(ChatMessage message, Principal principal) {
        if (principal != null &&
                message.getSenderId() != null &&
                !message.getSenderId().equals(principal.getName())) {
            throw new ValidationException("Sender mismatch");
        }

        if (message.getRecipientId() == null || message.getRecipientId().isEmpty()) {
            throw new ValidationException("Recipient required");
        }

        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new ValidationException("Message cannot be empty");
        }
    }
}