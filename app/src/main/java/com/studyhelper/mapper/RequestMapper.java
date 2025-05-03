package com.studyhelper.mapper;

import com.studyhelper.dto.request.RequestRequest;
import com.studyhelper.dto.response.RequestResponse;
import com.studyhelper.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface   RequestMapper {
    Request toRequest(RequestRequest requestRequest);

    RequestResponse toResponse(Request request);
}