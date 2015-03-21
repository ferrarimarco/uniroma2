package info.ferrarimarco.uniroma2.is.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public abstract class Entity {
    @Id
    private String id;
}
