package io.landysh.inflor.java.knime.nodes.readFCS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnProperties;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.filestore.FileStoreFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import io.landysh.inflor.java.core.dataStructures.ColumnStore;
import io.landysh.inflor.java.core.fcs.FCSFileReader;
import io.landysh.inflor.java.knime.dataTypes.columnStoreCell.ColumnStoreCell;

/**
 * This is the model implementation of ReadFCSSet.
 * 
 *
 * @author Landysh Co.
 */
public class ReadFCSSetNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(ReadFCSSetNodeModel.class);

	// Folder containing FCS Files.
	static final String CFGKEY_PATH = "Path";
	static final String DEFAULT_PATH = null;
	// Should we compensate?
	static final String CFGKEY_COMPENSATE = "Compensate";

	static final boolean DEFAULT_COMPENSATE = false;
	private final SettingsModelString m_path = new SettingsModelString(CFGKEY_PATH, DEFAULT_PATH);
	private final SettingsModelBoolean m_compensate = new SettingsModelBoolean(CFGKEY_COMPENSATE, DEFAULT_COMPENSATE);

	private FileStoreFactory fileStoreFactory;

	private int currentFileIndex=0;
	private int fileCount;

	/**
	 * Constructor for the node model.
	 */
	protected ReadFCSSetNodeModel() {
		super(0, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		DataTableSpec spec;
		try {
			spec = createSpec();
		} catch (final Exception e) {
			final InvalidSettingsException ise = new InvalidSettingsException(
					"Unable to read headers of 1 or more FCS Files.");
			ise.printStackTrace();
			throw ise;
		}
		return new DataTableSpec[] { spec };
	}

	private HashMap<String, String> createColumnPropertiesContent() throws Exception {
		/**
		 * Creates column properties for an FCS Set by looking all of the
		 * headers and setting shared keyword values.
		 */
		final ArrayList<String> filePaths = getFilePaths(m_path.getStringValue());
		final HashMap<String, String> content = new HashMap<String, String>();
		filePaths.stream().map(path -> FCSFileReader.readHeaderOnly(path))
								  					.forEach(map -> map.entrySet()
										            .forEach(entry -> updateContent(content, entry)));
		return content;
	}

	private void updateContent(HashMap<String, String> content, Entry<String, String> entry){
		if (content.containsKey(entry.getKey())){
			String currentValue = content.get(entry.getKey());
			currentValue = currentValue + "||" + entry.getValue();
		} else {
			content.put(entry.getKey(), entry.getValue());
		}
	}
	
	private DataColumnSpec createFCSColumnSpec() throws Exception {
		final DataColumnSpecCreator creator = new DataColumnSpecCreator("FCS Frame", ColumnStoreCell.TYPE);
		// Create properties
		final HashMap<String, String> content = createColumnPropertiesContent();
		final DataColumnProperties properties = new DataColumnProperties(content);
		creator.setProperties(properties);
		// Create spec
		final DataColumnSpec dcs = creator.createSpec();
		return dcs;
	}

	private DataTableSpec createSpec() throws Exception {
		final DataColumnSpec[] colSpecs = new DataColumnSpec[] { createFCSColumnSpec() };
		final DataTableSpec tableSpec = new DataTableSpec(colSpecs);
		return tableSpec;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		currentFileIndex = 0;
		logger.info("Beginning Execution.");
		fileStoreFactory = FileStoreFactory.createWorkflowFileStoreFactory(exec);
		// Create the output spec and data container.
		final DataTableSpec outSpec = createSpec();
		final BufferedDataContainer container = exec.createDataContainer(outSpec);
		final ArrayList<String> filePaths = getFilePaths(m_path.getStringValue());
		fileCount = filePaths.size();
		exec.checkCanceled();
		filePaths.parallelStream().map(path->FCSFileReader.read(path, m_compensate.getBooleanValue()))
								  .forEach(columnStore -> addRow(columnStore, container, exec));//forEach(dataSet -> tempStore.add(dataSet));
		exec.checkCanceled();

		// once we are done, we close the container and return its table
		container.close();
		final BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	private synchronized void addRow(ColumnStore columnStore, BufferedDataContainer container, ExecutionContext exec) {
		final RowKey key = new RowKey("Row " + currentFileIndex);
		final String fsName = currentFileIndex + "ColumnStore.fs";
		FileStore fileStore;
		try {
			fileStore = fileStoreFactory.createFileStore(fsName);
			final ColumnStoreCell fileCell = new ColumnStoreCell(fileStore, columnStore);
			final DataCell[] cells = new DataCell[] { fileCell };

			final DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);

			// check if the execution monitor was canceled
			exec.setProgress(currentFileIndex / (double) fileCount, "Reading file " + (currentFileIndex + 1));
			currentFileIndex++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> getFilePaths(String dirPath) {
		/**
		 * Returns a list of valid FCS Files from the chose directory.
		 */
		final File folder = new File(dirPath);
		final File[] files = folder.listFiles();
		final ArrayList<String> validFiles = new ArrayList<String>();
		for (final File file : files) {
			final String filePath = file.getAbsolutePath();
			if (FCSFileReader.isValidFCS(filePath) == true) {
				validFiles.add(filePath);
			} else if (file.isDirectory()) {
				System.out.println("Directory " + file.getName());
			}
		}
		return validFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_path.loadSettingsFrom(settings);
		m_compensate.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_path.saveSettingsTo(settings);
		m_compensate.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_path.validateSettings(settings);
		m_compensate.validateSettings(settings);
	}
}
