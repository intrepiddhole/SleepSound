package ipnossoft.rma;

abstract class RelaxPropertyReader {
  RelaxPropertyReader() {
  }

  public abstract String buildConfigBuildType();

  public abstract String buildConfigFlavor();

  public abstract String relaxConfiguration();
}
