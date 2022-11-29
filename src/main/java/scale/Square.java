package scale;

import GroupQuery.GroupQueries;
import GroupQuery.ResponseCost;
import utilis.Utility;

import java.util.Arrays;
import java.util.List;

public class Square extends Scaling {
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
        squareNumerator = Math.sqrt(Arrays.stream(listNumerator).map(operand -> operand * operand).sum());
        squareDenominator = Math.sqrt(Arrays.stream(listDenominator).map(operand -> operand * operand).sum());
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
