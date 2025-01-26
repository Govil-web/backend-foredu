package com.foroescolar.exceptions.security.filters;

import com.foroescolar.exceptions.security.filters.token.*;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.foroescolar.exceptions.dtoserror.ErrorResponse;
import com.foroescolar.exceptions.dtoserror.ErrorResponseBuilder;
import com.foroescolar.exceptions.security.SecurityException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FilterErrorHandler {
    private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE;
    private static final String CHARSET = "UTF-8";

    private final ObjectMapper objectMapper;

    private final ErrorResponseBuilder errorResponseBuilder;

    public FilterErrorHandler(ObjectMapper objectMapper, ErrorResponseBuilder errorResponseBuilder) {
        this.objectMapper = objectMapper;
        this.errorResponseBuilder = errorResponseBuilder;
    }

    public void handleException(Exception ex, HttpServletResponse response) throws IOException {
        try {
            MDC.put("exceptionType", ex.getClass().getSimpleName());

            ErrorResponse errorResponse = createErrorResponse(ex);
            writeErrorResponse(errorResponse, response);
            logError(ex, errorResponse);
        } finally {
            MDC.remove("exceptionType");
        }
    }
    private ErrorResponse createErrorResponse(Exception ex) {
        return switch (ex) {
            case JWTDecodeException ignored ->
                    errorResponseBuilder.buildTokenError(
                            new TokenMalformedException("Token con formato inválido")
                    );
            case TokenExpiredException e ->
                    errorResponseBuilder.buildTokenError(e);
            case TokenInvalidatedException e ->
                    errorResponseBuilder.buildTokenError(e);
            case TokenInvalidException e ->
                    errorResponseBuilder.buildTokenError(e);
            case AuthenticationFailedException e ->
                    errorResponseBuilder.buildSecurityError(e);
            case TokenException e ->
                    errorResponseBuilder.buildTokenError(e);
            case SecurityException e ->
                    errorResponseBuilder.buildSecurityError(e);
            default -> {
                log.error("Excepción no manejada: {}", ex.getClass().getName(), ex);
                yield errorResponseBuilder.buildUnexpectedError(ex);
            }
        };
    }



    private void writeErrorResponse(ErrorResponse errorResponse, HttpServletResponse response) throws IOException {
        try {
            configureResponse(response, errorResponse);
            objectMapper.writeValue(response.getOutputStream(), errorResponse);
        } catch (IOException e) {
            log.error("Error al escribir la respuesta de error", e);
            throw e;
        }
    }

    private void configureResponse(HttpServletResponse response, ErrorResponse errorResponse) {
        response.setStatus(errorResponse.getStatus().value());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARSET);
    }

    private void logError(Exception ex, ErrorResponse errorResponse) {
        String message = String.format(
                "Error en filtro de seguridad - Código: %s, Mensaje: %s",
                errorResponse.getCode(),
                errorResponse.getMessage()
        );

        if (errorResponse.getStatus().is5xxServerError()) {
            log.error(message, ex);
        } else {
            log.warn(message);
            log.debug("Detalles de la excepción", ex);
        }
    }
    public void handleTokenExpired(HttpServletRequest request,
                                   HttpServletResponse response,
                                   TokenExpiredException ex) throws IOException {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .code("TOKEN_EXPIRED")
                .message("Su sesión ha expirado. Por favor, vuelva a iniciar sesión" + ex.getMessage())
                .details(createErrorDetails(request))
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARSET);

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private Map<String, String> createErrorDetails(HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();
        details.put("path", request.getRequestURI());
        details.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(LocalDateTime.now()));
        return details;
    }
}