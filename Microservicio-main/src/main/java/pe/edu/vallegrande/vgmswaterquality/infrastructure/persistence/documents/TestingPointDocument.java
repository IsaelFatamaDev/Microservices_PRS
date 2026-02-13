package pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "testing_points")
public class TestingPointDocument {

    @Id
    private String id;

    @Field("organization_id")
    private String organizationId;

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

    @Field("point_name")
    private String pointName;

    @Field("point_type")
    private String pointType;

    @Field("location")
    private String location;

    @Field("latitude")
    private Double latitude;

    @Field("longitude")
    private Double longitude;

    @Field("zone_id")
    private String zoneId;
}
