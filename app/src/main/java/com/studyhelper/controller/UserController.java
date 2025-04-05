package com.studyhelper.controller;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создаёт пользователя")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public UserResponse createUser(@RequestBody @Valid UserRequest request) {
        return userService.createUser(request);
    }

    @Operation(summary = "Обновляет пользователя")
    @PutMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public UserResponse updateUser(@PathVariable UUID id, @RequestBody @Valid UserRequest request) {
        return userService.updateUser(id, request);
    }

    @Operation(summary = "Удаление пользователя")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Предоставлят страницу пользователей")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageDto<UserResponse> getUserPage(@ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
        return userService.getUserPage(pageable);
    }
}
