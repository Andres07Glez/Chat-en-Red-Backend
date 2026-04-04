package mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response;

public record ConversationKeyResponse(
        String encryptedKey,
        String iv,
        String creatorPublicKey
) {
}
