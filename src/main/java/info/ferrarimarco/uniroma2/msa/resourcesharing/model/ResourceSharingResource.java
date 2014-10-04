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
public class ResourceSharingResource {

	@Id
	private String id;

	private String title;
	private String description;
	private Double latitude;
	private Double longitude;
	private String locality;
	private String country;
	private DateTime creationTime;
	private String acquisitionMode;
	private String creatorId;

}
