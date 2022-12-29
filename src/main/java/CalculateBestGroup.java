import GroupQuery.GroupQueries;
import scale.Scaling;
import scale.ScalingFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CalculateBestGroup {
    public static ResultBestGroup getBestGroup(String cluster, List<GroupQueries> listGroupQueries) {
        List<GroupQueries> listToReturn = new ArrayList<>();

        if (listGroupQueries.size() == 0) {
            return new ResultBestGroup(cluster, listToReturn);
        } else if (listGroupQueries.size() == 1) {
            listToReturn.add(listGroupQueries.get(0));
            return new ResultBestGroup(cluster, listToReturn);
        }

        Scaling scaling = ScalingFactory.getScaling(ScalingFactory.typeScaling);
        scaling.calculateCost(listGroupQueries, null, null);

        listGroupQueries = listGroupQueries.stream().filter(groupQueries -> groupQueries.getQueries().size() != 0).collect(Collectors.toList());

        listGroupQueries.sort((first, second) -> Double.compare(second.getCost(null, null, scaling).getCost(), first.getCost(null, null, scaling).getCost()));


        List<Double> numerator = new ArrayList<>();
        List<Double> denominator = new ArrayList<>();
        System.out.println("-------------------------------------------");
        for (GroupQueries listGroupQuery : listGroupQueries) {
            numerator.add(listGroupQuery.getCost(null, null, scaling).getNumerator());
            denominator.add(listGroupQuery.getCost(null, null, scaling).getDenominator());
        }
        Collections.sort(numerator);
        Collections.sort(denominator);
        for (int i = 0; i < numerator.size(); i++) {
            System.out.println("(" + i + "," + new BigDecimal(numerator.get(i)) + ")");
        }
        System.out.println("++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < denominator.size(); i++) {
            System.out.println("(" + i + "," + new BigDecimal(denominator.get(i)) + ")");
        }
        System.out.println("-------------------------------------------");


        if (listGroupQueries.size() > 0) {
            listToReturn.add(listGroupQueries.get(0));
            /*System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + listGroupQueries.get(0).getQueries().size());
            System.out.print("----> ");
            for (GroupQueries listGroupQuery : listGroupQueries) {
                System.out.print(listGroupQuery.getQueries().size() + " ");
            }
            System.out.println("");*/

            listGroupQueries.get(0).setAdded(true);
            Scaling scalingCopy = ScalingFactory.getScaling(ScalingFactory.typeScaling);
            scalingCopy.calculateCost(listGroupQueries, null, listToReturn);

            while (listToReturn.size() < listGroupQueries.size()) {
                listGroupQueries.sort((first, second) -> Double.compare(second.getCost(null, listToReturn, scalingCopy).getCost(), first.getCost(null, listToReturn, scalingCopy).getCost()));
                GroupQueries groupQueries = listGroupQueries.get(0);
                if (groupQueries.isAdded()) {
                    break;
                } else {
                    listToReturn.add(groupQueries);
                    groupQueries.setAdded(true);
                }
            }
            for (GroupQueries groupQuery : listGroupQueries) {
                groupQuery.setAdded(false);
            }
        }
        return new ResultBestGroup(cluster, listToReturn);
    }
}
