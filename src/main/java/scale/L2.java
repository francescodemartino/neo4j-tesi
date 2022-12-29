package scale;

import GroupQuery.GroupQueries;
import GroupQuery.ResponseCost;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;

// https://scicomp.stackexchange.com/questions/22094/normalize-data-so-that-the-sum-of-squares-1
public class L2 extends Scaling {
    protected BigDecimal squareNumerator;
    protected BigDecimal squareDenominator;
    protected MathContext mc = new MathContext(10);

    @Override
    public void calculateCost(List<GroupQueries> listGroupQueries, List<GroupQueries> groupsToRemove, List<GroupQueries> groups) {
        double[] listNumerator = new double[listGroupQueries.size()];
        double[] listDenominator = new double[listGroupQueries.size()];
        for (int i = 0; i < listGroupQueries.size(); i++) {
            ResponseCost responseCost = listGroupQueries.get(i).getCost(groupsToRemove, groups, null);
            listNumerator[i] = responseCost.getNumerator();
            listDenominator[i] = responseCost.getDenominator();
        }
        calculateCost(listNumerator, listDenominator);
    }

    @Override
    public void calculateCost(double[] listNumerator, double[] listDenominator) {
        squareNumerator = Arrays.stream(listNumerator).mapToObj(operand -> new BigDecimal(operand).multiply(new BigDecimal(operand))).reduce(BigDecimal.ZERO, BigDecimal::add).sqrt(mc);
        squareDenominator = Arrays.stream(listDenominator).mapToObj(operand -> new BigDecimal(operand).multiply(new BigDecimal(operand))).reduce(BigDecimal.ZERO, BigDecimal::add).sqrt(mc);
    }

    @Override
    public double getCostNumerator(double value) {
        return (new BigDecimal(value).divide(squareNumerator, mc)).doubleValue();
    }

    @Override
    public double getCostDenominator(double value) {
        return (new BigDecimal(value).divide(squareDenominator, mc)).doubleValue();
    }
}
