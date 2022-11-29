package scale;

public class ScalingFactory {
    public static String typeScaling = "none";

    public static Scaling getScaling(String typology) {
        switch (typology) {
            case "sd-avg":
                return new SdAvg();
            case "min-max":
                return new MinMax();
            case "square":
                return new Square();
            case "none":
                return new None();
        }
        return new None();
    }
}
