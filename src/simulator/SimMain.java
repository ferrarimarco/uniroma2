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
	public static final Integer lunghezzaMaxRunN = 6000;
	public static final String pathSeq = "c:\\SeqStabileClient";
	public static final Integer mode = 0;
	public static final Double clockStabile = 82.230004136;
	public static final Double alpha = 0.1;

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
		
		//Variabili per stima media Gordon
		Double xij;
		Double sommaTempiMediRispXij;	
		Double mediaCampionariaXj;
		double[] arrayXj = new double[numeroOsservazioniP];
		Double sommaTuttiXj;
		Double en;
		Double valoreAttuale = 0.0;
		
		//Variabili per stima varianza Gordon
		Double differenzaPerCalcoloVarianza;
		Double stimaVarianzaGordon;
		
		DecimalFormat df = new DecimalFormat("#.#########");
		
		try {
			bufferedWriterMedieGordon = new BufferedWriter(new FileWriter("c:\\medieGordon.txt", false));
			bufferedWriterVarianzeGordon = new BufferedWriter(new FileWriter("c:\\varianzeGordon.txt", false));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 1; i <= lunghezzaMaxRunN; i++){
			
			sommaTempiMediRispXij = 0.0;
			sommaTuttiXj = 0.0;
			en = 0.0;
			differenzaPerCalcoloVarianza = 0.0;
			stimaVarianzaGordon = 0.0;
			

			System.out.println("Lunghezza run " + i);
			
			for(int j = 1; j <= numeroOsservazioniP; j++){
	
				seq = new Sequenziatore(numeroJob);
				
				//Clock con 0 perché non conosciamo ancora il clock di stabilizzazione
				seq.simula(i, 0.0);
				
				//Prendo il j-esimo campione del run di lunghezza i-esima
				xij = seq.getTempoMedioRispJob();

				//Sommo tutti i tempi medi di risposta per richieste verso Host
				arrayXj[j - 1] = arrayXj[j - 1] + xij;
				//Calcolo media campionaria
				mediaCampionariaXj = arrayXj[j - 1]/i;

				//Somma di tutti gli Xj per calcolo media con Gordon
				sommaTuttiXj += mediaCampionariaXj;

			}
						
			//Divido per numero di osservazioni per avere media campionaria
			en = sommaTuttiXj / numeroOsservazioniP;
			System.out.println("en "+ en);

			//Scrivo media su file risultati
			try {
				bufferedWriterMedieGordon.write(df.format(en).toString());
				bufferedWriterMedieGordon.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Calcolo varianza con Gordon
			for(int j = 0; j < numeroOsservazioniP; j++){
				mediaCampionariaXj = arrayXj[j]/i;
				differenzaPerCalcoloVarianza += Math.pow(mediaCampionariaXj - en, 2);
			}
			
			stimaVarianzaGordon = differenzaPerCalcoloVarianza / (numeroOsservazioniP - 1);				

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
		
		for(int i = 10; i <= SimMain.numeroJob; i += 10){
			seq = new Sequenziatore(i);
			seq.simula(-1, SimMain.clockStabile);
			
			//Salvo lo stato stabile
			path = SimMain.pathSeq + i + ".ser";
			SimMain.salvaSequenziatore(seq, path);
			
			System.out.println("Salvataggio simulatore stabile per " + i + " client.");
		}
		
	}
	
	private static void runStat(Integer numeroClient){
		
		//Carico sequenziatore stabile
		Sequenziatore seqStabile;
		
		Integer n = 0;
		Double yj;
		
		Double nSegnato = 0.0;
		int[] arrayN = new int[numeroOsservazioniP];
		Double sommaTuttiN = 0.0;
		
		Double ySegnato = 0.0;
		double[] arrayY = new double[numeroOsservazioniP];
		Double sommaTuttiYj = 0.0;
		
		UniformLongGenerator genLunghRun = new UniformLongGenerator(50L, 100L, SeedCalculator.getSeme());
		
		for(int i = 1; i <= numeroOsservazioniP; i++){
			
			//Generiamo un intero per sapere quanto deve essere lungo il run
			n = genLunghRun.generateNextValue().intValue();
			yj = 0.0;
			arrayN[i - 1] = n;
			sommaTuttiN += n;
			
			for(int j = 1; j <= n; j++){
				seqStabile = SimMain.caricaSequenziatore(SimMain.pathSeq + numeroClient + ".ser");
				seqStabile.simula(n, Double.MAX_VALUE);
				
				yj += seqStabile.getTempoMedioRispJob();
			}
			
			arrayY[i - 1] = yj;
			sommaTuttiYj += yj; 
		}
		
		nSegnato = sommaTuttiN / numeroOsservazioniP;
		ySegnato = sommaTuttiYj / numeroOsservazioniP;		
	}
}
