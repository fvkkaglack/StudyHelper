package com.studyhelper.service;

import com.studyhelper.dto.request.DisputeRequest;
import com.studyhelper.dto.response.DisputeResponse;
import com.studyhelper.entity.Dispute;
import com.studyhelper.mapper.DisputeMapper;
import com.studyhelper.repository.DisputeRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DisputeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisputeService.class);

    private final DisputeRepository disputeRepository;
    private final DisputeMapper disputeMapper;

    public DisputeService(DisputeRepository disputeRepository, DisputeMapper disputeMapper) {
        this.disputeRepository = disputeRepository;
        this.disputeMapper = disputeMapper;
    }

    public DisputeResponse createDispute(@Valid DisputeRequest request) {
        Dispute dispute = disputeMapper.toDispute(request);
        disputeRepository.save(dispute);
        return disputeMapper.toResponse(dispute);
    }

    public DisputeResponse resolveDispute(UUID id, String resolution) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispute not found"));
        dispute.setStatus(Dispute.DisputeStatus.RESOLVED);
        dispute.setResolution(resolution);
        disputeRepository.save(dispute);
        return disputeMapper.toResponse(dispute);
    }
}