package ipnossoft.rma.upgrade;

public class StrokePriceGenerator {
  private String priceLimitedOffer;

  public StrokePriceGenerator(String var1) {
    this.priceLimitedOffer = var1;
  }

  public String generate() {
    String var1 = this.priceLimitedOffer.replaceAll("[^\\d,.]", "");
    String var2;
    if(var1.contains(",")) {
      var2 = ",";
    } else {
      var2 = ".";
    }

    var1 = var1.replaceAll(",", ".");
    String var4 = this.priceLimitedOffer.replaceAll(var1, "");
    boolean var5;
    if(this.priceLimitedOffer.indexOf(var4) == 0) {
      var5 = true;
    } else {
      var5 = false;
    }

    double var9 = Double.parseDouble(SubscriptionBuilderUtils.cleansePrice(this.priceLimitedOffer)) * 3.0D;
    int var7 = (int)((var9 - Math.floor(var9)) * 100.0D);
    int var8 = (int)(var9 - (double)var7 * 0.01D);
    int var6 = var7;
    if(var7 >= 90) {
      var6 = 99;
    }

    var1 = "";
    if(var5) {
      var1 = "" + var4;
    }

    String var3 = var1 + var8;
    var1 = var3;
    if(var6 > 0) {
      var1 = var3 + "." + var6;
    }

    var3 = var1;
    if(!var5) {
      var3 = var1 + var4;
    }

    return var3.replaceAll("\\.", var2);
  }
}
