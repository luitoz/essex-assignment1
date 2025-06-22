
# from business.login_manager import LoginManager
from model.model import Report
from business.business import ReportManager

def main():
    # Authorize user
    # login_manager = LoginManager()
    # login_manager.handle_login()

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
            location = input("\nEnter dataset location in classpath: ").strip()
            print()
            report = Report(position, operation_type, location, None)
            report_manager.configure_report(report)

        elif option == "2":
            print("")
            try:
                report = report_manager.generate_report(report)
            except Exception:
                # Do nothing
                pass

        elif option == "3":
            print("\nExiting... Goodbye!")
            break

        else:
            print("\nInvalid option. Try again.")

if __name__ == "__main__":
    main()
