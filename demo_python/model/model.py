# model/report.py

class Report:
    """
    Report object representing an operation on a dataset column.
    - column_position: Position of the column to analyze.
    - operation_type: The operation to apply (1 = MEAN, 2 = SUM, 3 = MIN).
    - dataset_location: Path or identifier for the dataset.
    - outcome: Result of the computation.
    """
    def __init__(self, column_position: int, operation_type: int, dataset_location: str, outcome: float = None):
        self._column_position = column_position
        self._operation_type = operation_type
        self._dataset_location = dataset_location
        self._outcome = outcome

    @property
    def column_position(self) -> int:
        """Get or set the position of the column."""
        return self._column_position

    @column_position.setter
    def column_position(self, value: int) -> None:
        self._column_position = value

    @property
    def operation_type(self) -> int:
        """Get or set the operation type (1 = MEAN, 2 = SUM, 3 = MIN)."""
        return self._operation_type

    @operation_type.setter
    def operation_type(self, value: int) -> None:
        self._operation_type = value

    @property
    def dataset_location(self) -> str:
        """Get or set the dataset location."""
        return self._dataset_location

    @dataset_location.setter
    def dataset_location(self, value: str) -> None:
        self._dataset_location = value

    @property
    def outcome(self) -> float:
        """Get or set the outcome of the operation."""
        return self._outcome

    @outcome.setter
    def outcome(self, value: float) -> None:
        self._outcome = value

    @property
    def operation_type_desc(self) -> str:
        """Return a human-readable description of the operation."""
        if self._operation_type == 1:
            return "MEAN"
        elif self._operation_type == 2:
            return "SUM"
        elif self._operation_type == 3:
            return "MIN"
        return ""
