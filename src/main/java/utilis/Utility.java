package utilis;

import java.util.Arrays;

public class Utility {
    public static double calculateSd(double[] numArray)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }

    public static double calculateAvg(double[] numArray) {
        return Arrays.stream(numArray).sum() / numArray.length;
    }
}
