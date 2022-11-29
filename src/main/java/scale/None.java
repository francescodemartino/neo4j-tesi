package scale;

import GroupQuery.GroupQueries;
import GroupQuery.ResponseCost;
import utilis.Utility;

import java.util.List;

public class None extends Scaling {
    @Override
    public void calculateCost(List<GroupQueries> listGroupQueries, List<GroupQueries> groupsToRemove, List<GroupQueries> groups) { }

    @Override
    public void calculateCost(double[] listNumerator, double[] listDenominator) { }

    @Override
    public double getCostNumerator(double value) {
        return value;
    }

    @Override
    public double getCostDenominator(double value) {
        return value;
    }
}
