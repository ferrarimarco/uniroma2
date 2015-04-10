package info.ferrarimarco.uniroma2.is.model.dto;

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
}
