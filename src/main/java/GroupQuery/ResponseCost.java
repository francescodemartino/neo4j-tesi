package GroupQuery;

public class ResponseCost {
    private final double numerator;
    private final double denominator;
    private final double cost;

    public ResponseCost(double numerator, double denominator, double cost) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.cost = cost;
    }

    public double getNumerator() {
        return numerator;
    }

    public double getDenominator() {
        return denominator;
    }

    public double getCost() {
        return cost;
    }
}
