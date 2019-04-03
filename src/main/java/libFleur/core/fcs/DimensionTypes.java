/*
 * ------------------------------------------------------------------------ Copyright 2016 by Aaron Hart
 * Email: Aaron.Hart@gmail.com
 * Created on December 14, 2016 by Aaron Hart
 */
package libFleur.core.fcs;

public enum DimensionTypes {
  FORWARD_SCATTER(new String[] {".*fsc.*", ".*fcs.*", ".*forward.*", ".*size.*"}),
  SIDE_SCATTER(new String[] {".*side.*", ".*ssc.*", ".*orth.*"}),
  VIABILITY(new String[] {}), 
  TIME(new String[] {".*time.*"}), 
  DNA(new String[] {".*dapi.*", ".*pi.*"}),
  PULSE_WIDTH(new String[] {"Width"});

  private final String[] regi;

  DimensionTypes(String[] regi) {
    this.regi = regi;
  }

  public boolean matches(String parameterName) {
    for (String regex : this.regi) {
      if (parameterName.toLowerCase().matches(regex)) {
        return true;
      }
    }
    return false;
  }
}
