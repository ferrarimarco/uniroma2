import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * This class can simplify any given CF grammar, using the following algorithms:
 * from below, from above, elimination of epsilon productions, eliminations of
 * unit productions, avoiding left recursion. <br \>
 * It can also parse a word, according to the grammar's production rules, using
 * the Cocke-Younger-Kasami algorithm. <br \>
 * See "Pettorossi, A.: Automata Theory and Formal Languages. Aracne, Dec 2007",
 * Chapter 3, Section 3.13.1 "CYK Parser" for additional details.<br \>
 * See <a href="http://java.sun.com/j2se/javadoc/writingdoccomments/">How To
 * Write Javadoc Comments</a> for additional details about the Javadoc of this
 * project.
 *
 * @author Marco Ferrari
 */
public class ContextFreeParsing {

	// Variabili per Grammar Formatter
	private ArrayList<String>[] formattedGrammar;
	private int counterFormatter, lhsLenght;
	private String lhs, s;
	private ArrayList<String> rhsNoOr;

	// Variabili From Below
	private ArrayList<String>[] simpGrammarFB;
	private ArrayList<String> VtVnFB, productionsFormatter;
	private ArrayList<Integer> indiciOR;
	private String rhsFB, nonTerminalFB, nonTerminalRhsFB;

	// Variabili From Above
	private ArrayList<String>[] simpGrammarFA;
	private String lhsFA, rhsFA;
	private char rhsCharFA;

	// Variabili Epsilon Productions
	private ArrayList<String>[] simpGrammarEps;
	private ArrayList<String> nullableEps, nullableProdEps;
	private String rhsEps, lhsEps, prodEps, prodEpsAxiom, nonTerminalEps,
			newProdEps, newRhsEps, lhsProdAxiomEps;
	private boolean nonNullableCheckerEps, nonNullableCheckerAxiomEps;

	// Variabili Unit Productions
	private ArrayList<String>[] simpGrammarUnit, grammarUP;
	private ArrayList<String> queueUnitProd, unfoldingUnitProd;
	private String lhsTrivial, rhsTrivial, productionUnitProd,
			productionUnfoldingUnitProd, rhsProductionUnfoldingUnitProd,
			lhsUnitProd, rhsUnitProd;

	// Variabili per cercare LHS e RHS
	private String rhsSearch, lhsSearch;

	// Veriabili Left Recursion
	private ArrayList<String>[] simpGrammarLeftRecursion, grammarLR1, grammarLR2;
	private String lhsLeftRecursion, rhsLeftRecursion, productionLeftRecursion,
			lhsNRR, rhsNRR, randomCharS, productionNLR;
	private ArrayList<Integer> leftRecursiveProductionsIndex,
			indexNonLeftRecursive;
	private int indexLeftRecursion, indexNLR, indexCorrectionLR;
	private Random random;
	private char randomChar;

	// Variabili per il parser
	private ArrayList<String>[] grammarCKY1, grammarCKY2;
	private boolean parseResultPAR;
	private String[][] recogMatrixPAR;
	private String subWordPAR;

	// Variabili consoleReader
	private String readString;

	// Variabili Parser
	private ArrayList<String> lhsSymbolPAR, cykHelper, cykProductionsComb;
	private String firstCellPAR, secondCellPAR;

	// Variabili rhsBuilder
	private ArrayList<String> cellsCombinations, firstStringNT, secondStringNT,
			combinationsPAR;

	public static void main(String[] args) {

		ContextFreeParsing a = new ContextFreeParsing();

		System.out.print("Inserire la grammatica da analizzare: ");
		String consoleGrammar = a.consoleReader();

		//grammar per testare il compito
		//String consoleGrammar = S->SA|a;A->EB|EC|AD|b;B->AE;D->b;E->a.

		// Scelta della procedura
		System.out.println("Cosa vuoi fare?");
		System.out.println("1: From Below");
		System.out.println("2: From Above");
		System.out.println("3: Elimination of Epsilon Productions");
		System.out.println("4: Elimination of Unit Productions");
		System.out.println("5: Avoiding Left Recursion");
		System.out.println("6: Parse a string");
		System.out.println("7: Do all possible simplifications on the given grammar");
		System.out.print("Scelta: ");

		// Scelta utente (input da console)
		int algChooser = Integer.parseInt(a.consoleReader());

		// Formatto la grammatica
		ArrayList<String>[] consoleGrammarFormatted = a.grammarFormatter(consoleGrammar);
		ArrayList<String>[] newGrammar;

		System.out.println();
		System.out.println();

		//ArrayLists per applicare tutte le semplificazioni
		ArrayList<String>[] newGrammar2, newGrammar3, newGrammar4, newGrammar5;

		switch (algChooser) {
			case 1:
				System.out.println("Grammatica Semplificata (From Below): ");
				newGrammar = a.fromBelow(consoleGrammarFormatted);
				a.grammarPrinter(newGrammar);
				break;
			case 2:
				System.out.println("Grammatica Semplificata (From Above): ");
				newGrammar = a.fromAbove(consoleGrammarFormatted);
				a.grammarPrinter(newGrammar);
				break;
			case 3:
				System.out.println("Grammatica Semplificata (Elimination of Epsilon Productions): ");
				newGrammar = a.epsilonProductions(consoleGrammarFormatted);
				a.grammarPrinter(newGrammar);
				break;
			case 4:
				System.out.println("Grammatica Semplificata (Elimination of Unit Productions): ");
				newGrammar = a.unitProductions(consoleGrammarFormatted);
				a.grammarPrinter(newGrammar);
				break;
			case 5:
				System.out.println("Grammatica Semplificata (Avoiding Left Recursion): ");
				newGrammar = a.leftRecursion(consoleGrammarFormatted);
				a.grammarPrinter(newGrammar);
				break;
			case 6:
				System.out.print("Inserisci una parola da parsare: ");
				String consoleWord = a.consoleReader();
				System.out.println();

				boolean parseResultMain = a.parserCYK(consoleGrammarFormatted, consoleWord);

				if (parseResultMain == true) {
					System.out.println("La parola APPARTIENE al linguaggio generato dalla grammatica.");
				} else {
					System.out.println("La parola NON APPARTIENE al linguaggio generato dalla grammatica.");
				}

				break;
			case 7:
				System.out.println("Grammatica Semplificata: ");
				newGrammar = a.fromBelow(consoleGrammarFormatted);
				newGrammar2 = a.fromAbove(newGrammar);
				newGrammar3 = a.epsilonProductions(newGrammar2);
				newGrammar4 = a.unitProductions(newGrammar3);
				newGrammar5 = a.leftRecursion(newGrammar4);
				a.grammarPrinter(newGrammar5);
		}
	}

