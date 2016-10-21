package io.landysh.inflor.java.core.plots;

import java.util.UUID;

import org.jfree.chart.JFreeChart;

import io.landysh.inflor.java.core.dataStructures.FCSDimension;

public abstract class AbstractFCChart {

	/**
	 * @Param newUUID creates a new UUID for this plot definition.
	 */

	public final String uuid;
	protected JFreeChart chart;
	protected ChartSpec spec;

	public AbstractFCChart(String priorUUID, ChartSpec spec) {
		// Create new UUID if needed.
		if (priorUUID == null) {
			uuid = UUID.randomUUID().toString();
		} else {
			uuid = priorUUID;
		}
		
		this.spec = spec;
		
	}
	
	public abstract void update(ChartSpec spec);
	public abstract JFreeChart createChart(FCSDimension domainDimension, FCSDimension rangeDimension);

}
