package com.studyhelper.controller;

import com.studyhelper.dto.request.DisputeRequest;
import com.studyhelper.dto.response.DisputeResponse;
import com.studyhelper.service.DisputeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/dispute")
@Tag(name = "Спор", description = "API для управления спорами")
public class DisputeController {
    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @Operation(summary = "Создать спор по заданию")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DisputeResponse createDispute(@RequestBody @Valid DisputeRequest request) {
        return disputeService.createDispute(request);
    }

    @Operation(summary = "Разрешить спор")
    @PutMapping(path = "/{id}/resolve", produces = APPLICATION_JSON_VALUE)
    public DisputeResponse resolveDispute(@PathVariable UUID id, @RequestBody String resolution) {
        return disputeService.resolveDispute(id, resolution);
    }
}