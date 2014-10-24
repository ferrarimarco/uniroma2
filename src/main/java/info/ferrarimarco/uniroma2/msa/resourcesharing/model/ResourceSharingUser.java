package info.ferrarimarco.uniroma2.msa.resourcesharing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceSharingUser {
	@Id
	private String userId;
	private String gcmId;
	private DateTime lastUpdate;
	private String address;
	private String locality;
	private String country;
	private Double latitude;
	private Double longitude;
	private Integer maxDistance;
}
