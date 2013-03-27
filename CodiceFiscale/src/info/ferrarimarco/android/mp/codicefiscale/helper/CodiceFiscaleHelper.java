package info.ferrarimarco.android.mp.codicefiscale.helper;

import java.util.Calendar;
import java.util.Locale;

public class CodiceFiscaleHelper {

	public String computeCodiceFiscale(String surname, String name, Calendar birthDate, String birthPlace, boolean male){
		return "";
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
	
	public String computeBirthDateYear(Calendar birthDate){
		int year = birthDate.get(Calendar.YEAR);
		
		String result = Integer.toString(year);
		
		return result.substring(result.length() - 2, result.length());
	}
	
	public String computeBirthDateMonth(Calendar birthDate){
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
	
	public String computeBirthDateDay(Calendar birthDate, boolean male){
		int day = birthDate.get(Calendar.DAY_OF_MONTH);

		day = (male) ? day : day + 40;
		
		return Integer.toString(day);
	}
}
