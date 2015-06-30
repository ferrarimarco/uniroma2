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
    private String symbolicId;
    private String categoryName;
    private String clazzName;
    private String productName;
    private String entityName;
    private Double value;
    private String indexType;
    
    public boolean isEmpty() {
        return StringUtils.isBlank(categoryId) && StringUtils.isBlank(clazzId) && StringUtils.isBlank(productId);
    }
}
