package dev.bookt.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bookt.common.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class IdempotencyFilter extends OncePerRequestFilter {
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public IdempotencyFilter(IdempotencyKeyRepository idempotencyKeyRepository) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 4096);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String idempotencyKey = request.getHeader("Idempotency-Key");
        ObjectMapper objectMapper = new ObjectMapper();

        if (idempotencyKey == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String json = objectMapper.writeValueAsString(new ErrorResponse("Idempotency-Key header is required"));
            response.setContentType("application/json");
            response.getWriter().write(json);
            return;
        }

        Optional<IdempotencyKey> existingKeyOptional = idempotencyKeyRepository.findByKey(idempotencyKey);

        if (existingKeyOptional.isPresent()) {
            wrappedRequest.getInputStream().readAllBytes();
            String requestBodyHash = String.valueOf(wrappedRequest.getContentAsString().hashCode());
            IdempotencyKey existingKey = existingKeyOptional.get();

            if (existingKey.getRequestBodyHash().equals(requestBodyHash)) {
                response.setStatus(existingKey.getResponseStatus());
                response.getWriter().write(existingKey.getResponseBody());
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
                String json = objectMapper.writeValueAsString(new ErrorResponse("Different body detected for the same key"));
                response.getWriter().write(json);
                return;
            }
        }

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        String requestBodyHash = String.valueOf(wrappedRequest.getContentAsString().hashCode());
        short status = (short) wrappedResponse.getStatus();
        String responseBody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);

        IdempotencyKey newRecord = new IdempotencyKey(idempotencyKey, requestBodyHash, status, responseBody);
        idempotencyKeyRepository.save(newRecord);

        wrappedResponse.copyBodyToResponse();
    }
}
