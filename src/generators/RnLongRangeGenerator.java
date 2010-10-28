package generators;

public class RnLongRangeGenerator {

	private RnSequenceGenerator rng;
	private Long rangeStart;
	private Long rangeEnd;
	
	public RnLongRangeGenerator(Long rangeStart, Long rangeEnd, Long seed){
		
		rng = new RnSequenceGenerator(seed);
		
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}
	
	
	public Long getNextValue(){
		Double d = rangeStart + (rangeEnd - rangeStart) * rng.getNextValue();
		Long l = d.longValue();
		return l;
	}
	
}
