package info.ferrarimarco.android.mp.codicefiscale.helper;

import android.annotation.SuppressLint;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CodiceFiscaleHelper {

	private static Map<String, Integer> evenLetterValues;
	private static Map<String, Integer> oddLetterValues;
	private static Map<Integer, String> codeValues;
	
	public static String computeCodiceFiscale(String surname, String name, Calendar birthDate, String birthPlace, boolean male){
		
		String surnameCode = computeSurname(surname);
		String nameCode = computeName(name);
		String birthDateYear = computeBirthDateYear(birthDate);
		String birthDateMonth = computeBirthDateMonth(birthDate);
		String birthDateDay = computeBirthDateDay(birthDate, male);
		String birthPlaceCode = computeBirthPlaceCode(birthPlace);
		
		String result = surnameCode + nameCode + birthDateYear + birthDateMonth + birthDateDay + birthPlaceCode;
		
		String controlCode = computeControlCode(result);
		
		result += controlCode;
		
		return result;
	}
	
	public static String computeSurname(String surname){
		surname = surname.replaceAll("\\s", "");
		surname = surname.toUpperCase(Locale.ITALIAN);
		
		String surnameCons = "";
		String surnameVows = "";
		
		surnameCons = surname.replaceAll("[AEIOU]", "");
		surnameVows = surname.replaceAll("[BCDFGHJKLMNPQRSTVWXYZ]", "");
		
		String result = "";
		
		if(surname.isEmpty()){
			result = "XXX";
		}else{
			if(surnameCons.length() >= 3){
				result = surnameCons.substring(0, 3);
			}else if(surnameCons.length() == 2){
				if(!surnameVows.isEmpty()){
					result = surnameCons + surnameVows.substring(0, 1);
				}else{
					result = surnameCons + "X";
				}
			}else if(surnameCons.length() == 1){
				if(!surnameVows.isEmpty()){
					if(surnameVows.length() >= 2){
						result = surnameCons + surnameVows.substring(0, 2);
					}else if(surnameVows.length() == 1){
						result = surnameCons + surnameVows + "X";
					}
				}else{
					result = surnameCons + "XX";
				}
			}else{// no consonants
				if(surnameVows.length() >= 3){
					result = surnameVows.substring(0, 3);
				}else if(surnameVows.length() == 2){
					result = surnameVows + "X";
				}else if(surnameVows.length() == 1){
					result = surnameVows + "XX";
				}else{
					result = "XXX";
				}
			}
		}
		
		return result;
	}
	
	public static String computeName(String name){
		name = name.replaceAll("\\s", "");
		name = name.toUpperCase(Locale.ITALIAN);
		
		String nameCons = "";
		String nameVows = "";
		
		nameCons = name.replaceAll("[AEIOU]", "");
		nameVows = name.replaceAll("[BCDFGHJKLMNPQRSTVWXYZ]", "");
		
		String result = "";
		
		if(!name.isEmpty()){
			if(nameCons.length() >= 4){
				result = nameCons.substring(0, 1) + nameCons.substring(2, 4);
			}else if(nameCons.length() == 3){
				result = nameCons;
			}else if(nameCons.length() == 2){
				if(nameVows.isEmpty()){
					result = nameCons + "X";
				}else{
					result = nameCons + nameVows.substring(0, 1);
				}
			}else if(nameCons.length() == 1){
				if(nameVows.isEmpty()){
					result = nameCons + "XX";
				}else{
					if(nameVows.length() >= 2){
						result = nameCons + nameVows.substring(0, 2);
					}else if(nameVows.length() == 1){
						result = nameCons + nameVows + "X";
					}
				}
			}else{
				if(nameVows.length() >= 3){
					result = nameVows.substring(0, 3);
				}else if(nameVows.length() == 2){
					result = nameVows + "X";
				}else if(nameVows.length() == 1){
					result = nameVows + "XX";
				}
			}
		}else{
			result = "XXX";
		}		
		
		return result;
	}
	
	public static String computeBirthDateYear(Calendar birthDate){
		int year = birthDate.get(Calendar.YEAR);
		
		String result = Integer.toString(year);
		
		return result.substring(result.length() - 2, result.length());
	}
	
	public static String computeBirthDateMonth(Calendar birthDate){
		int month = birthDate.get(Calendar.MONTH);
		
		String result = "";
		
		if(month == 1){
			result = "A";
		}else if(month == 2){
			result = "B";
		}else if(month == 3){
			result = "C";
		}else if(month == 4){
			result = "D";
		}else if(month == 5){
			result = "E";
		}else if(month == 6){
			result = "H";
		}else if(month == 7){
			result = "L";
		}else if(month == 8){
			result = "M";
		}else if(month == 9){
			result = "P";
		}else if(month == 10){
			result = "R";
		}else if(month == 11){
			result = "S";
		}else if(month == 12){
			result = "T";
		}else{
			throw new RuntimeException();
		}
		
		return result;
	}
	
	public static String computeBirthDateDay(Calendar birthDate, boolean male){
		int day = birthDate.get(Calendar.DAY_OF_MONTH);

		day = (male) ? day : day + 40;
		
		return Integer.toString(day);
	}
	
	public static String computeBirthPlaceCode(String birthPlace){
		return "H501";
	}
	
	public static String computeControlCode(String codiceFiscale){
		
		if(evenLetterValues == null || oddLetterValues == null || codeValues == null){
			initControlCodeMaps();
		}
		
		int sum = 0;
		
		for(int i = 1; i < codiceFiscale.length() + 1; i++){
			
			String letter = codiceFiscale.substring(i - 1, i).toUpperCase(Locale.ITALIAN);
			
			if(i % 2 == 0){
				sum += evenLetterValues.get(letter);
			}else{
				sum += oddLetterValues.get(letter);
			}
		}
		
		Integer result = sum % 26;
		
		String ret = codeValues.get(result);
		
		return ret;
	}
	
	@SuppressLint("UseSparseArrays")
	private static void initControlCodeMaps(){
		oddLetterValues = new HashMap<String, Integer>(36);
		evenLetterValues = new HashMap<String, Integer>(36);
		codeValues = new HashMap<Integer, String>(36);
		
		oddLetterValues.put("A", 1);
		oddLetterValues.put("0", 1);
		oddLetterValues.put("B", 0);
		oddLetterValues.put("1", 0);
		oddLetterValues.put("C", 5);
		oddLetterValues.put("2", 5);
		oddLetterValues.put("D", 7);
		oddLetterValues.put("3", 7);
		oddLetterValues.put("E", 9);
		oddLetterValues.put("4", 9);
		oddLetterValues.put("F", 13);
		oddLetterValues.put("5", 13);
		oddLetterValues.put("G", 15);
		oddLetterValues.put("6", 15);
		oddLetterValues.put("H", 17);
		oddLetterValues.put("7", 17);
		oddLetterValues.put("I", 19);
		oddLetterValues.put("8", 19);
		oddLetterValues.put("J", 21);
		oddLetterValues.put("9", 21);
		oddLetterValues.put("K", 2);
		oddLetterValues.put("L", 4);
		oddLetterValues.put("M", 18);
		oddLetterValues.put("N", 20);
		oddLetterValues.put("O", 11);
		oddLetterValues.put("P", 3);
		oddLetterValues.put("Q", 6);
		oddLetterValues.put("R", 8);
		oddLetterValues.put("S", 12);
		oddLetterValues.put("T", 14);
		oddLetterValues.put("U", 16);
		oddLetterValues.put("V", 10);
		oddLetterValues.put("W", 22);
		oddLetterValues.put("X", 25);
		oddLetterValues.put("Y", 24);
		oddLetterValues.put("Z", 23);
		
		evenLetterValues.put("A", 0);
		evenLetterValues.put("0", 0);
		evenLetterValues.put("B", 1);
		evenLetterValues.put("1", 1);
		evenLetterValues.put("C", 2);
		evenLetterValues.put("2", 2);
		evenLetterValues.put("D", 3);
		evenLetterValues.put("3", 3);
		evenLetterValues.put("E", 4);
		evenLetterValues.put("4", 4);
		evenLetterValues.put("F", 5);
		evenLetterValues.put("5", 5);
		evenLetterValues.put("G", 6);
		evenLetterValues.put("6", 6);
		evenLetterValues.put("H", 7);
		evenLetterValues.put("7", 7);
		evenLetterValues.put("I", 8);
		evenLetterValues.put("8", 8);
		evenLetterValues.put("J", 9);
		evenLetterValues.put("9", 9);
		evenLetterValues.put("K", 10);
		evenLetterValues.put("L", 11);
		evenLetterValues.put("M", 12);
		evenLetterValues.put("N", 13);
		evenLetterValues.put("O", 14);
		evenLetterValues.put("P", 15);
		evenLetterValues.put("Q", 16);
		evenLetterValues.put("R", 17);
		evenLetterValues.put("S", 18);
		evenLetterValues.put("T", 19);
		evenLetterValues.put("U", 20);
		evenLetterValues.put("V", 21);
		evenLetterValues.put("W", 22);
		evenLetterValues.put("X", 23);
		evenLetterValues.put("Y", 24);
		evenLetterValues.put("Z", 25);
		
		codeValues.put(0, "A");
		codeValues.put(1, "B");
		codeValues.put(2, "C");
		codeValues.put(3, "D");
		codeValues.put(4, "E");
		codeValues.put(5, "F");
		codeValues.put(6, "G");
		codeValues.put(7, "H");
		codeValues.put(8, "I");
		codeValues.put(9, "J");
		codeValues.put(10, "K");
		codeValues.put(11, "L");
		codeValues.put(12, "M");
		codeValues.put(13, "N");
		codeValues.put(14, "O");
		codeValues.put(15, "P");
		codeValues.put(16, "Q");
		codeValues.put(17, "R");
		codeValues.put(18, "S");
		codeValues.put(19, "T");
		codeValues.put(20, "U");
		codeValues.put(21, "V");
		codeValues.put(22, "W");
		codeValues.put(23, "X");
		codeValues.put(24, "Y");
		codeValues.put(25, "Z");
	}
}
