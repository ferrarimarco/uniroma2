package info.ferrarimarco.uniroma2.is.model.dto;

import org.apache.commons.lang.StringUtils;

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
    private Double successValue;
    private Double likingValue;
    private Double perishabilityValue;
    private String indexType;
    
    public boolean isEmpty() {
        return StringUtils.isBlank(categoryId) && StringUtils.isBlank(clazzId) && StringUtils.isBlank(productId);
    }
}
