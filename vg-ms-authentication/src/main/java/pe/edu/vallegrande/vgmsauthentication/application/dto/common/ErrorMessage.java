package pe.edu.vallegrande.vgmsauthentication.application.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
    private String field;
    private String message;
    private String errorCode;
    private Integer status;

    public static ErrorMessage validation(String field, String message, String errorCode) {
        return ErrorMessage.builder()
            .field(field)
            .message(message)
            .errorCode(errorCode)
            .status(400)
            .build();
    }

    public static ErrorMessage of(String message, String errorCode, int status) {
        return ErrorMessage.builder()
            .message(message)
            .errorCode(errorCode)
            .status(status)
            .build();
    }
}