package simulator;

import generators.SeedCalculator;
import generators.UniformLongGenerator;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

public class SimMain {
	
	public static final Integer numeroJob = 120;
	public static final Integer numeroOsservazioniP = 50;
	public static final Integer lunghezzaMaxRunN = 1000;
	public static final String pathSeq = "c:\\SeqStabileClient";
	public static final Integer mode = 1;
	public static final Double clockStabile = 82.230004136;

	public static void main(String[] args) {
		
		if(mode == 0){//Stabilizzazione
			SimMain.runStab();
		}else if(mode == 1){//Salvataggio di tutti gli stati stabili di partenza
			SimMain.runSalvataggioStatoStabile();
		}else{
			//SimMain.runStat();
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
	
	private static void runStab(){
		
		Sequenziatore seq = null;
		BufferedWriter bufferedWriterMedieGordon = null;
		BufferedWriter bufferedWriterVarianzeGordon = null;
		
		Double mediaCampionaria = 0.0;
		Double mediaCampionariaTot = 0.0;
		Double sommaTempiMediRisp = 0.0;
		Double stimaMediaGordon = 0.0;
		Double stimaVarianzaGordon = 0.0;
		Double differenzaPerCalcoloVarianza = 0.0;
		
		DecimalFormat df = new DecimalFormat("#.#########");
		
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
				
				//Clock con Double.MAX_VALUE perché non conosciamo ancora il clock di stabilizzazione
				seq.simula(j, Double.MAX_VALUE);
				
				//Sommo tutti i tempi medi di risposta per richieste verso Host
				sommaTempiMediRisp += seq.getTempoMedioRispJob();
			}
			
			//Divido per numero di osservazioni per avere media campionaria
			mediaCampionaria = sommaTempiMediRisp / numeroOsservazioniP;
			
			//Calcolo media con Gordon			
			mediaCampionariaTot += mediaCampionaria;
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
			
			//Gestisco la prima iterazione
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
		
		//Scrivo clock stabile su file risultati (riuso quello delle medie)
		try {
			bufferedWriterMedieGordon.write("Clock per stato stabile: " + seq.getStabClock());
			System.out.println("Clock per stato stabile: " + seq.getStabClock());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bufferedWriterMedieGordon.close();
			bufferedWriterVarianzeGordon.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void runSalvataggioStatoStabile(){
		
		Sequenziatore seq;
		String path = null;
		
		for(int i = 1; i <= SimMain.numeroJob; i += 10){
			seq = new Sequenziatore(i);
			seq.simula(SimMain.lunghezzaMaxRunN, SimMain.clockStabile);
			
			//Salvo lo stato stabile
			path = SimMain.pathSeq + i + ".ser";
			SimMain.salvaSequenziatore(seq, path);
			
			System.out.println("Salvataggio simulatore stabile per" + i + " client.");
		}
		
	}
	
	private static void runStat(Integer numeroClient){
		
		//Carico sequenziatore stabile
		Sequenziatore seqStabile;
		Double tempoMedioRispTuttiJob = 0.0;
		Integer totaleLunghezzeRun = 0;
		Integer tempLunghezzaRun = 0;
		
		UniformLongGenerator genLunghRun = new UniformLongGenerator(50L, 100L, SeedCalculator.getSeme());
		
		for(int i = 1; i <= numeroOsservazioniP; i++){
			
			tempLunghezzaRun = genLunghRun.generateNextValue().intValue();
			totaleLunghezzeRun += tempLunghezzaRun;
			
			for(int j = 1; j <= tempLunghezzaRun; j++){
				seqStabile = SimMain.caricaSequenziatore(SimMain.pathSeq + numeroClient + ".ser");
				seqStabile.simula(tempLunghezzaRun, Double.MAX_VALUE);
				
				tempoMedioRispTuttiJob += seqStabile.getTempoMedioRispJob();
			}
		}
		
	}
}
