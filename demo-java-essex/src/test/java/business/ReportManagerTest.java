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
    	Report report = new Report(20, 1, "data/xaa.zip", null);
		reportManager.configureReport(report);
		reportManager.generateReport(report);
    }
    @Test
    void testReportWrongDatasetLocation() {
    	Report report = new Report(-1, 1, "dataset", null);
		reportManager.configureReport(report);
    	Exception exception = assertThrows(Exception.class, () -> reportManager.generateReport(report));
    	assertEquals("sorry", exception.getMessage());
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
//    	exception.printStackTrace();
//    	reportManager.showReport(report);
    	assertEquals("sorry", exception.getMessage());
    }
    
    @Test
    void testReportWrongColumnDataType() {
    	Report report = new Report(0, 1, "data/xaa.zip", null);
		reportManager.configureReport(report);
    	Exception exception = assertThrows(Exception.class, () -> reportManager.generateReport(report));
//    	exception.printStackTrace();
//    	reportManager.showReport(report);
    	assertEquals("sorry", exception.getMessage());
    }

}
