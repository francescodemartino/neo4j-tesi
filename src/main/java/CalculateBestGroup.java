import java.util.ArrayList;
import java.util.List;

public class CalculateBestGroup {
    public static ResultBestGroup getBestGroup(String cluster, List<GroupQueries> listGroupQueries) {
        List<GroupQueries> listToReturn = new ArrayList<>();

        listGroupQueries.sort((first, second) -> Double.compare(second.getCost(null, null), first.getCost(null, null)));
        if (listGroupQueries.size() > 0) {
            listToReturn.add(listGroupQueries.get(0));
            double lastCost = 0;
            double currentCost = listGroupQueries.get(0).getCost(null, null);

            while (listToReturn.size() < listGroupQueries.size()) {
                lastCost = currentCost;
                listGroupQueries.sort((first, second) -> {
                    if (first.isOnTop(listToReturn, null)) {
                        return -1;
                    } else if (second.isOnTop(listToReturn, null)) {
                        return 1;
                    } else {
                        return Double.compare(second.getCost(listToReturn, null), first.getCost(listToReturn, null));
                    }
                });
                // listGroupQueries.forEach(groupQueries -> System.out.println(groupQueries.getCost(listToReturn, null)));
                // System.out.println("-----------------");
                // System.out.println("listToReturn.size(): " + listToReturn.size());
                // System.out.println("listGroupQueries.size(): " + listGroupQueries.size());
                currentCost = listGroupQueries.get(0).getCost(null, listToReturn);
                // System.out.println("lastCost: " + lastCost);
                // System.out.println("currentCost: " + currentCost);
                if (lastCost <= currentCost) {
                    listToReturn.add(listGroupQueries.get(0));
                } else {
                    break;
                }
            }
            // System.out.println("listToReturn size: " + listToReturn.size());
            // System.out.println("++++++++++++++++++++");
        }
        return new ResultBestGroup(cluster, listToReturn);
    }
}
