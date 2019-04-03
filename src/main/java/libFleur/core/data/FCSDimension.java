package libFleur.core.data;

// Default serialization not used. We should measure performance.
@SuppressWarnings("serial")
public class FCSDimension extends DomainObject implements Comparable <FCSDimension> {

  // eg. the n in PnN
  private int parameterIndex;

  // $PnE Amplification type
  private double ampTypef1;
  private double ampTypef2;

  // $PnN Short name
  private String shortName;
  // $PnS Stain name
  private String stainName;

  // $PnR Range
  private double range;

  private double[] data;

  public FCSDimension(int size, int index, String pnn, String pns, double pneF1, double pneF2,
      double pnr) {
    this(null, size, index, pnn, pns, pneF1, pneF2, pnr);
  }

  public FCSDimension(String priorUUID, int size, int index, String pnn, String pns, double pneF1,
      double pneF2, double pnr) {
    super(priorUUID);
    parameterIndex = index;
    shortName = pnn;
    stainName = pns;
    ampTypef1 = pneF1;
    ampTypef2 = pneF2;
    range = pnr;
    this.data = new double[size];
  }

  @Override
  public int compareTo(FCSDimension other) {
    int result = 0;
    if (this.parameterIndex < other.parameterIndex) {
      result -= 1;
    } else if (this.parameterIndex > other.parameterIndex) {
      result += 1;
    } else {
      result = 0;
    }
    return result;
  }
  
  public double[] getData() {
    return data;
  }

  public String getDisplayName() {
    if (stainName != null && stainName.trim().length() != 0) {
      return stainName + ": "+ shortName;
    } else {
      return shortName;
    }
  }

  public int getIndex() {
    return this.parameterIndex;
  }

  public double getPNEF1() {
    return this.ampTypef1;
  }

  public double getPNEF2() {
    return this.ampTypef2;
  }
  
  public double getRange() {
    return this.range;
  }

  public String getShortName() {
    return this.shortName;
  }

  public int size() {
    return this.data.length;
  }

  public String getStainName() {
    if (stainName!=null){
      return stainName;
    } else {
      return "";
    }
  }

  public void setData(double[] newData) {
    this.data = newData;
  }

  @Override
  public String toString() {
    return this.getDisplayName();
  }

  public void setShortName(String newValue) {
    this.shortName = newValue;    
  }
}
