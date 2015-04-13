package info.ferrarimarco.uniroma2.is.model.dto;

import org.apache.commons.lang.StringUtils;

import info.ferrarimarco.uniroma2.is.model.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {
    private String categoryId;
    private String clazzId;
    private String productId;
    private Entity entity;
    private Double value;
    private String indexType;
    
    public boolean isEmpty() {
        return StringUtils.isBlank(categoryId) && StringUtils.isBlank(clazzId) && StringUtils.isBlank(productId);
    }
}
