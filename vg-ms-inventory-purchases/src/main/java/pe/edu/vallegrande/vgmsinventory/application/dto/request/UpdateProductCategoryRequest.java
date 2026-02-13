package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UpdateProductCategoryRequest {
    private final String categoryName;
    private final String description;
}
