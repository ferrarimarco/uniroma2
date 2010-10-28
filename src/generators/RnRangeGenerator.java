package generators;

public class RnRangeGenerator extends RnSequenceGenerator {

	private Long rangeA;
	private Long rangeB;
	
	public RnRangeGenerator(Long rangeA, Long rangeB){
		
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
