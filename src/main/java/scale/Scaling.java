package scale;

import GroupQuery.GroupQueries;

import java.util.List;

public abstract class Scaling {

    public abstract void calculateCost(List<GroupQueries> listGroupQueries, List<GroupQueries> groupsToRemove, List<GroupQueries> groups);

    public abstract void calculateCost(double[] listNumerator, double[] listDenominator);

    public abstract double getCostNumerator(double value);

    public abstract double getCostDenominator(double value);
}
