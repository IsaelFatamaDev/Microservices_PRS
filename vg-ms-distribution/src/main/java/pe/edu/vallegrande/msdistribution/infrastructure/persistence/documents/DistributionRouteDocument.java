package pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "route")
@CompoundIndex(def = "{'record_status': 1, 'organization_id': 1}")
public class DistributionRouteDocument extends BaseDocument {

    @Indexed
    @Field("organization_id")
    private String organizationId;

    @Field("route_name")
    private String routeName;

    @Field("zones")
    private List<ZoneOrderEmbedded> zones;

    @Field("total_estimated_duration")
    private int totalEstimatedDuration;

    @Field("responsible_user_id")
    private String responsibleUserId;

    @Data
    public static class ZoneOrderEmbedded {
        @Field("zone_id")
        private String zoneId;
        private int order;
        @Field("estimated_duration")
        private int estimatedDuration;
    }
}
