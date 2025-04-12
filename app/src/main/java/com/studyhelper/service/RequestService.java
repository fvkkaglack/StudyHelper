package com.studyhelper.service;

import com.studyhelper.dto.request.RequestRequest;
import com.studyhelper.dto.response.RequestResponse;
import com.studyhelper.entity.Request;
import com.studyhelper.mapper.RequestMapper;
import com.studyhelper.repository.RequestRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    public RequestService(RequestRepository requestRepository, RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
    }

    public RequestResponse createRequest(@Valid RequestRequest request) {
        Request entity = requestMapper.toRequest(request);
        requestRepository.save(entity);
        return requestMapper.toResponse(entity);
    }
}