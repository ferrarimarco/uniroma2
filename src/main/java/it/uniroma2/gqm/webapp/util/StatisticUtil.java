package it.uniroma2.gqm.webapp.util;

import java.util.List;

import it.uniroma2.gqm.model.Measurement;;

public class StatisticUtil {

	public static Statistic calculatestatistic(List<Measurement> measure) {
		Statistic stat = new Statistic();
        double media=0;
        for(int i=0;i<measure.size();i++) {
        	media += measure.get(i).getValue();
        }
        media /= measure.size();
        stat.setMedia(media);
        stat.setVarianza(0);
        
        return stat;
	}
}
