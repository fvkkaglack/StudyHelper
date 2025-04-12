package com.studyhelper.mapper;

import com.studyhelper.dto.request.DisputeRequest;
import com.studyhelper.dto.response.DisputeResponse;
import com.studyhelper.entity.Dispute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DisputeMapper {
    @Mapping(target = "id", ignore = true) // id генерируется автоматически
    @Mapping(target = "status", expression = "java(request.status() != null ? Dispute.DisputeStatus.valueOf(request.status()) : Dispute.DisputeStatus.PENDING)")
    Dispute toDispute(DisputeRequest request);

    @Mapping(target = "status", expression = "java(dispute.getStatus() != null ? dispute.getStatus().name() : null)")
    DisputeResponse toResponse(Dispute dispute);
}