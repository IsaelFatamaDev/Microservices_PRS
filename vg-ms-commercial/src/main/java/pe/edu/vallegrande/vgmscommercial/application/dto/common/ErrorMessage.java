package pe.edu.vallegrande.vgmscommercial.application.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {

     private String message;
     private String errorCode;
     private Integer status;
     private String field;

     public static ErrorMessage of(String message, String errorCode, Integer status) {
          return ErrorMessage.builder()
                    .message(message)
                    .errorCode(errorCode)
                    .status(status)
                    .build();
     }

     public static ErrorMessage validation(String field, String message, String errorCode) {
          return ErrorMessage.builder()
                    .field(field)
                    .message(message)
                    .errorCode(errorCode)
                    .status(400)
                    .build();
     }
}
