package generators;

public class SeedCalculator {
	
	private static Long s = 1L;

	public static Long getSeme() {
		if((s + 2) % 5 == 0)
			return (s = s + 4L);
		else
			return (s = s + 2L);
		
	}
}
