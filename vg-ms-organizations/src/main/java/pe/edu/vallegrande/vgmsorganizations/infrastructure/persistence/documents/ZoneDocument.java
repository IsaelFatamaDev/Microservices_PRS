package pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "zones")
@CompoundIndex(name = "idx_zone_org_name", def = "{'organization_id': 1, 'zone_name': 1}", unique = true)
public class ZoneDocument {

    @Id
    private String id;

    @Indexed
    @Field("organization_id")
    private String organizationId;

    @Field("zone_name")
    private String zoneName;

    @Field("description")
    private String description;

    @Indexed
    @Field("record_status")
    private String recordStatus;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("updated_by")
    private String updatedBy;
}