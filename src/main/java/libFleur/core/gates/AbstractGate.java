/*
 * ------------------------------------------------------------------------
 *  Copyright 2016 by Aaron Hart
 *  Email: Aaron.Hart@gmail.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 * ---------------------------------------------------------------------
 *
 * Created on December 14, 2016 by Aaron Hart
 */
package libFleur.core.gates;

import java.util.BitSet;
import java.util.HashMap;

import libFleur.core.data.DomainObject;
import libFleur.core.data.FCSFrame;
import org.w3c.dom.Element;

import libFleur.core.proto.FCSFrameProto.Message.Subset.Type;
import libFleur.core.transforms.TransformSet;

@SuppressWarnings("serial")
public abstract class AbstractGate extends DomainObject implements Hierarchical {
  

  protected String parentID;
  protected HashMap<String, String> customInfo;

  
  public AbstractGate(String priorUUID) {
    super(priorUUID);
  }

  public AbstractGate() {
    this(null);
  }

  /**
   * An abstract class that provides the basis for all gating-ml compliant gates.
   * 
   * @see RangeGate
   */

  /**
   * 
   * @param data - The input data. Must contain entries for all of this.getDimensionNames().
   * @return A boolean array of rowCount.length where true corresponds to being in the gate
   */
  public abstract BitSet evaluate(FCSFrame data, TransformSet transforms);

  public String getInfo(String name) {
    return customInfo.get(name);
  }

  public String setInfo(String key, String value) {
    return customInfo.put(key, value);
  }

  /**
   * Generate a gating-ML 2.0 compliant XML Element for this gate.
   * 
   * @return an org.w3c.dom Element.
   */
  public abstract Element toXMLElement();

  /**
   * Override to validate the gate definition. If gate is invalid, throw an exception.
   * 
   * @throws IllegalStateException
   */
  public abstract void validate();
  public abstract String getDomainAxisName();
  public abstract String getRangeAxisName();
  public abstract String getLabel();
  public abstract Type getType();
  public abstract String[] getDimensions();
  public abstract Double[] getDescriptors();
  
  @Override
  public String getParentID(){
    return parentID;
  }
  
  @Override
  public void setParentID(String newValue){
    parentID = newValue;
  }
}
