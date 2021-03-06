package info.ferrarimarco.uniroma2.is.controller.application;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.service.StatService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatsApplicationController extends AbstractApplicationController{
    
    public StatsApplicationController(@NonNull StatService statService) {
        super(null, null, null, null, statService);
    }

    public Double computeIndex(String indexType, String criteriaId){
        double result = 0.0;
        try{
            switch(indexType){
            case Constants.SUCCESS_INDEX:
                result = statService.success(criteriaId);
                break;
            case Constants.LIKING_INDEX:
                result = statService.liking(criteriaId);
                break;
            case Constants.PERISHABILITY_INDEX:
                result = statService.perishability(criteriaId);
                break;
            case Constants.DEFECTING_INDEX:
                result = statService.defecting(criteriaId);
                break;
            default:
                throw new IllegalArgumentException("Cannot choose a index for: " + indexType);
            }
        }catch(ArithmeticException e){
            log.warn("Cannot compute {} index: {}", indexType, e.getMessage());
        }
        
        return result;
    }
}
