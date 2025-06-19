package com.studyhelper.mapper;

import com.studyhelper.dto.request.ChatMessageRequest;
import com.studyhelper.dto.response.ChatMessageResponse;
import com.studyhelper.entity.ChatMessage;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chat", ignore = true) // chat будет установлен вручную
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "content", source = "request.message")
    @Mapping(target = "sentAt", ignore = true) // Устанавливается через @PrePersist
    ChatMessage toChatMessage(ChatMessageRequest request, Task task, User sender, User receiver);

    @Mapping(target = "senderNickname", source = "senderNickname")
    @Mapping(target = "sentAt", source = "message.sentAt")
    ChatMessageResponse toMessageResponse(ChatMessage message, String senderNickname);
}