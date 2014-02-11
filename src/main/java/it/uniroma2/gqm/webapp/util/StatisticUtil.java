package it.uniroma2.gqm.webapp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.uniroma2.gqm.model.Measurement;

public class StatisticUtil {

	public static Statistic calculatestatistic(List<Measurement> measure) {
		
		Statistic stat = new Statistic();
		
        double media=0;
        double varianza=0;
        
        /*
         * Mean is aritmethic mean
         * Variance is mean[x^2] - mean[x]^2
         */
        for(int i=0;i<measure.size();i++) {
        	media += measure.get(i).getValue();
        	varianza += measure.get(i).getValue() * measure.get(i).getValue();
        }
        
        media /= measure.size();
        varianza /= measure.size();
        varianza -= media * media;
        
        /*
         * Median
         */
        double mediana = 0;
        Collections.sort(measure);
        
        if(measure.size() % 2 == 0)
        	mediana = (measure.get(measure.size()/2-1).getValue() + measure.get(measure.size()/2).getValue()) / 2;
        else
        	mediana = measure.get((measure.size()+1)/2-1).getValue();
        
        /*
         * Moda
         */
        double moda = 0;
        int count = 1;
        double value = measure.get(0).getValue();
        int max_count = 0;
        double max_value=0;
        for(int i=0; i<measure.size()-1; i++) {
        	if(measure.get(i).getValue() == measure.get(i+1).getValue()) {
        		count++;
        		value = measure.get(i).getValue();
        	}
        	else {
        		if(count > max_count) {
        			max_count = count;
        			max_value = value;
        		}
        		count = 1;
        		value = 0;
        	}
        }
        moda = max_value;

        stat.setMedia(media);
        stat.setVarianza(varianza);
        stat.setMediana(mediana);
        stat.setModa(moda);
        
        return stat;
	}
}
