package pe.edu.vallegrande.vgmsclaims.application.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standard class for error messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
    
    private String code;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;
    
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
    
    public static ErrorMessage of(String code, String message) {
        return ErrorMessage.builder()
                .code(code)
                .message(message)
                .build();
    }
    
    public static ErrorMessage of(String code, String message, String path) {
        return ErrorMessage.builder()
                .code(code)
                .message(message)
                .path(path)
                .build();
    }
}