	/**
	 * Reads a string input from the console, using InputStreamReader and
	 * BufferedReader
	 *
	 * @return readString the string read from the console
	 *
	 */
	private String consoleReader() {

		readString = "";

		InputStreamReader inputStreamReader = new InputStreamReader(System.in);

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		try {
			readString = bufferedReader.readLine();
		} catch (IOException e) {
			System.out.println("Errore nell'input da console: " + e);
		}

		return readString;

	}

	/**
	 * Returns the LHS of a given production
	 *
	 * @param production
	 *            production to search into
	 * @param separator
	 *            LHS-RHS separator
	 * @return rhsSearch LHS of the given production
	 */
	private String lhsSearch(String production, char separator) {
		lhsSearch = production.substring(0, production.indexOf(separator));
		return lhsSearch;
	}

	/**
	 * Returns the RHS of a given production
	 *
	 * @param production
	 *            production to search into
	 * @param separator
	 *            LHS-RHS separator
	 * @return rhsSearch RHS of the given production
	 */
	private String rhsSearch(String production, char separator) {
		rhsSearch = production.substring(production.indexOf(separator) + 1);
		return rhsSearch;
	}

	/**
	 * Prints given grammar on the default console
	 *
	 * @param grammar
	 *            Given grammar
	 */
	public void grammarPrinter(ArrayList<String>[] grammar) {
		String nomeInsieme = "";

		for (int i = 0; i < grammar.length; i++) {
			switch (i) {
				case 0:
					nomeInsieme = "Insieme dei terminali (Vt)";
					break;
				case 1:
					nomeInsieme = "Insieme dei non terminali (Vn)";
					break;
				case 2:
					nomeInsieme = "Insieme delle produzioni (P)";
					break;
				case 3:
					nomeInsieme = "Assioma (S)";
					break;
			}

			System.out.print(nomeInsieme + " = {");

			for (int j = 0; j < grammar[i].size(); j++) {
				System.out.print("{" + grammar[i].get(j) + "}");
				if (j != grammar[i].size() - 1) {
					System.out.print(",");
				}
			}

			System.out.print("}");
			System.out.println();
		}
	}

	/**
	 * Inizializes an Array of ArrayLists (String)
	 *
	 * @param arrayList
	 *            The array to be inizialised
	 */
	private void ArrayListInit(ArrayList<String>[] arrayList) {

		for (int i = 0; i < arrayList.length; i++) {
			arrayList[i] = new ArrayList<String>();
		}

	}

