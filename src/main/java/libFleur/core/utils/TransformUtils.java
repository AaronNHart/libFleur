package libFleur.core.utils;

import libFleur.core.transforms.TransformType;
import libFleur.core.transforms.AbstractTransform;
import libFleur.core.transforms.LogicleTransform;
import libFleur.core.transforms.BoundDisplayTransform;
import libFleur.core.transforms.LogrithmicTransform;
import libFleur.core.fcs.DimensionTypes;


public class TransformUtils {

    public static AbstractTransform createDefaultTransform(TransformType selectedType) {

        AbstractTransform newTransform = null;

        if (selectedType == TransformType.LINEAR || selectedType == TransformType.BOUNDARY) {
            newTransform = new BoundDisplayTransform(Double.MAX_VALUE, Double.MAX_VALUE);
        } else if (selectedType == TransformType.LOGARITHMIC) {
            newTransform = new LogrithmicTransform(1, 1000000);
        } else if (selectedType == TransformType.LOGICLE) {
            newTransform = new LogicleTransform();
        } else {
            // noop
        }
        return newTransform;

    }

    public static AbstractTransform createDefaultTransform(String parameterName) {
        if (DimensionTypes.DNA.matches(parameterName)
                || DimensionTypes.FORWARD_SCATTER.matches(parameterName)
                || DimensionTypes.SIDE_SCATTER.matches(parameterName)
                || DimensionTypes.TIME.matches(parameterName)
                || FCSUtilities.MERGE_DIMENSION_NAME.equals(parameterName)){
            return new BoundDisplayTransform(0, 262144);

        } else if (DimensionTypes.PULSE_WIDTH.matches(parameterName)) {
            return new BoundDisplayTransform(0, 100);
        } else {
            return new LogicleTransform();
        }
    }
}
