package scale;

import GroupQuery.GroupQueries;
import GroupQuery.ResponseCost;
import utilis.Utility;

import java.util.List;

public class SdAvg extends Scaling {
    protected double sdNumerator;
    protected double avgNumerator;
    protected double sdDenominator;
    protected double avgDenominator;

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
        sdNumerator = Utility.calculateSd(listNumerator);
        avgNumerator = Utility.calculateAvg(listNumerator);
        sdDenominator = Utility.calculateSd(listDenominator);
        avgDenominator = Utility.calculateAvg(listDenominator);
    }



    public double getSdNumerator() {
        return sdNumerator;
    }

    public double getAvgNumerator() {
        return avgNumerator;
    }

    public double getSdDenominator() {
        return sdDenominator;
    }

    public double getAvgDenominator() {
        return avgDenominator;
    }

    @Override
    public double getCostNumerator(double value) {
        return (value - getAvgNumerator()) / getSdNumerator();
    }

    @Override
    public double getCostDenominator(double value) {
        return (value - getAvgDenominator()) / getSdDenominator();
    }
}