	/**
	 * Converts any given string as standard form grammar. It consists in an
	 * Array of four ArrayLists, representing the 4-tule <Vt, Vn, P, S>.
	 *
	 * @param grammarString
	 *            String to be converted in the standard form
	 * @return formattedGrammar An Array of four ArrayLists
	 *
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String>[] grammarFormatter(String grammarString) {

		// Inizializzo gli ArrayList di formattedGrammar
		formattedGrammar = new ArrayList[4];
		ArrayListInit(formattedGrammar);

		// Primo elemento della stringa = assioma; lo aggiungo
		formattedGrammar[3].add(lhsSearch(grammarString, '-'));

		// Costruisco gli insiemi Vt e Vn
		for (int i = 0; i < grammarString.length(); i++) {

			// Esamino la stringa e aggiungo elementi a Vt e Vn, escludendo i
			// simboli - > ; . e
			if ((grammarString.charAt(i) != '|') && (grammarString.charAt(i) != 'e') && (grammarString.charAt(i) != '-') && (grammarString.charAt(i) != '>') && (grammarString.charAt(i) != ';') && (grammarString.charAt(i) != '.')) {

				// Controllo se carattere maiuscolo o minuscolo e lo inserisco
				// nell'insieme oppurtuno
				if (Character.isLowerCase(grammarString.charAt(i)) && (formattedGrammar[0].contains(Character.toString(grammarString.charAt(i))) == false)) {
					// minuscolo = aggiungo a terminali

					// Controllo se l'elemento è già presente nell'insieme
					formattedGrammar[0].add(Character.toString(grammarString.charAt(i)));

				} else if (Character.isUpperCase(grammarString.charAt(i)) && (formattedGrammar[1].contains(Character.toString(grammarString.charAt(i))) == false)) {

					// maiuscolo = aggiungo a non terminali
					formattedGrammar[1].add(Character.toString(grammarString.charAt(i)));
				}
			}
		}

		// Costruisco l'insieme delle produzioni

		// Inizializzo il contatore
		counterFormatter = 0;

		// ArrayList per formattare le produzioni
		productionsFormatter = new ArrayList<String>();

		// Divido la stringa in più parti (ES: se c'è un Or inserisco due
		// produzioni con lo stesso lhs)
		while ((grammarString.length() != 0) && (counterFormatter < grammarString.length())) {

			// Cerco un delimitatore nella stringa (; o .)
			if ((grammarString.charAt(counterFormatter) == ';') || (grammarString.charAt(counterFormatter) == '.')) {
				// Se ho trovato un delimitatore...

				// Sottostringa da analizzare
				s = grammarString.substring(0, counterFormatter);

				// Aggiorno la stringa in input (tolgo la sottostringa che sto
				// elaborando ora)
				grammarString = grammarString.substring(counterFormatter + 1);

				// Memorizzo il lhs in una variabile e lo tolgo dalla
				// sottostringa che sto analizzando
				lhsLenght = s.indexOf('-');
				lhs = s.substring(0, lhsLenght);
				s = s.substring(lhsLenght + 2);

				// Controllo per l'or
				boolean checkerOR = false;

				// Cerco gli eventuali or nella stringa che sto analizzando
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == '|') {
						// Ho trovato almeno un or
						checkerOR = true;

						// Esco dal for se trovo un or
						break;
					}
				}

				if (checkerOR == false) {
					// Se non ci sono or...
					// Aggiungo la produzione all'insieme delle produzioni:
					// LHS + rhs
					productionsFormatter.add(lhs + ";" + s);

				} else {
					// Se ho trovato gli or...
					// Memorizzo gli indici degli or
					indiciOR = new ArrayList<Integer>();

					for (int i = 0; i < s.length(); i++) {
						if (s.charAt(i) == '|') {
							indiciOR.add(i);
						}
					}

					// Memorizzo il rhs, diviso in corrispondenza degli or
					rhsNoOr = new ArrayList<String>();

					if (indiciOR.size() == 1) {
						// Caso 1: un solo OR
						rhsNoOr.add(s.substring(0, indiciOR.get(0)));
						rhsNoOr.add(s.substring(indiciOR.get(0) + 1));
					} else {
						for (int i = 0; i < indiciOR.size(); i++) {
							if (i == 0) {
								// Primo OR
								rhsNoOr.add(s.substring(0, indiciOR.get(i)));
							} else if (i == indiciOR.size() - 1) {
								// Ultimo OR

								// Aggiungo prima parte
								rhsNoOr.add(s.substring(indiciOR.get(i - 1) + 1, indiciOR.get(i)));

								// Aggiungo seconda parte
								rhsNoOr.add(s.substring(indiciOR.get(i) + 1));
							} else {
								// Comportamento normale
								rhsNoOr.add(s.substring(indiciOR.get(i - 1) + 1, indiciOR.get(i)));
							}
						}
					}

					// Aggiungo tutte le produzioni spezzate con lo stesso lhs
					// alla lista delle produzioni
					for (int i = 0; i < rhsNoOr.size(); i++) {
						productionsFormatter.add(lhs + ";" + rhsNoOr.get(i));
					}
				}

				// Reinizializzo le variabili di controllo
				counterFormatter = -1;
				checkerOR = false;
			}

			// Incremento il contatore per passare al carattere successivo
			counterFormatter++;
		}

		// Aggiungo la lista delle produzioni alla grammatica formattata
		formattedGrammar[2].addAll(productionsFormatter);

		// Restituisco la grammatica formattata
		return formattedGrammar;
	}

	/**
	 * From Below algorithm: Eliminates all the symbols that do not generate
	 * words
	 *
	 * @param grammar
	 *            grammar with useless symbols
	 * @return simpGrammarEps grammar without useless symbols
	 *
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<String>[] fromBelow(ArrayList<String>[] grammar) {

		// Inizializzo gli ArrayList della nuova grammatica
		simpGrammarFB = new ArrayList[4];
		ArrayListInit(simpGrammarFB);

		// Aggiungo gli elementi che simpGrammarFB ha in comune con grammar
		simpGrammarFB[0].addAll(grammar[0]);
		simpGrammarFB[3].addAll(grammar[3]);

		// Insieme Vt U V'n
		VtVnFB = new ArrayList<String>();

		// Costruisco l'insieme Vt U V'n (aggiungo per ora solo i terminali e
		// l'assioma di grammar)
		VtVnFB.addAll(grammar[0]);
		VtVnFB.addAll(grammar[3]);

		// Esamino i non terminali di grammar
		for (int i = 0; i < grammar[1].size(); i++) {

			// Non terminale da controllare
			nonTerminalFB = grammar[1].get(i);

			// Scelgo le produzioni con nonTerminalFB su rhs
			for (int j = 0; j < grammar[2].size(); j++) {

				// Se se il LHS della produzione che voglio semplificare è
				// uguale al non terminale, controllo anche il rhs
				if (nonTerminalFB.equals(lhsSearch(grammar[2].get(j), ';')) == true) {

					// RHS della produzione
					rhsFB = rhsSearch(grammar[2].get(j), ';');

					// Controllo se nel RHS della produzione c'è la stringa
					// vuota come unico simbolo
					if ((rhsFB.indexOf('e') != -1) && (rhsFB.length() == 1)) {

						// Lascio la produzione inalterata e aggiungo il simbolo
						// a Vt U V'n
						VtVnFB.add(nonTerminalFB);

						// Aggiungo il non terminale che ho controllato a V'n
						simpGrammarFB[1].add(nonTerminalFB);

					} else {

						// Non c'è la stringa vuota, proseguo
						for (int k = 0; k < rhsFB.length(); k++) {

							// Cerco il non terminale nel rhs
							nonTerminalRhsFB = rhsFB.substring(k, k + 1);

							// Controllo se il carattere è maiuscolo
							if (Character.isUpperCase(nonTerminalRhsFB.charAt(0)) == true) {

								// Controllo se tutti i simboli del rhs della
								// produzione appartengono a Vt U V'n
								if (VtVnFB.contains(nonTerminalRhsFB) == false) {

									// Se il simbolo non appartiene
									//elimino la produzione
									grammar[2].remove(j);
									j = j - 1;
									break;

								} else {

									// Se il non terminale appartiene lascio la
									// produzione inalterata e aggiungo il
									// simbolo a Vt U V'n
									VtVnFB.add(nonTerminalRhsFB);

									// Inoltre aggiungo il non terminale che ho
									// controllato a V'n
									simpGrammarFB[1].add(nonTerminalFB);
								}
							} else if ((Character.isUpperCase(nonTerminalRhsFB.charAt(0)) == false) && (rhsFB.length() == 1)) {

								// Caso in cui la produzione produce un solo
								// terminale
								// Se il non terminale appartiene lascio la
								// produzione inalterata e aggiungo il simbolo a
								// Vt U V'n
								VtVnFB.add(nonTerminalRhsFB);

								// Inoltre aggiungo il non terminale che ho
								// controllato a V'n
								simpGrammarFB[1].add(nonTerminalFB);
							}
						}
					}
				}
			}
		}

		simpGrammarFB[2].addAll(grammar[2]);

		return simpGrammarFB;
	}

	/**
	 * From Above algorithm: Eliminates all the symbols not reachable from the
	 * start symbol
	 *
	 * @param grammar
	 *            grammar with useless symbols
	 * @return simpGrammarEps grammar without useless symbols
	 *
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<String>[] fromAbove(ArrayList<String>[] grammar) {

		// Inizializzo gli ArrayList della nuova grammatica
		simpGrammarFA = new ArrayList[4];
		ArrayListInit(simpGrammarFA);

		// Aggiungo l'assioma di grammar a simpGrammarFA (assioma e V'n)
		simpGrammarFA[1].addAll(grammar[3]);
		simpGrammarFA[3].addAll(grammar[3]);

		// Controllo tutte le produzioni di grammar
		for (int j = 0; j < grammar[2].size(); j++) {

			// Controllo se il LHS di una produzione appartiene a V'n
			lhsFA = lhsSearch(grammar[2].get(j), ';');

			// Se la produzione ha sul LHS un non terminale in V'n, controllo
			// anche il RHS
			if (simpGrammarFA[1].contains(lhsFA) == true) {

				// Aggiungo la produzione a P'
				simpGrammarFA[2].add(grammar[2].get(j));

				// RHS da controllare
				rhsFA = rhsSearch(grammar[2].get(j), ';');

				// Controllo tutti i simboli del RHS della produzione
				for (int k = 0; k < rhsFA.length(); k++) {

					// Simbolo da controllare
					rhsCharFA = rhsFA.charAt(k);

					// Controllo se è terminale o non terminale (escludendo
					// stringa vuota)
					if ((Character.isLowerCase(rhsCharFA) == true) && (rhsCharFA != 'e')) {

						// Se minuscolo (terminale) lo aggiungo a V't
						simpGrammarFA[0].add(Character.toString(rhsCharFA));

					} else {
						// Se maiuscolo (non terminale) lo aggiungo a V'n
						simpGrammarFA[1].add(Character.toString(rhsCharFA));
					}
				}
			}
		}

		// Restituisco simpGrammar
		return simpGrammarFA;
	}

	/**
	 * Eliminates all the epsilon productions from the productions of a given
	 * grammar
	 *
	 * @param grammar
	 *            grammar with epsilon productions
	 * @return simpGrammarEps grammar without epsilon productions
	 *
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<String>[] epsilonProductions(ArrayList<String>[] grammar) {

		// Inizializzo gli ArrayList della nuova grammatica
		simpGrammarEps = new ArrayList[4];
		ArrayListInit(simpGrammarEps);

		// Aggiungo l'assioma di grammar all'insieme dei non terminali di
		// simpGrammarEps e lo imposto come assioma
		simpGrammarEps[1].addAll(grammar[3]);
		simpGrammarEps[3].addAll(grammar[3]);

		// Insieme dei simboli nullable di grammar, per ora vuoto
		nullableEps = new ArrayList<String>();

		// Cerco le produzioni che hanno epsilon sul rhs e le inserisco
		// nell'insieme dei nullable
		for (int j = 0; j < grammar[2].size(); j++) {

			// LHS della produzione
			lhsEps = lhsSearch(grammar[2].get(j), ';');

			// RHS della produzione
			rhsEps = rhsSearch(grammar[2].get(j), ';');

			// Controllo se ci sono produzioni del tipo A->e
			if ((rhsEps.charAt(0) == 'e') && (rhsEps.length() == 1)) {

				// Aggiungo il non terminale della produzione all'insieme dei
				// nullable
				nullableEps.add(lhsEps);
			}
		}

		// Cerco i non terminali nullable all'interno di una produzione
		for (int k = 0; k < grammar[2].size(); k++) {

			// Produzione
			prodEps = grammar[2].get(k);

			// Controllo le produzioni per vedere quali non terminali sono
			// nullable (mi lascio quelle dell'assioma per dopo)
			if (lhsSearch(prodEps, ';').equals("S") == false) {

				// Controllo per sapere se ho trovato almeno un simbolo non
				// nullable
				nonNullableCheckerEps = false;

				// Controllo il rhs di tutte le produzioni
				for (int l = 2; l < prodEps.length(); l++) {

					// Controllo se il non terminale del lhs è presente
					// nell'insieme dei nullable
					if (nullableEps.contains(prodEps.substring(l, l + 1)) == false) {

						// Ho trovato almeno un simbolo non nullable
						nonNullableCheckerEps = true;
					}
				}

				// Se il checker è false vuol dire che sul RHS della produzione
				// ci sono tutti nullable
				if (nonNullableCheckerEps == false) {

					// RHS da aggiungere all'insieme dei nullable
					lhsEps = Character.toString(grammar[2].get(k).charAt(0));

					// Aggiungo il lhs della produzione all'insieme dei nullable
					// (se non c'è già)
					if (nullableEps.contains(lhsEps) == false) {
						nullableEps.add(lhsEps);
					}
				}
			}
		}

		// Controllo se l'assioma è nullable
		for (int p = 0; p < grammar[2].size(); p++) {

			// Produzione
			prodEpsAxiom = grammar[2].get(p);

			// LHS produzione
			lhsProdAxiomEps = lhsSearch(prodEpsAxiom, ';');

			// Prendo solo le produzioni con l'assioma sul LHS
			if (lhsProdAxiomEps.equals(grammar[3].get(0)) == true) {

				// Controllo per sapere se ho trovato almeno un simbolo non
				// nullable
				nonNullableCheckerAxiomEps = false;

				for (int o = 2; o < prodEpsAxiom.length(); o++) {

					// Controllo se il non terminale è presente nell'insieme dei
					// nullable
					if (nullableEps.contains(Character.toString(prodEpsAxiom.charAt(o))) == false) {

						// Ho trovato almeno un simbolo non nullable, quindi il
						// lhs della produzione non è nullable
						nonNullableCheckerAxiomEps = true;
					}
				}

				// Se il checker è false, assioma è nullable
				if (nonNullableCheckerAxiomEps == false) {

					// Aggiungo S ai nullable, se non c'è già
					if (nullableEps.contains(grammar[3].get(0)) == false) {
						nullableEps.add(grammar[3].get(0));
					}

					// Aggiungo la produzione S->e alle produzioni di
					// simpGrammarEps
					simpGrammarEps[2].add(grammar[3].get(0) + ";e");
				}
			}
		}

		// ArrayList per memorizzare i nullable di una produzione
		nullableProdEps = new ArrayList<String>();

		// Ora inserisco le nuove produzioni
		for (int p = 0; p < grammar[2].size(); p++) {

			// Produzione da controllare
			prodEps = grammar[2].get(p);

			// LHS della produzione
			lhsEps = lhsSearch(prodEps, ';');

			// RHS della produzione
			rhsEps = rhsSearch(prodEps, ';');

			// Controllo solo le produzioni senza 'e' sul RHS
			if (rhsEps.indexOf('e') == -1) {

				// Cerco gli eventuali nullable su RHS della produzione
				for (int q = 0; q < rhsEps.length(); q++) {

					// Non terminale da analizzare
					nonTerminalEps = Character.toString(rhsEps.charAt(q));

					// Cerco tutti i nullable nel RHS
					if (nullableEps.contains(nonTerminalEps) == true) {

						// Se un non terminale è nullable lo aggiungo
						// all'arrayList dei simboli nullable specifici della
						// produzione
						nullableProdEps.add(nonTerminalEps);
					}
				}
			}

			// Se ho trovato nullable sul RHS della produzione
			if (nullableProdEps.size() != 0) {

				// Sostituisco prima un sono nullable, poi due, poi tre..
				for (int r = 0; r < nullableProdEps.size(); r++) {

					// Caso Base: sostituisco un solo non terminale nullable
					if (r == 0) {

						for (int s = 0; s < nullableProdEps.size(); s++) {

							// Nuova produzione
							newRhsEps = rhsEps.replaceAll(nullableProdEps.get(s), "");

							newProdEps = lhsEps + ";" + newRhsEps;

							// Controllo se ho eliminato tutto il RHS
							if (newRhsEps.length() != 0) {

								// Aggiungo la nuova produzione a grammar
								simpGrammarEps[2].add(newProdEps);
							}
						}

					} else {

						// Assegnazione per sostituire più di un terminale
						newRhsEps = rhsEps;

						// Sostituisco più di un non terminale
						for (int v = 0; v <= r; v++) {

							// Sostituisco un terminale alla volta
							newRhsEps = newRhsEps.replaceAll(nullableProdEps.get(v), "");

							// Nuova produzione da aggiungere
							newProdEps = lhsEps + ";" + newRhsEps;

							// Controllo se ho eliminato tutto il RHS
							if ((newRhsEps.length() != 0) && (simpGrammarEps[2].contains(newProdEps) == false)) {
								// Aggiungo la nuova produzione a grammar
								simpGrammarEps[2].add(newProdEps);
							}
						}

					}
				}
			}

			// Svuoto l'arrayList dei simboli nullable specifici della
			// produzione
			nullableProdEps.clear();
		}

		// Elimino tutte le produzioni del tipo A->e (tengo eventualmente S->e)
		for (int u = 0; u < grammar[2].size(); u++) {

			lhsEps = lhsSearch(grammar[2].get(u), ';');
			rhsEps = rhsSearch(grammar[2].get(u), ';');

			if ((rhsEps.substring(0).equals("e") == true) && (rhsEps.length() == 1) && (lhsEps.equals(grammar[3].get(0)) == false)) {
				// Rimuovo la produzione dall'arrayList
				grammar[2].remove(u);

				// Aggiorno il contatore per la rimozione
				u = u - 1;
			}
		}

		// Aggiungo elementi a simpGrammarEps
		simpGrammarEps[0].addAll(grammar[0]);

		for(int v = 0; v < grammar[1].size(); v++){
			if(simpGrammarEps[1].contains(grammar[1].get(v)) == false){
				simpGrammarEps[1].add(grammar[1].get(v));
			}
		}

		simpGrammarEps[2].addAll(grammar[2]);

		return simpGrammarEps;
	}

	/**
	 * Eliminates all the trivial unit productions from a set of productions
	 *
	 * @param productions
	 *            set of productions with trivial unit productions
	 *
	 */
	private void eliminateTrivialUnitProductions(ArrayList<String> productions) {

		for (int j = 0; j < productions.size(); j++) {

			lhsTrivial = lhsSearch(productions.get(j), ';');
			rhsTrivial = rhsSearch(productions.get(j), ';');

			// Se RHS = LHS cancello la produzione, che è una trivial Unit
			// Production
			if ((lhsTrivial.charAt(0) == rhsTrivial.charAt(0)) && (rhsTrivial.length() == 1)) {
				productions.remove(j);
			}
		}
	}

