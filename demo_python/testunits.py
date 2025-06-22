import unittest
from business.business import ReportManager
from model.model import Report
from unittest.mock import patch
class ReportManagerTest(unittest.TestCase):
    @patch('sys.stderr.write')
    def test_report_wrong_column_datatype(self, mock_write):
        obj = ReportManager()
        report = Report(0,1,"xaa.zip")
        with self.assertRaisesRegex(Exception, "No valid numeric data found in column index 0."):
            obj.generate_report(report)        

    
    @patch('sys.stderr.write')
    def test_report_wrong_operation_type(self, mock_write):
        obj = ReportManager()
        report = Report(1,2,"xaa.zip")
        obj.generate_report(report)
        mock_write.assert_called_with("not implemented")    

    def test_report_wrong_column_number(self):
        obj = ReportManager()
        report = Report('b',1,"xaa.zip")
        with self.assertRaises(TypeError):
            obj.generate_report(report)
    
    def test_report_wrong_dataset_location(self):
        obj = ReportManager()
        report = Report(20,1,"xaa1.zip")
        with self.assertRaisesRegex(Exception, "dataset not found:*"):
            obj.generate_report(report)    
    
    def test_report_wrong_dataset_location(self):
        obj = ReportManager()
        report = Report(20,1,"xaa.zip")     
        obj.generate_report(report)   


        

if __name__ == '__main__':
    unittest.main()
