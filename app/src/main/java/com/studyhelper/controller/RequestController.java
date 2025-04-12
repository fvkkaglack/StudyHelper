package com.studyhelper.controller;

import com.studyhelper.dto.request.RequestRequest;
import com.studyhelper.dto.response.RequestResponse;
import com.studyhelper.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/request")
@Tag(name = "Заявки", description = "API для управления заявками")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @Operation(summary = "Подать заявку на задание")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponse createRequest(@RequestBody @Valid RequestRequest request) {
        return requestService.createRequest(request);
    }
}