	/**
	 * Eliminates all the unit productions from the productions of a given
	 * grammar
	 *
	 * @param grammar
	 *            grammar with unit productions
	 * @return simpGrammarLeftRecursion grammar without unit productions
	 *
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String>[] unitProductions(ArrayList<String>[] grammar) {

		grammarUP = new ArrayList[4];
		ArrayListInit(grammarUP);

		// Elimino le Epsilon productions prima di iniziare
		grammarUP = epsilonProductions(grammar);

		// Inizializzo gli ArrayList della nuova grammatica
		simpGrammarUnit = new ArrayList[4];
		ArrayListInit(simpGrammarUnit);

		// Aggiungo Vt, Vn e S di grammar a simpGrammarUnit
		simpGrammarUnit[0].addAll(grammarUP[0]);
		simpGrammarUnit[3].addAll(grammarUP[3]);

		// Elimino tutte le Trivial Unit Productions
		eliminateTrivialUnitProductions(grammarUP[2]);

		// Costruisco queue FIFO (uso un arrayList)
		queueUnitProd = new ArrayList<String>();

		// Ci inserisco tutte le non trivial unit productions
		for (int k = 0; k < grammarUP[2].size(); k++) {

			// RHS da controllare
			rhsUnitProd = rhsSearch(grammarUP[2].get(k), ';');

			// Controllo se è una non trivial unit production
			if ((rhsUnitProd.length() == 1) && (Character.isUpperCase(rhsUnitProd.charAt(0)) == true)) {
				productionUnitProd = grammarUP[2].get(k);

				// Aggiungo la produzione alla coda
				queueUnitProd.add(productionUnitProd);

			}
		}

		// ArrayList per gestire l'unfolding
		unfoldingUnitProd = new ArrayList<String>();

		// Ora elaboro ogni non trivial unit production nella coda
		for (int l = 0; l < queueUnitProd.size(); l++) {

			// Produzione nella coda da elaborare
			productionUnitProd = queueUnitProd.get(l);
			lhsUnitProd = lhsSearch(productionUnitProd, ';');
			rhsUnitProd = rhsSearch(productionUnitProd, ';');

			// Unfoldo il RHS della non trivial unit production (mi aiuto con
			// unfoldingUnitProd)
			for (int m = 0; m < grammarUP[2].size(); m++) {

				// Data la non trivial unit prod A->B, cerco tutte le produzioni
				// con B sul LHS
				if (rhsUnitProd.charAt(0) == grammarUP[2].get(m).charAt(0)) {

					// Trovata la produzione B->alpha, aggiungo A->alpha ad
					// unfoldingUnitProd
					unfoldingUnitProd.add(lhsUnitProd + ";" + grammarUP[2].get(m).substring(2));
				}
			}

			// Elimino le eventuali trivial unit productions da
			// unfoldingUnitProd, prima di controllarne le produzioni
			eliminateTrivialUnitProductions(unfoldingUnitProd);

			// Aggiungo tutte le non trivial unit production alla coda e le
			// altre all'insieme delle produzioni
			for (int n = 0; n < unfoldingUnitProd.size(); n++) {
				productionUnfoldingUnitProd = unfoldingUnitProd.get(n);
				rhsProductionUnfoldingUnitProd = productionUnfoldingUnitProd.substring(2);

				// Controllo il RHS
				if ((Character.isUpperCase(productionUnfoldingUnitProd.charAt(2)) == true) && (rhsProductionUnfoldingUnitProd.length() == 1)) {

					// Se è non trivial unit production la aggiungo alla coda
					// (se non c'è già)
					if (queueUnitProd.contains(productionUnfoldingUnitProd) == false) {
						queueUnitProd.add(productionUnfoldingUnitProd);
					}

				} else {

					// Se non è non trivial unit production la inserisco
					// nell'insieme delle produzioni (se non c'è già)
					if (grammarUP[2].contains(productionUnfoldingUnitProd) == false) {
						grammarUP[2].add(productionUnfoldingUnitProd);
					}
				}
			}

			// Svuoto l'arrayList di aiuto, per ospitare una nuova produzione
			unfoldingUnitProd.clear();
		}

		// Rimuovo dall'insieme delle produzioni tutte le unitProduction nella
		// coda
		for (int o = 0; o < queueUnitProd.size(); o++) {
			grammarUP[2].remove(queueUnitProd.get(o));
		}

		// Aggiungo le produzioni elaborate a simpGrammarUnit
		simpGrammarUnit[2].addAll(grammarUP[2]);

		//Aggiungo i non terminali a simpGrammarUnit
		for(int v = 0; v < grammar[1].size(); v++){
			if(simpGrammarUnit[1].contains(grammar[1].get(v)) == false){
				simpGrammarUnit[1].add(grammar[1].get(v));
			}
		}

		// Riordino l'insieme delle produzioni
		Collections.sort(simpGrammarUnit[2]);

		return simpGrammarUnit;
	}

	/**
	 * Eliminates all the occurencies of left recursion from the productions of
	 * a given grammar
	 *
	 * @param grammar
	 *            left recursive grammar
	 * @return simpGrammarLeftRecursion non left recursive grammar
	 *
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String>[] leftRecursion(ArrayList<String>[] grammar) {

		grammarLR1 = new ArrayList[4];
		grammarLR2 = new ArrayList[4];

		ArrayListInit(grammarLR1);
		ArrayListInit(grammarLR2);

		// Elimino epsilon productions e unit productions
		grammarLR1 = epsilonProductions(grammar);
		grammarLR2 = unitProductions(grammarLR1);

		// Inizializzo la nuova grammatica
		simpGrammarLeftRecursion = new ArrayList[4];
		ArrayListInit(simpGrammarLeftRecursion);

		// Aggiungo i terminali di grammar a simpGrammarLeftRecursion
		simpGrammarLeftRecursion[0].addAll(grammarLR2[0]);

		// Aggiungo l'assioma
		simpGrammarLeftRecursion[3].addAll(grammarLR2[3]);

		// ArrayList per memorizzare le posizioni delle left recursion
		leftRecursiveProductionsIndex = new ArrayList<Integer>();

		// Controllo tutte le produzioni
		for (int i = 0; i < grammarLR2[2].size(); i++) {

			// Produzione
			productionLeftRecursion = grammarLR2[2].get(i);
			lhsLeftRecursion = lhsSearch(productionLeftRecursion, ';');
			rhsLeftRecursion = rhsSearch(productionLeftRecursion, ';');

			// Controllo se nella produzione c'è left recursion
			if (rhsLeftRecursion.indexOf(lhsLeftRecursion) == 0) {

				// La produzione è left recursive. Memorizzo la posizione
				leftRecursiveProductionsIndex.add(i);
			}
		}

		// Adesso sistemo le Left Recursive Productions che ho trovato
		for (int j = 0; j < leftRecursiveProductionsIndex.size(); j++) {

			// Produzione con left recursion
			indexLeftRecursion = leftRecursiveProductionsIndex.get(j);
			productionLeftRecursion = grammarLR2[2].get(indexLeftRecursion);

			indexNonLeftRecursive = new ArrayList<Integer>();

			// Cerco le altre produzioni con lo stesso LHS di questa produzione,
			// che non abbiano Left Recurision
			for (int k = 0; k < grammarLR2[2].size(); k++) {

				// LHS della produzione da cercare
				lhsLeftRecursion = lhsSearch(grammarLR2[2].get(k), ';');

				// Cerco tutte le produzioni con lhs uguale a quella con left
				// recursion, ma senza left recursion
				if ((lhsLeftRecursion.equals(lhsSearch(productionLeftRecursion, ';')) == true) && (leftRecursiveProductionsIndex.contains(k) == false)) {

					// Memorizzo gli indici delle produzioni non left recursive
					// con lo stesso lhs della produzione recursive
					indexNonLeftRecursive.add(k);
				}
			}

			// Introduco un nuovo simbolo. Genero un carattere casuale, non
			// contenuto nell'insieme dei non terminali
			random = new Random();
			randomChar = (char) (random.nextInt('Z' - 'A' + 1) + 'A');
			randomChar = Character.toUpperCase(randomChar);
			randomCharS = Character.toString(randomChar);

			while (grammarLR2[1].contains(randomChar) == true) {
				randomChar = (char) (random.nextInt('Z' - 'A' + 1) + 'A');
				randomChar = Character.toUpperCase(randomChar);
				randomCharS = Character.toString(randomChar);
			}

			// Aggiungo il nuovo simbolo all'insieme dei non terminali
			grammarLR2[1].add(randomCharS);

			// Inserisco le nuove produzioni con il nuovo simbolo
			for (int l = 0; l < indexNonLeftRecursive.size(); l++) {

				// Prendo ogni produzione senza left recursion, i cui indici
				// sono memorizzati in indexNonLeftRecursive
				indexNLR = indexNonLeftRecursive.get(l);
				productionNLR = grammarLR2[2].get(indexNLR);

				// Aggiongo la nuova produzione con il nuovo simbolo casuale
				// appena generato
				grammarLR2[2].add(productionNLR + randomChar);
			}

			// Inserisco le nuove produzioni (non right recursive e right
			// recursive)
			// LHS
			lhsNRR = randomChar + ";";

			// RHS. Tolgo il simbolo che mi generava left recursion
			rhsNRR = rhsSearch(productionLeftRecursion, ';').replaceFirst(lhsSearch(productionLeftRecursion, ';'), "");

			// Aggiungo le nuove produzioni alla grammatica
			grammarLR2[2].add(lhsNRR + rhsNRR);
			grammarLR2[2].add(lhsNRR + rhsNRR + randomChar);

			// Rimuovo la produzione con left recursion dall'insieme delle
			// produzioni
			grammarLR2[2].remove(indexLeftRecursion);

			// Aggiorno gli indici delle produzioni dopo che ho rimosso (li
			// sposto di una posizione dopo la rimozione)
			for (int m = 0; m < leftRecursiveProductionsIndex.size(); m++) {
				indexCorrectionLR = leftRecursiveProductionsIndex.get(m);
				leftRecursiveProductionsIndex.set(m, indexCorrectionLR - 1);
			}

			// Svuoto l'array di ricerca delle produzioni non left recursive
			indexNonLeftRecursive.clear();
		}

		// Aggiungo elementi alla simpGrammarLeftRecursion
		simpGrammarLeftRecursion[1].addAll(grammarLR2[1]);
		simpGrammarLeftRecursion[2].addAll(grammarLR2[2]);

		return simpGrammarLeftRecursion;
	}

	/**
	 * Parses a string, according the rules of the given grammar, using
	 * Cocke-Younger-Kasami algorithm.
	 *
	 * @param grammar
	 *            grammar that generates the language
	 * @param word
	 *            word to parse
	 * @return true if the word is in the set L(G) (Language generated by
	 *         grammar)
	 *
	 */
	@SuppressWarnings("unchecked")
	public boolean parserCYK(ArrayList<String>[] grammar, String word) {

		// Contorro le una parola è composta solo da terminali in Vt
		for (int i = 0; i < word.length(); i++) {
			subWordPAR = word.substring(i, i + 1);

			// Al primo terminale non presente in Vt return false (la parola non
			// può essere generata da grammar)
			if (grammar[0].contains(subWordPAR) == false) {
				parseResultPAR = false;
				return parseResultPAR;
			}
		}

		grammarCKY1 = new ArrayList[4];
		grammarCKY2 = new ArrayList[4];

		ArrayListInit(grammarCKY1);
		ArrayListInit(grammarCKY2);

		//Elimino unit e epsilon productions
		grammarCKY1 = epsilonProductions(grammar);
		grammarCKY2 = unitProductions(grammarCKY1);

		//Trasformo la grammatica in Chomsky Normal Form

		// Matrice di riconoscimento
		recogMatrixPAR = new String[word.length()][word.length()];

		// ArrayList per memorizzare i LHS delle produzioni con il simbolo che
		// cerco sul RHS
		lhsSymbolPAR = new ArrayList<String>();

		// Riempio la prima riga della matrice del CYK parser
		// Riempio le celle
		for (int j = 0; j < word.length(); j++) {

			// Inizializzo la cella
			recogMatrixPAR[0][j] = "";

			// Terminale da cercare nel RHS delle produzioni
			subWordPAR = word.substring(j, j + 1);

			// Cerco nelle produzioni
			for (int k = 0; k < grammarCKY2[2].size(); k++) {

				// Se trovo il simbolo come unico sul RHS di una produzione
				// allora aggiungo il LHS all'arrayList relativo a questo
				// terminale
				if (subWordPAR.equals(rhsSearch(grammarCKY2[2].get(k), ';')) == true) {

					// Aggiungo il LHS della produzione all'arrayList
					lhsSymbolPAR.add(lhsSearch(grammarCKY2[2].get(k), ';'));
				}

			}

			// Riempio la cella con gli elementi di lhsSymbolPAR
			for (int l = 0; l < lhsSymbolPAR.size(); l++) {
				recogMatrixPAR[0][j] = recogMatrixPAR[0][j] + lhsSymbolPAR.get(l);
				if (l != lhsSymbolPAR.size() - 1) {
					recogMatrixPAR[0][j] = recogMatrixPAR[0][j];
				}
			}

			// Svuoto l'arrayList dei LHS relativi a quel simbolo
			lhsSymbolPAR.clear();
		}

		// ArrayList per memorizzare i simboli delle celle
		cykHelper = new ArrayList<String>();

		// ArrayList per cercare nei RHS delle produzioni i simboli costruiti
		// con cykHelper
		cykProductionsComb = new ArrayList<String>();

		// Indice di riga (riempio il resto della matrice)
		for (int i = 1; i < word.length(); i++) {

			// Indice di colonna (riempio solo le celle necessarie)
			for (int j = 0; j < word.length() - i; j++) {

				// Inizializzo la cella
				recogMatrixPAR[i][j] = "";

				// Seleziono i simboli rilevanti delle righe precedenti (mi
				// serve una coppia di celle)
				// K = secondo indice di riga (k va da 1 a i-1)
				for (int k = 0; k < i; k++) {

					// Prima cella della coppia
					cykHelper.add(recogMatrixPAR[k][j]);

					// Seconda cella della coppia
					cykHelper.add(recogMatrixPAR[i - k - 1][j + k + 1]);
				}

				// Ora ho tutte le celle significative a coppie
				// Costruisco gli elementi da cercare nei RHS delle produzioni
				for (int l = 0; l < cykHelper.size(); l = l + 2) {

					// Costruisco le combinazioni di non terminali, presi dalla
					// coppia di celle
					// ES: se ho A,C e B,F le possibili produzioni sono: AB, AF,
					// CB, CF
					// Queste poi le cerco nell'insieme delle produzioni

					// Coppia di celle da cui creare le combinazioni di non
					// terminali
					firstCellPAR = "";
					secondCellPAR = "";

					// Gli assegno i valori
					firstCellPAR = cykHelper.get(l);
					secondCellPAR = cykHelper.get(l + 1);

					// Costruisco le possibili combinazioni da cercare nei rhs
					// delle produzioni
					combinationsPAR = rhsBuilder(firstCellPAR, secondCellPAR);

					for (int q = 0; q < combinationsPAR.size(); q++) {
						if (cykProductionsComb.contains(combinationsPAR.get(q)) == false) {
							cykProductionsComb.add(combinationsPAR.get(q));
						}
					}
				}

				// Cerco nei RHS delle produzioni
				for (int m = 0; m < cykProductionsComb.size(); m++) {

					// Controllo tutti i RHS delle produzioni di grammar
					for (int n = 0; n < grammarCKY2[2].size(); n++) {

						// Se trovo la combinazione su qualche RHS, aggiungo il
						// LHS della produzione alla cella
						if (cykProductionsComb.get(m).equals(rhsSearch(grammarCKY2[2].get(n), ';')) == true) {
							recogMatrixPAR[i][j] = recogMatrixPAR[i][j] + lhsSearch(grammarCKY2[2].get(n), ';');
						}
					}
				}

				// Svuoto gli arraylists
				cykProductionsComb.clear();
				cykHelper.clear();
			}
		}

		System.out.println();
		for (int p = 0; p < recogMatrixPAR.length; p++) {

			System.out.print("CYK Matrix - row " + p + ": ");
			for (int o = 0; o < recogMatrixPAR[p].length; o++) {
				if (recogMatrixPAR[p][o] != null) {
					System.out.print(recogMatrixPAR[p][o] + "  ");
				}
			}
			System.out.println();
		}

		// Controllo se l'assioma è nell'ultima casella
		if (recogMatrixPAR[word.length() - 1][0].indexOf(grammarCKY2[3].get(0)) != -1) {
			parseResultPAR = true;
		} else {
			parseResultPAR = false;
		}

		System.out.println();
		return parseResultPAR;
	}

