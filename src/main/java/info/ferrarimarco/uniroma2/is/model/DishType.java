package info.ferrarimarco.uniroma2.is.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DishType extends Entity{
    private String superTypeId;
    private List<String> subTypeIds;
    private String name;
    private boolean isLeaf;
}
