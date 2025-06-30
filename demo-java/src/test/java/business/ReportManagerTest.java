package business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Report;

public class ReportManagerTest {
	ReportManager reportManager;
	
    @BeforeEach
    void setUp() {
    	 reportManager = new ReportManager();
    }
    @Test
    void testReport() {
    	System.out.println("init testReport");
		for(int i=0; i<10;i++){
			Report report = new Report(20, 1, "data/xaa.zip", null);
			reportManager.configureReport(report);
			reportManager.generateReport(report);
		}
		System.out.println("finish testReport");
    }
    @Test
    void testReportWrongDatasetLocation() {
    	System.out.println("init testReport2");
    	Report report = new Report(-1, 1, "dataset", null);
		reportManager.configureReport(report);
    	Exception exception = assertThrows(Exception.class, () -> reportManager.generateReport(report));
    	assertEquals("java.lang.RuntimeException: dataset not found in classpath", exception.getMessage());
    	System.out.println("finish testReport");
    }
    
    @Test
    void testReportWrongOperationType() {
    	Report report = new Report(-1, -1, "data/xaa.zip", null);
		reportManager.configureReport(report);
    	Exception exception = assertThrows(Exception.class, () -> reportManager.generateReport(report));
    	assertEquals("not implemented", exception.getMessage());
    }
    
    @Test
    void testReportWrongColumnNumber() {
    	Report report = new Report(-1, 1, "data/xaa.zip", null);
		reportManager.configureReport(report);
    	Exception exception = assertThrows(Exception.class, () -> reportManager.generateReport(report));
    	assertEquals("java.util.concurrent.ExecutionException: java.lang.ArrayIndexOutOfBoundsException", exception.getMessage());
    }
    
    @Test
    void testReportWrongColumnDataType() {
    	Report report = new Report(0, 1, "data/xaa.zip", null);
		reportManager.configureReport(report);
    	Exception exception = assertThrows(Exception.class, () -> reportManager.generateReport(report));
    	assertEquals("java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.lang.RuntimeException: java.lang.NumberFormatException: For input string: \"A-2047758\"", exception.getMessage());
    }

}
