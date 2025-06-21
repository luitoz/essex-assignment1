import java.util.Scanner;

import business.LoginManager;
import business.ReportManager;
import model.Report;

public class InteractiveMenu {

	public static void main(String[] args) {
		// authorize user
		LoginManager loginManager = new LoginManager();
    	loginManager.handleLogin();
		// if successful continues
		Scanner scanner = new Scanner(System.in);
		int option;
		Report report = null;
		ReportManager reportManager = new ReportManager();

		do {
			// Print menu
			System.out.println("\n===== Main Menu =====");
			System.out.println("1. Configure Report");
			System.out.println("2. Generate Report");
			System.out.println("3. Exit");
			System.out.print("Choose an option (1-3): ");

			// Read user input
			option = scanner.nextInt();
			scanner.nextLine();

			// Perform action
			switch (option) {
			case 1:
				System.out.print("\nEnter position of column to analize: ");
				int position = scanner.nextInt();
				scanner.nextLine();

				System.out.print("\nEnter operation to be applied to specified column. MEAN = 1 SUM = 2 MIN = 3 ");
				int operationType = scanner.nextInt();
				scanner.nextLine();
				System.out.print("\nEnter dataset location in classpath: ");
				String location = scanner.nextLine();
				System.out.println();
				report = new Report(position, operationType, location, null);
				reportManager.configureReport(report);
				break;

			case 2:
				System.out.println("");
				report = reportManager.generateReport(report);
				System.out.println("\n--- Report Details ---");
				System.out.println("Column position: " + report.getColumnPosition());
				System.out.println("Operation type: " + report.getOperationTypeDesc());
				System.out.println("Dataset location: " + report.getDatasetLocation());
				System.out.println("Outcome: " + report.getOutcome());
				break;

			case 3:
				System.out.println("\nExiting... Goodbye!");
				break;

			default:
				System.out.println("\nInvalid option. Try again.");
			}

		} while (option != 3);

		scanner.close();
	}
}
