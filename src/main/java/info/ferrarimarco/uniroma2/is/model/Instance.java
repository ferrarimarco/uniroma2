package info.ferrarimarco.uniroma2.is.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Instance extends Entity{
    private Clazz instanceClass;
    private String barCode;
    private String brand;
    private String instanceCount;
}
