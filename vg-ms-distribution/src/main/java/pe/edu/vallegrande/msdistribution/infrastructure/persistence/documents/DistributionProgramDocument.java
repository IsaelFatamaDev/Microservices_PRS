package pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "program")
@CompoundIndex(def = "{'record_status': 1, 'organization_id': 1}")
public class DistributionProgramDocument extends BaseDocument {

    @Indexed
    @Field("organization_id")
    private String organizationId;

    @Field("schedule_id")
    private String scheduleId;

    @Field("route_id")
    private String routeId;

    @Field("zone_id")
    private String zoneId;

    @Field("street_id")
    private String streetId;

    @Field("program_date")
    private String programDate;

    @Field("planned_start_time")
    private String plannedStartTime;

    @Field("planned_end_time")
    private String plannedEndTime;

    @Field("actual_start_time")
    private String actualStartTime;

    @Field("actual_end_time")
    private String actualEndTime;

    @Field("responsible_user_id")
    private String responsibleUserId;

    @Field("observations")
    private String observations;
}
