package scale;

import GroupQuery.GroupQueries;
import GroupQuery.ResponseCost;

import java.util.Arrays;
import java.util.List;

public class MinMax extends Scaling {
    double minNumerator;
    double maxNumerator;
    double minDenominator;
    double maxDenominator;

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
        minNumerator = Arrays.stream(listNumerator).min().getAsDouble();
        maxNumerator = Arrays.stream(listNumerator).max().getAsDouble();
        minDenominator = Arrays.stream(listDenominator).min().getAsDouble();
        maxDenominator = Arrays.stream(listDenominator).max().getAsDouble();
    }

    @Override
    public double getCostNumerator(double value) {
        return (value - minNumerator) / (maxNumerator - minNumerator);
    }

    @Override
    public double getCostDenominator(double value) {
        return (value - minDenominator) / (maxDenominator - minDenominator);
    }
}