	/**
	 * Builds all the possible combinations of productions combining non
	 * terminals in the first string, with non terminals on the second string.
	 * Only non terminals of the first string can occour on the first positions,
	 * thus only non terminals on the second string can occour on the second
	 * position
	 *
	 * @param firstString
	 *            first string of the combination
	 * @param secondString
	 *            second string of the combination
	 * @return cellsCombinations ArrayList of all the possible combinations
	 *
	 */
	private ArrayList<String> rhsBuilder(String firstString, String secondString) {

		// ArrayList per memorizzare non terminali della prima stringa
		firstStringNT = new ArrayList<String>();

		// ArrayList per memorizzare non terminali della seconda stringa
		secondStringNT = new ArrayList<String>();

		// ArrayList delle combinazioni
		cellsCombinations = new ArrayList<String>();

		// Aggiungo gli elementi al primo arrayList
		if (firstString.length() != 0) {

			for (int i = 0; i < firstString.length(); i++) {

				// Controllo se ho più di un non terminale
				if (firstString.length() > 0) {

					// Aggiungo la stringa fino al separatore all'arrayList
					firstStringNT.add(firstString.substring(0, 1));

					// Aggiorno la stringa
					firstString = firstString.substring(1);

					// Aggiorno l'indice del for per farlo ricominciare
					i = -1;

				} else {

					// Ho solo un terminale, lo aggiungo all'arraylist
					firstStringNT.add(firstString);
				}

			}

		} else {

			// la seconda stringa è vuota, aggiungo l'elemento vuoto
			firstStringNT.add("");
		}

		// Aggiungo gli elementi al secondo arrayList
		if (secondString.length() != 0) {

			for (int i = 0; i < secondString.length(); i++) {

				// Controllo se ho più di un non terminale
				if (secondString.length() > 0) {

					// Aggiungo la stringa fino al separatore all'arrayList
					secondStringNT.add(secondString.substring(0, 1));

					// Aggiorno la stringa
					secondString = secondString.substring(1);

					// Aggiorno l'indice del for per farlo ricominciare
					i = -1;

				} else {

					// Ho solo un terminale, lo aggiungo all'arraylist
					secondStringNT.add(secondString);
				}

			}

		} else {

			// la seconda stringa è vuota, aggiungo l'elemento vuoto
			secondStringNT.add("");
		}

		// Ora costruisco le combinazioni
		for (int i = 0; i < firstStringNT.size(); i++) {

			for (int j = 0; j < secondStringNT.size(); j++) {

				// Aggiungo le combinazioni all'arraylist
				cellsCombinations.add(firstStringNT.get(i) + secondStringNT.get(j));
			}
		}

		return cellsCombinations;
	}

