package com.studyhelper.mapper;

import com.studyhelper.dto.request.DisputeRequest;
import com.studyhelper.dto.response.DisputeResponse;
import com.studyhelper.entity.Dispute;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DisputeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "complainantId", source = "user.id")
    @Mapping(target = "reason", source = "request.reason")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "resolution", ignore = true)
    Dispute toDispute(DisputeRequest request, Task task, User user);

    @Mapping(target = "status", expression = "java(dispute.getStatus() != null ? dispute.getStatus().name() : null)")
    DisputeResponse toResponse(Dispute dispute);
}