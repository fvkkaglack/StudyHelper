package com.studyhelper.mapper;

import com.studyhelper.dto.response.ChatResponse;
import com.studyhelper.entity.Chat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatResponse toResponse(Chat chat);
}