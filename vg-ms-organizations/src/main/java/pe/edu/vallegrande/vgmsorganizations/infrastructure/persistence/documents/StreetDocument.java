package pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents;

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
@Document(collection = "streets")
@CompoundIndex(name = "idx_street_zone_name", def = "{'zone_id': 1, 'street_name': 1}", unique = true)
public class StreetDocument {

    @Id
    private String id;

    @Indexed
    @Field("organization_id")
    private String organizationId;

    @Indexed
    @Field("zone_id")
    private String zoneId;

    @Field("street_type")
    private String streetType;

    @Field("street_name")
    private String streetName;

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