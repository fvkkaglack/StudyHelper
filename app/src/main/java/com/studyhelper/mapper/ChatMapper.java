package com.studyhelper.mapper;

import com.studyhelper.dto.response.ChatMessageResponse;
import com.studyhelper.dto.response.ChatResponse;
import com.studyhelper.entity.Chat;
import com.studyhelper.entity.ChatMessage;
import com.studyhelper.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserRepository.class})
public interface ChatMapper {
    @Mapping(target = "messages", source = "messages")
    ChatResponse toResponse(Chat chat);

    @Mapping(target = "senderNickname", source = "senderId", qualifiedByName = "mapSenderNickname")
    ChatMessageResponse toMessageResponse(ChatMessage message);

    @Named("mapSenderNickname")
    default String mapSenderNickname(UUID senderId, UserRepository userRepository) {
        return userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalStateException("Отправитель не найден"))
                .getNickname();
    }
}