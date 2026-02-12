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
@Document(collection = "schedule")
@CompoundIndex(def = "{'record_status': 1, 'organization_id': 1}")
public class DistributionScheduleDocument extends BaseDocument {

    @Indexed
    @Field("organization_id")
    private String organizationId;

    @Field("zone_id")
    private String zoneId;

    @Field("street_id")
    private String streetId;

    @Field("schedule_name")
    private String scheduleName;

    @Field("days_of_week")
    private List<String> daysOfWeek;

    @Field("start_time")
    private String startTime;

    @Field("end_time")
    private String endTime;

    @Field("duration_hours")
    private Integer durationHours;
}
