package info.ferrarimarco.uniroma2.is.service.persistence;

public interface CounterService {
    long getNextCategorySequence();
    long getNextClazzSequence();
    long getNextProductSequence();
    long getNextProductEntitySequence();
    long getNextEntityStatSequence();
}
