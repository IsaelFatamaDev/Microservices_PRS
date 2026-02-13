package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class CreateProductCategoryRequest {
    @NotBlank(message = "Organization ID is required")
    private final String organizationId;
    @NotBlank(message = "Category name is required")
    private final String categoryName;
    private final String description;
}
