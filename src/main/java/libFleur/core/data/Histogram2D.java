package libFleur.core.data;

import java.util.BitSet;

import libFleur.core.utils.FCSUtilities;

public class Histogram2D {

  double[][] mask;
  double[][] histogram;
  private double xBinWidth;
  private double yBinWidth;
  private double[] zValues;
  private double[] yBins;
  private double[] xBins;
  private int yBinCount;
  private int xBinCount;
  private double xMin;
  private double yMin;

  public Histogram2D(double[] xData, double xMin, double xMax, double[] yData, double yMin,
      double yMax) {
    
    this.xMin = xMin;
    this.yMin = yMin;

    this.xBinCount = FCSUtilities.DEFAULT_BIN_COUNT;
    this.yBinCount = FCSUtilities.DEFAULT_BIN_COUNT;
    // histogram contains resulting XYZ dataset
    xBins = new double[xBinCount * yBinCount];
    yBins = new double[xBinCount * yBinCount];
    zValues = new double[xBinCount * yBinCount];

    // Mask is the masked raw data to be used for gating.
    mask = new double[2][xData.length];
    xBinWidth = (xMax - xMin) / xBinCount;
    yBinWidth = (yMax - yMin) / yBinCount;

    initializeHistogram(xBinCount, xBinWidth, xMin, yBinCount, yBinWidth, yMin);
    populateHistogram(zValues, xData, yData, xBinWidth, yBinWidth, yBinCount);

  }

  public double[] populateHistogram(double[] zValues,
      double[] xData, double[] yData, double xBinWidth, double yBinWidth, int yBinCount) {
    for (int i = 0; i < xData.length; i++) {
      double x = xData[i];
      double y = yData[i];
      int xBin = (int) ((x-xMin)/ xBinWidth);
      int yBin = (int) ((y-yMin)/ yBinWidth);
      int histogramIndex = yBinCount * xBin + yBin;
      if (histogramIndex < 0) {
        histogramIndex = 0;
      } else if (histogramIndex > zValues.length - 1) {
        histogramIndex = zValues.length - 1;
      }
      zValues[histogramIndex]++;
      mask[0][i] = (double) xBin * xBinWidth;

    }
    return zValues;
  }

  public void initializeHistogram(int xBinCount, double xBinWidth, double xMin, int yBinCount,
      double yBinWidth, double yMin) {
    for (int i = 0; i < xBinCount; i++) {
      double xBinLeftEdge = i * xBinWidth + xMin;
      for (int j = 0; j < yBinCount; j++) {
        double yBinLeftEdge = j * yBinWidth + yMin;
        int currentRowIndex = i * yBinCount + j;
        xBins[currentRowIndex] = xBinLeftEdge;
        yBins[currentRowIndex] = yBinLeftEdge;
        zValues[currentRowIndex] = 0;
      }
    }
  }

  public double getYBinWidth() {
    return yBinWidth;
  }

  public double getXBinWidth() {
    return xBinWidth;
  }

  public double[] getXBins() {
    return xBins;
  }

  public double[] getYBins() {
    return yBins;
  }

  public double[] getZValues() {
    return zValues;
  }

  public BitSet getNonEmptyBins() {
    BitSet bits = new BitSet(zValues.length);
    for (int i = 0; i < zValues.length; i++) {
      if (zValues[i] >= Double.MIN_NORMAL || zValues[i] < -Double.MIN_NORMAL) {
        bits.set(i);
      }
    }
    return bits;
  }
}
