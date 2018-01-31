package ipnossoft.rma;

public class RelaxFreePropertyReader extends RelaxPropertyReader {
  public RelaxFreePropertyReader() {
  }

  public String buildConfigBuildType() {
    return "release";
  }

  public String buildConfigFlavor() {
    return "google";
  }

  public String relaxConfiguration() {
    return "free";
  }
}
