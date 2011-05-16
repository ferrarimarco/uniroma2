package simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class SimMain {
	
	public static final Integer numeroJob = 12;

	public static void main(String[] args) {
		
		Integer numeroOsservazioni = 50;
		Integer lunghezzaMaxRun = 50;
		Sequenziatore seq;
		BufferedWriter bufferedWriterMedieGordon = null;
		BufferedWriter bufferedWriterVarianzeGordon = null;
		
		Double mediaCampionaria = 0.0;
		Double mediaCampionariaTot = 0.0;
		Double sommaTempiMediRisp = 0.0;
		Double stimaMediaGordon = 0.0;
		Double stimaVarianzaGordon = 0.0;
		Double differenzaPerCalcoloVarianza = 0.0;
		
		DecimalFormat df = new DecimalFormat("#.########");
		
		try {
			bufferedWriterMedieGordon = new BufferedWriter(new FileWriter("c:\\medieGordon.txt", false));
			bufferedWriterVarianzeGordon = new BufferedWriter(new FileWriter("c:\\varianzeGordon.txt", false));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int j = 1; j < lunghezzaMaxRun + 1; j++){
			
			sommaTempiMediRisp = 0.0;
			mediaCampionariaTot = 0.0;
			differenzaPerCalcoloVarianza = 0.0;
			
			for(int i = 0; i < numeroOsservazioni; i++){
				seq = new Sequenziatore(numeroJob);
				seq.simula(j);
				
				sommaTempiMediRisp += seq.getTempoMedioRispJob();
			}
			
			mediaCampionaria = sommaTempiMediRisp / numeroOsservazioni;
			mediaCampionariaTot += mediaCampionaria;			
			
			//Calcolo media con Gordon
			stimaMediaGordon = mediaCampionariaTot / j;
			
			//Scrivo media su file risultati
			if(stimaMediaGordon != 0){
				try {
					bufferedWriterMedieGordon.write(df.format(stimaMediaGordon).toString());
					bufferedWriterMedieGordon.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//Calcolo varianza con Gordon
			differenzaPerCalcoloVarianza += Math.pow(mediaCampionaria - stimaMediaGordon, 2);
			
			stimaVarianzaGordon = differenzaPerCalcoloVarianza / (j - 1);
			
			//Scrivo varianza su file risultati
			if(stimaMediaGordon != 0){
				try {
					bufferedWriterVarianzeGordon.write(df.format(stimaVarianzaGordon).toString());
					bufferedWriterVarianzeGordon.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			bufferedWriterMedieGordon.close();
			bufferedWriterVarianzeGordon.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
