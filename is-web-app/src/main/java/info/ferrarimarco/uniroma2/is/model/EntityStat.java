package info.ferrarimarco.uniroma2.is.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class EntityStat extends Entity{
    private String entityId;
    private String parentEntityId;
    private Long requested = 0L;
    private Long dispensed = 0L;
    private Long expired = 0L;
    private Long stocked = 0L;
    private Long defected = 0L;
}
