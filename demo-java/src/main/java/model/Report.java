package model;

public class Report {
	/**
	 * position of the column to analyze
	 */
	int columnPosition;
	/**
	 * operation to be applied to specified column. MEAN = 1 SUM = 2 MIN = 3
	 */
	int operationType;

	/**
	 * dataset location
	 */

	String datasetLocation;
	/**
	 * outcome after calculation
	 */
	Double outcome;

	public Report(int columnPosition, int operationType, String datasetLocation, Double outcome) {
		super();
		this.columnPosition = columnPosition;
		this.operationType = operationType;
		this.datasetLocation = datasetLocation;
		this.outcome = outcome;
	}

	public int getColumnPosition() {
		return columnPosition;
	}

	public void setColumnPosition(int columnPosition) {
		this.columnPosition = columnPosition;
	}

	public int getOperationType() {
		return operationType;
	}

	public String getOperationTypeDesc() {
		if (operationType == 1)
			return "MEAN";
		return "";
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public String getDatasetLocation() {
		return datasetLocation;
	}

	public void setDatasetLocation(String datasetLocation) {
		this.datasetLocation = datasetLocation;
	}

	public Double getOutcome() {
		return outcome;
	}

	public void setOutcome(Double outcome) {
		this.outcome = outcome;
	}

	@Override
	public String toString() {
		return "Report [columnPosition=" + columnPosition + ", operationType=" + operationType + ", "
				+ (datasetLocation != null ? "datasetLocation=" + datasetLocation + ", " : "")
				+ (outcome != null ? "outcome=" + outcome : "") + "]";
	}
	

}
