package simulator;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

public class SimMain {
	
	public static final Integer numeroJob = 12;
	public static final Integer numeroOsservazioniP = 20;
	public static final Integer lunghezzaMaxRunN = 15;

	public static void main(String[] args) {
		
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

		for(int i = 1; i <= lunghezzaMaxRunN; i++){
			
			sommaTempiMediRisp = 0.0;
			mediaCampionaria = 0.0;
			differenzaPerCalcoloVarianza = 0.0;

			System.out.println("Lunghezza run " + i);
			
			for(int j = 1; j <= numeroOsservazioniP; j++){

				seq = new Sequenziatore(numeroJob);
				seq.simula(j);
				
				sommaTempiMediRisp += seq.getTempoMedioRispJob();
			}
			
			mediaCampionaria = sommaTempiMediRisp / numeroOsservazioniP;
			mediaCampionariaTot += mediaCampionaria;			
			
			//Calcolo media con Gordon
			stimaMediaGordon = mediaCampionariaTot / i;
			
			//Scrivo media su file risultati
			try {
				bufferedWriterMedieGordon.write(df.format(stimaMediaGordon).toString());
				bufferedWriterMedieGordon.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Calcolo varianza con Gordon
			differenzaPerCalcoloVarianza += Math.pow(mediaCampionaria - stimaMediaGordon, 2);
			
			stimaVarianzaGordon = differenzaPerCalcoloVarianza / (i - 1);
			
			if(i == 1)
				stimaVarianzaGordon = 0.0;
			
			//Scrivo varianza su file risultati
			try {
				bufferedWriterVarianzeGordon.write(df.format(stimaVarianzaGordon).toString());
				bufferedWriterVarianzeGordon.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bufferedWriterMedieGordon.close();
			bufferedWriterVarianzeGordon.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void salvaSequenziatore(Sequenziatore seq, String path){
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		
		try{
			fos = new FileOutputStream(path);
			out = new ObjectOutputStream(fos);
			out.writeObject(seq);
			out.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	private static Sequenziatore caricaSequenziatore(String path){
		
		Sequenziatore seq = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		try{
			fis = new FileInputStream(path);
			in = new ObjectInputStream(fis);
			seq = (Sequenziatore)in.readObject();
			in.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
		
		return seq;
	}
}
