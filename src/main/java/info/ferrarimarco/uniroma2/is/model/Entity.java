package info.ferrarimarco.uniroma2.is.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Entity {
    @Id
    private String id;

    @NonNull
    private String name;
    private String symbolicId;

    // This field is used when an instance has a DTO role
    @Transient
    private List<String> selectedEntities;
}