	/*
	 * CLASS TESTING
	 *  - From Below alg. grammar = S->XY|a;Z->b.
	 *
	 * Results Grammatica Semplificata (From Below): Insieme dei terminali (Vt) =
	 * {{a},{b}} Insieme dei non terminali (Vn) = {{S},{Z}} Insieme delle
	 * produzioni (P) = {{S;a},{Z;b}} Assioma (S) = {{S}}
	 *
	 *  - From Above alg. grammar = S->a;Z->b.
	 *
	 * Results Grammatica Semplificata (From Above): Insieme dei terminali (Vt) =
	 * {{a}} Insieme dei non terminali (Vn) = {{S}} Insieme delle produzioni (P) =
	 * {{S;a}} Assioma (S) = {{S}}
	 *
	 *  - Elimination of Epsilon Productions grammar = S->ABC;A->e;B->e;C->c.
	 *
	 * Results Insieme dei terminali (Vt) = {{c}} Insieme dei non terminali (Vn) =
	 * {{S}} Insieme delle produzioni (P) = {{S;BC},{S;AC},{S;C},{S;ABC},{C;c}}
	 * Assioma (S) = {{S}}
	 *
	 *  - Elimination of Unit Productions grammar = S->AS|A;A->a|B;B->b|S|A.
	 *
	 * Results Insieme dei terminali (Vt) = {{a},{b}} Insieme dei non terminali
	 * (Vn) = {{S},{A},{B}} Insieme delle produzioni (P) =
	 * {{A;AS},{A;a},{A;b},{B;AS},{B;a},{B;b},{S;AS},{S;a},{S;b}} Assioma (S) =
	 * {{S}}
	 *
	 *  - Avoiding Left Recursion grammar = S->A|b;A->AB|a.
	 *
	 * Results Insieme dei terminali (Vt) = {{b},{a}} Insieme dei non terminali
	 * (Vn) = {{S},{A},{B},{Z}} Insieme delle produzioni (P) =
	 * {{S;b},{A;a},{S;AB},{S;a},{A;aZ},{Z;B},{Z;BZ}} Assioma (S) = {{S}}
	 *
	 *  - CYK Parser grammar =
	 * S->CB|FA|FB;A->CS|FD|a;B->FS|CE|b;C->a;D->AA;E->BB;F->b.
	 *
	 * Results word = aababb //E' generabile dalla grammatica word = aaa //Non è
	 * generabile dalla grammatica word = aaaababababbbb //E' generabile dalla
	 * grammatica
	 *
	 */

}
