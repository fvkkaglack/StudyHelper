package com.studyhelper.mapper;

import com.studyhelper.dto.request.TaskRequest;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.entity.Request;
import com.studyhelper.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = ERROR)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true) // id генерируется автоматически
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "status", defaultValue = "OPEN")
    Task toTask(TaskRequest request);

    @Mapping(target = "status", expression = "java(task.getStatus() != null ? task.getStatus() : null)")
    @Mapping(target = "requestIds", source = "requests", qualifiedByName = "toRequestIds")
    TaskResponse toResponse(Task task);

    @Named("toRequestIds")
    default List<UUID> toRequestIds(Set<Request> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return null;
        }
        return requests.stream().map(Request::getId).toList();
    }

    @Mapping(target = "id", ignore = true) // id не обновляем
    @Mapping(target = "authorId", source = "request.authorId")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "reward", source = "request.reward")
    @Mapping(target = "deadline", source = "request.deadline")
    @Mapping(target = "executorId", source = "request.executorId")
    @Mapping(target = "status", expression = "java(request.status() != null ? request.status() : task.getStatus())")
    Task updateTask(Task task, TaskRequest request);
}