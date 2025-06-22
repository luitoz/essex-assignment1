import sys
from business.business import LoginManager
from business.business import ReportManager
from model.model import Report
from security.security import Authenticator

def main():
    # Authorize user
    authenticator = Authenticator(True)
    login_manager = LoginManager(authenticator)
    login_manager.handleLogin()
    # if successful
    report = None
    report_manager = ReportManager()

    while True:
        # Print menu
        print("\n===== Main Menu =====")
        print("1. Configure Report")
        print("2. Generate Report")
        print("3. Exit")
        option = input("Choose an option (1-3): ").strip()

        if option == "1":
            position = int(input("\nEnter position of column to analyze: ").strip())
            operation_type = int(input("\nEnter operation to be applied to specified column. MEAN = 1, SUM = 2, MIN = 3: ").strip())
            location = input("\nEnter dataset location in data folder: ").strip()
            print("")
            report = Report(position, operation_type, location, None)
            report_manager.configure_report(report)

        elif option == "2":
            print("")
            try:
                report = report_manager.generate_report(report)
            except Exception as ex:
                print("Error generating report: ", ex)

        elif option == "3":
            print("\nExiting... Goodbye!")
            break

        else:
            print("\nInvalid option. Try again.")

if __name__ == "__main__":
    main()
