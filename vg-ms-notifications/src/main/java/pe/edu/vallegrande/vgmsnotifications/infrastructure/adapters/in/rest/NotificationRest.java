package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsnotifications.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsnotifications.application.dto.request.SendNotificationRequest;
import pe.edu.vallegrande.vgmsnotifications.application.dto.request.WhatsAppMessageRequest;
import pe.edu.vallegrande.vgmsnotifications.application.dto.response.NotificationResponse;
import pe.edu.vallegrande.vgmsnotifications.application.mappers.NotificationMapper;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.IGetNotificationHistoryUseCase;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.IRetryNotificationUseCase;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.ISendNotificationUseCase;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationRest {

    private final ISendNotificationUseCase sendUseCase;
    private final IGetNotificationHistoryUseCase historyUseCase;
    private final IRetryNotificationUseCase retryUseCase;
    private final NotificationMapper mapper;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<NotificationResponse>> send(
            @Valid @RequestBody SendNotificationRequest request) {
        return sendUseCase.send(
                request.getPhoneNumber(),
                request.getRecipientName(),
                request.getType(),
                request.getVariables() != null ? request.getVariables() : Map.of(),
                "API_MANUAL", null, request.getUserId())
                .map(mapper::toResponse)
                .map(response -> ApiResponse.success(response, "Notification sent"));
    }

    @PostMapping("/whatsapp")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<NotificationResponse>> sendWhatsApp(
            @Valid @RequestBody WhatsAppMessageRequest request) {
        NotificationType type = mapTemplateToType(request.getTemplate());
        Map<String, String> variables = extractVariables(request.getParams());

        String recipientName = variables.getOrDefault("name",
                variables.getOrDefault("firstName", "Usuario"));

        if (!variables.containsKey("name") && variables.containsKey("firstName")) {
            variables.put("name", recipientName);
        }

        return sendUseCase.send(
                request.getPhone(),
                recipientName,
                type,
                variables,
                "API_WHATSAPP", null, null)
                .map(mapper::toResponse)
                .map(response -> ApiResponse.success(response, "WhatsApp notification sent"));
    }

    private NotificationType mapTemplateToType(String template) {
        return switch (template.toLowerCase()) {
            case "welcome" -> NotificationType.WELCOME;
            case "profile-updated" -> NotificationType.PROFILE_UPDATED;
            case "account-deactivated" -> NotificationType.ACCOUNT_DEACTIVATED;
            case "account-restored" -> NotificationType.ACCOUNT_RESTORED;
            default -> NotificationType.CUSTOM;
        };
    }

    private Map<String, String> extractVariables(Map<String, Object> params) {
        if (params == null) {
            return new java.util.HashMap<>();
        }
        return params.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() != null ? e.getValue().toString() : "",
                        (v1, v2) -> v1,
                        java.util.HashMap::new));
    }

    @PostMapping("/{id}/retry")
    public Mono<ApiResponse<NotificationResponse>> retryOne(
            @Parameter(description = "Notification ID") @PathVariable String id) {
        return retryUseCase.retryOne(id)
                .map(mapper::toResponse)
                .map(response -> ApiResponse.success(response, "Retry executed"));
    }

    @PostMapping("/retry-all")
    public Mono<ApiResponse<List<NotificationResponse>>> retryAll() {
        return retryUseCase.retryAllFailed()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.success(list, "Massive retry: " + list.size() + " processed"));
    }

    @GetMapping
    public Mono<ApiResponse<List<NotificationResponse>>> findAll() {
        return historyUseCase.findAll()
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::success);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<NotificationResponse>> findById(
            @Parameter(description = "Notification ID") @PathVariable String id) {
        return historyUseCase.findById(id)
                .map(mapper::toResponse)
                .map(ApiResponse::success);
    }

    @GetMapping("/user/{userId}")
    public Mono<ApiResponse<List<NotificationResponse>>> findByUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        return historyUseCase.findByUserId(userId)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::success);
    }

    @GetMapping("/status/{status}")
    public Mono<ApiResponse<List<NotificationResponse>>> findByStatus(
            @Parameter(description = "Status: SENT, FAILED, PENDING") @PathVariable String status) {
        NotificationStatus notifStatus = NotificationStatus.valueOf(status.toUpperCase());
        return historyUseCase.findByStatus(notifStatus)
                .map(mapper::toResponse)
                .collectList()
                .map(ApiResponse::success);
    }
}
