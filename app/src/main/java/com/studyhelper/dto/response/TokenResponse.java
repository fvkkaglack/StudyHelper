package com.studyhelper.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}