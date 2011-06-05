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
	
	public static final Integer numeroClient = 120;
	public static final Integer numeroOsservazioniP = 50;
	public static final Integer lunghezzaMaxRunN = 6000;
	public static final String pathRisultatiMedieGordon = "c:\\medieGordon.txt";
	public static final String pathRisultatiVarianzeGordon = "c:\\varianzeGordon.txt";
	public static final String pathSeq = "c:\\SeqStabileClient";
	public static final String pathRisultatiIglehart = "c:\\iglehart.txt";
	public static final Integer mode = 2;
	
	// Clock per lunghezza run = 3000
	public static final Double clockStabile = 13000.0;
	
	public static final Double alpha = 0.1;
	
	// area = 0.95
	public static final Double area = 1 - (alpha / 2);
	
	// Da tabella
	public static final Double uAlphaMezzi = 1.645;
	
	// Calcolo k
	public static final Double k = Math.pow(uAlphaMezzi, 2) / numeroOsservazioniP;;
	
	public static void main(String[] args) {

		if (mode == 0) {// Stabilizzazione
			SimMain.runStab();
		} else if (mode == 1) {// Salvataggio di tutti gli stati stabili di
								// partenza
			SimMain.runSalvataggioStatoStabile();
		} else {
			SimMain.runStat();
		}
	}
	
	private static void salvaSequenziatore(Sequenziatore seq, String path) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		
		try {
			fos = new FileOutputStream(path);
			out = new ObjectOutputStream(fos);
			out.writeObject(seq);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static Sequenziatore caricaSequenziatore(String path) {

		Sequenziatore seq = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		try {
			fis = new FileInputStream(path);
			in = new ObjectInputStream(fis);
			seq = (Sequenziatore) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		return seq;
	}
	
	private static void runStab() {

		Sequenziatore seq = null;
		
		// Variabili per studio output
		BufferedWriter bufferedWriterMedieGordon = null;
		BufferedWriter bufferedWriterVarianzeGordon = null;
		DecimalFormat df = new DecimalFormat("#.#########");
		
		// Variabili per stima media Gordon
		Double xij;
		Double mediaCampionariaXj;
		double[] arrayXj = new double[numeroOsservazioniP];
		Double sommaTuttiXj;
		Double en;
		
		// Variabili per stima varianza Gordon
		Double differenzaPerCalcoloVarianza;
		Double stimaVarianzaGordon;
		
		try {
			bufferedWriterMedieGordon = new BufferedWriter(new FileWriter(pathRisultatiMedieGordon, false));
			bufferedWriterVarianzeGordon = new BufferedWriter(new FileWriter(pathRisultatiVarianzeGordon, false));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 1; i <= lunghezzaMaxRunN; i++) {
			
			sommaTuttiXj = 0.0;
			en = 0.0;
			differenzaPerCalcoloVarianza = 0.0;
			stimaVarianzaGordon = 0.0;
			
			System.out.println("Lunghezza run: " + i);
			
			for (int j = 1; j <= numeroOsservazioniP; j++) {
				
				seq = new Sequenziatore(numeroClient);
				
				// Clock con 0 perché non conosciamo ancora il clock di
				// stabilizzazione
				seq.simula(i, 0.0);
				
				// Prendo il j-esimo campione del run di lunghezza i-esima
				xij = seq.getTempoMedioRispJob();
				
				// Sommo tutti i tempi medi di risposta per richieste verso Host
				arrayXj[j - 1] = arrayXj[j - 1] + xij;
				
				// Calcolo media campionaria
				mediaCampionariaXj = arrayXj[j - 1] / i;
				
				// Somma di tutti gli Xj per calcolo media con Gordon
				sommaTuttiXj += mediaCampionariaXj;
			}
			
			// Divido per numero di osservazioni per avere media campionaria
			en = sommaTuttiXj / numeroOsservazioniP;
			
			// Scrivo media su file risultati
			try {
				bufferedWriterMedieGordon.write(df.format(en).toString());
				bufferedWriterMedieGordon.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Calcolo varianza con Gordon
			for (int j = 0; j < numeroOsservazioniP; j++) {
				mediaCampionariaXj = arrayXj[j] / i;
				differenzaPerCalcoloVarianza += Math.pow(mediaCampionariaXj - en, 2);
			}
			
			stimaVarianzaGordon = differenzaPerCalcoloVarianza / (numeroOsservazioniP - 1);
			
			// Debug
			System.out.println("Stima media Gordon: en = " + en);
			System.out.println("Stima varianza Gordon: s2 = " + stimaVarianzaGordon);
			
			// Scrivo varianza su file risultati
			try {
				bufferedWriterVarianzeGordon.write(df.format(stimaVarianzaGordon).toString());
				bufferedWriterVarianzeGordon.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Clock: " + seq.getStabClock());
		}
		
		// Scrivo clock ultimo run su file risultati (riuso quello delle medie)
		try {
			bufferedWriterMedieGordon.write("Clock ultimo run: " + seq.getStabClock());
			System.out.println("Clock ultimo run: " + seq.getStabClock());
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
	
	private static void runSalvataggioStatoStabile() {

		Sequenziatore seq;
		String path = null;
		
		for (int i = 10; i <= SimMain.numeroClient; i += 10) {
			System.out.println("Salvataggio simulatore stabile per " + i + " client.");
			
			seq = new Sequenziatore(i);
			seq.simula(-1, SimMain.clockStabile);
			
			// Salvo lo stato stabile
			path = SimMain.pathSeq + i + ".ser";
			SimMain.salvaSequenziatore(seq, path);
		}
		
	}
	
	private static void runStat() {

		// Per scrivere risultati
		BufferedWriter bufferedWriterIglehart = null;
		
		try {
			bufferedWriterIglehart = new BufferedWriter(new FileWriter(pathRisultatiIglehart, false));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Per sequenziatore stabile
		Sequenziatore seqStabile;
		
		DecimalFormat df = new DecimalFormat("#.############");
		
		for (int h = 10; h <= numeroClient; h += 10) {
			
			try {
				bufferedWriterIglehart.write("NumeroClient = " + h);
				bufferedWriterIglehart.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Inizio Run statistici per " + h + " client.");
			
			Integer n = 0;
			Double yj;
			Double nSegnato = 0.0;
			int[] arrayN = new int[numeroOsservazioniP];
			Double sommaTuttiN = 0.0;
			Double sommaPerS2n = 0.0;
			Double s2n = 0.0;
			Double ySegnato = 0.0;
			double[] arrayY = new double[numeroOsservazioniP];
			Double sommaTuttiYj = 0.0;
			Double sommaPerS2y = 0.0;
			Double s2y;
			Double sommaPerS2yn = 0.0;
			Double s2yn = 0.0;
			Double D = 0.0;
			Double numeratoreComuneIglehart = 0.0;
			Double denominatoreIglehart = 0.0;
			Double iglehartBasso = 0.0;
			Double iglehartAlto = 0.0;
			UniformLongGenerator genLunghRun = new UniformLongGenerator(50L, 100L, SeedCalculator.getSeme());
			
			for (int j = 0; j < numeroOsservazioniP; j++) {
				
				// Generiamo un intero per sapere quanto deve essere lungo il
				// run
				n = genLunghRun.generateNextValue().intValue();
				yj = 0.0;
				arrayN[j] = n;
				sommaTuttiN += n;
				
				System.out.println("Osservazione (j): " + j);
				System.out.println("Lunghezza run estratta (n): " + n);
				
				for (int i = 1; i <= n; i++) {
					seqStabile = SimMain.caricaSequenziatore(SimMain.pathSeq + numeroClient + ".ser");
					seqStabile.simula(seqStabile.getJobInHost() + i, 0.0);
					yj += seqStabile.getTempoMedioRispJob();
				}
				
				arrayY[j] = yj;
				sommaTuttiYj += yj;
			}
			
			nSegnato = sommaTuttiN / numeroOsservazioniP;
			ySegnato = sommaTuttiYj / numeroOsservazioniP;
			
			for (int j = 0; j < numeroOsservazioniP; j++) {
				sommaPerS2yn += (arrayY[j] - ySegnato) * (arrayN[j] - nSegnato);
				sommaPerS2n += Math.pow((arrayN[j] - nSegnato), 2);
				sommaPerS2y += Math.pow((arrayY[j] - ySegnato), 2);
			}
			
			s2n = sommaPerS2n / (numeroOsservazioniP - 1);
			s2y = sommaPerS2y / (numeroOsservazioniP - 1);
			s2yn = sommaPerS2yn / (numeroOsservazioniP - 1);
			
			D = Math.pow((ySegnato * nSegnato) - (k * s2yn), 2) - ((Math.pow(nSegnato, 2) - (k * s2n)) * ((Math.pow(ySegnato, 2) - (k * s2y))));
			
			numeratoreComuneIglehart = (ySegnato * nSegnato) - (k * s2yn);
			denominatoreIglehart = (Math.pow(nSegnato, 2) - k * s2n);
			
			iglehartBasso = (numeratoreComuneIglehart - Math.sqrt(D)) / denominatoreIglehart;
			iglehartAlto = (numeratoreComuneIglehart + Math.sqrt(D)) / denominatoreIglehart;
			
			System.out.println("iglehartBasso, iglehartAlto = " + df.format(iglehartBasso) + ", " + df.format(iglehartAlto));
			
			try {
				bufferedWriterIglehart.write("ySegnato = " + df.format(ySegnato));
				bufferedWriterIglehart.newLine();
				
				bufferedWriterIglehart.write("nSegnato = " + df.format(nSegnato));
				bufferedWriterIglehart.newLine();
				
				bufferedWriterIglehart.write("s2n = " + df.format(s2n));
				bufferedWriterIglehart.newLine();
				
				bufferedWriterIglehart.write("s2y = " + df.format(s2y));
				bufferedWriterIglehart.newLine();
				
				bufferedWriterIglehart.write("s2yn = " + df.format(s2yn));
				bufferedWriterIglehart.newLine();
				
				bufferedWriterIglehart.write("iglehartBasso, iglehartAlto = " + df.format(iglehartBasso) + ", " + df.format(iglehartAlto));
				bufferedWriterIglehart.newLine();
	
				bufferedWriterIglehart.write("--------------");
				bufferedWriterIglehart.newLine();				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bufferedWriterIglehart.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
