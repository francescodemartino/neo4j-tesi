package scale;

import GroupQuery.GroupQueries;
import GroupQuery.ResponseCost;

import java.util.Arrays;
import java.util.List;

// https://scicomp.stackexchange.com/questions/22094/normalize-data-so-that-the-sum-of-squares-1
public class L1 extends Scaling {
    protected double squareNumerator;
    protected double squareDenominator;

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
        squareNumerator = Arrays.stream(listNumerator).map(Math::abs).sum();
        squareDenominator = Arrays.stream(listDenominator).map(Math::abs).sum();
    }

    @Override
    public double getCostNumerator(double value) {
        return value / squareNumerator;
    }

    @Override
    public double getCostDenominator(double value) {
        return value / squareDenominator;
    }
}
