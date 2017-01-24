package components;

public class Range {
    private final double min;
    private final double mean;
    private final double max;


    public Range(double min, double mean, double max) {
        this.min = min;
        this.mean = mean;
        this.max = max;
    }

    public boolean containsMean(double currentMean) { return currentMean >= min && currentMean <= max; }

    
    /* GETTER */
    public double getMin() { return min; }

    public double getMean() { return mean; }

    public double getMax() { return max; }
}