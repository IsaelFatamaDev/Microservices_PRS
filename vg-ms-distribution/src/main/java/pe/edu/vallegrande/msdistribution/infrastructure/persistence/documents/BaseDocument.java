package pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public abstract class BaseDocument {
    @Id
    private String id;
    private String recordStatus;
    @CreatedDate
    private LocalDateTime createdAt;
    private String createdBy;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String updatedBy;
}
