package inflor.knime.nodes.downsample;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import inflor.core.downsample.DownSampleMethods;

public class DownsampleNodeSettings {

  private static final String DEFAULT_SELECTED_COLUMN = "none";
  private static final String KEY_SELECTED_COLUMN = "Column Name";
  private String mSelectedColumn = DEFAULT_SELECTED_COLUMN;

  private static final DownSampleMethods DEFAULT_SELECTED_METHOD = DownSampleMethods.RANDOM;
  private static final String KEY_SELECTED_METHOD = "Method";
  private DownSampleMethods mSelectedMethod = DEFAULT_SELECTED_METHOD;

  private static final boolean DEFAULT_USE_RANDOM_SEED = true;
  private static final String KEY_USE_RANDOM_SEED = "Use random seed";
  private boolean mUseRandomSeed = DEFAULT_USE_RANDOM_SEED;

  private static final Integer DEFAULT_RANDOM_SEED = 42;
  private static final String KEY_RANDOM_SEED = "Random seed";
  private Integer mRandomSeed = DEFAULT_RANDOM_SEED;

  private static final Integer DEFAULT_CEILING = 2000;
  private static final String KEY_CEILING = "Ceiling (max events per frame)";
  private Integer mCeiling = DEFAULT_CEILING;

  private static final String[] DEFAULT_DIMENSION_NAMES = new String[] {};
  private static final String KEY_DIMENSION_NAMES = "Dimension Names";
  private String[] mDimensionNames = DEFAULT_DIMENSION_NAMES;

  public void setSelectedColumn(String selectedItem) {
    mSelectedColumn = selectedItem;
  }

  public String getSelectedColumn() {
    return mSelectedColumn;
  }

  public void save(NodeSettingsWO settings) {
    settings.addString(KEY_SELECTED_COLUMN, mSelectedColumn);
    settings.addString(KEY_SELECTED_METHOD, mSelectedMethod.toString());
    settings.addBoolean(KEY_USE_RANDOM_SEED, mUseRandomSeed);
    settings.addInt(KEY_RANDOM_SEED, mRandomSeed);
    settings.addInt(KEY_CEILING, mCeiling);
    settings.addStringArray(KEY_DIMENSION_NAMES, mDimensionNames);
  }

  public void load(NodeSettingsRO settings) throws InvalidSettingsException {
    mSelectedColumn = settings.getString(KEY_SELECTED_COLUMN);
    String sms = settings.getString(KEY_SELECTED_METHOD);
    mSelectedMethod = DownSampleMethods.valueOf(DownSampleMethods.class, sms);// TODO
    mUseRandomSeed = settings.getBoolean(KEY_USE_RANDOM_SEED);
    mRandomSeed = settings.getInt(KEY_RANDOM_SEED);
    mCeiling = settings.getInt(KEY_CEILING);
    mDimensionNames = settings.getStringArray(KEY_DIMENSION_NAMES);
  }

  public DownSampleMethods getSampleMethod() {
    return mSelectedMethod;
  }

  public void setSelectedDownsampleMethod(DownSampleMethods newMethod) {
    mSelectedMethod = newMethod;
  }

  public boolean isRandomSeed() {
    return mUseRandomSeed;
  }

  public void setUseRandomSeed(boolean mUseRandomSeed) {
    this.mUseRandomSeed = mUseRandomSeed;
  }

  public void setRandomSeed(Integer value) {
    mRandomSeed = value;
  }

  public Number getRandomSeed() {
    return mRandomSeed;
  }

  public Integer getCeiling() {
    return mCeiling;
  }

  public void setDimensionNames(String[] array) {
    mDimensionNames = array;
  }

  public String[] getDimensionNames() {
    return mDimensionNames;
  }
}
