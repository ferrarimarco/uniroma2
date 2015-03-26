package info.ferrarimarco.uniroma2.is.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Entity {
    @Id
    private String id;
    private String name;
    private String symbolicId;
}
