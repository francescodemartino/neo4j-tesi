package scale;

public class ScalingFactory {
    public static String typeScaling = "none";

    public static Scaling getScaling(String typology) {
        switch (typology) {
            case "sd-avg":
                return new SdAvg();
            case "min-max":
                return new MinMax();
            case "l1":
                return new L1();
            case "l2":
                return new L2();
            case "none":
                return new None();
        }
        return new None();
    }
}
