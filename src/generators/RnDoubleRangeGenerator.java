package generators;

public class RnDoubleRangeGenerator extends RnGenerator {

	private Long rangeA;
	private Long rangeB;
	
	public RnDoubleRangeGenerator(Long rangeA, Long rangeB, Long seed){

		super(seed);
		
		this.rangeA = rangeA;
		this.rangeB = rangeB;

	}
	
	public Double getNextValue(){
		return rangeA + (rangeB - rangeA) * super.getNextValue();
	}

	public Long getRangeA() {
		return rangeA;
	}

	public Long getRangeB() {
		return rangeB;
	}
}
