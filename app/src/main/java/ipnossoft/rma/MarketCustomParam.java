package ipnossoft.rma;

public enum MarketCustomParam {
    AMAZON,
    BLACKBERRY,
    GOOGLE,
    HANDMARK,
    MOBIROO,
    NOOK,
    SAMSUNG,
    SNAPPCLOUD,
    TSTORE;

    private MarketCustomParam() {
    }

    public static MarketCustomParam fromString(String var0) {
        return valueOf(var0.toUpperCase());
    }
}
