package pe.edu.vallegrande.vgmsinfrastructure.application.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
    private final String code;
    private final String message;
    private final String field;
}
