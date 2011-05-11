package generators;

import interfaces.Generator;

public class UniformLongGenerator implements Generator {

	private UniformDoubleGenerator rng;
	private Long rangeStart;
	private Long rangeEnd;
	
	public UniformLongGenerator(Long rangeStart, Long rangeEnd, Long seed){
		
		rng = new UniformDoubleGenerator(seed);
		
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}
	
	
	public Long generateNextValue(){
		Double d = rangeStart + (rangeEnd - rangeStart) * rng.generateNextValue();
		Long l = d.longValue();
		return l;
	}


	public void setRangeEnd(Long rangeEnd) {
		this.rangeEnd = rangeEnd;
	}
	
}
