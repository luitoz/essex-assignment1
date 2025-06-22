import csv
import zipfile
import os
import tempfile
import sys
from concurrent.futures import ProcessPoolExecutor
from typing import List, Tuple
from model.model import Report
from pathlib import Path
from datetime import datetime

class ReportManager:
    def configure_report(self, report: Report):
        print("Report configuration succeeded")
    def generate_report(self, report: Report):
        extractedDataset = self.read_zip_file(report.dataset_location)
        batch_size = 10000
        
        if report.operation_type == 1:  # MEAN
            start = datetime.now()
            print(f"Init processing at {start}")
            mean = self.compute_mean_parallel_by_index(extractedDataset, report.column_position, batch_size)
            end = datetime.now()
            print(f"Finish processing at {end}")
            duration = end - start
            print(f"Duration: {duration.total_seconds() * 1000:.2f} ms")
            report.outcome = mean
            self.show_report(report)
        else:
            sys.stderr.write("not implemented")

    def show_report(self, report):
        """Print the details of a Report object."""
        print("\n--- Report Details ---")
        print(f"Column position: {report.column_position}")
        print(f"Operation type: {report.operation_type_desc}")
        print(f"Dataset location: {report.dataset_location}")
        print(f"Outcome: {report.outcome}")
        print("--------")

    def read_zip_file(self, location: str) -> str:
        """
        Reads the first available file from a zip archive, 
        extracts it to a temporary directory, and returns its path.
        """
        # Get the directory of the current module
        module_dir = Path(__file__).parent

        # Go one level up and point to sample.txt
        file_path = module_dir.parent / "data" / location
        if not os.path.exists(file_path):
            raise Exception("dataset not found:" + str(file_path))


        temp_dir = tempfile.mkdtemp()
        with zipfile.ZipFile(file_path, 'r') as z:
            for entry_name in z.namelist():
                print(f"Entry Name: {entry_name}")
                if not entry_name.endswith("/"):  # Not a directory
                    extracted_file = z.extract(entry_name, temp_dir)
                    return extracted_file  # Return the path
        raise Exception("dataset not found:" + str(file_path))

    def process_batch(self, batch: List[List[str]], col_index: int) -> Tuple[float, int]:
        total = 0.0
        count = 0
        for row in batch:
            if col_index < len(row):
                value = row[col_index]
                if value and value.strip() != "":
                    try:
                        total += float(value)
                        count += 1
                    except ValueError:
                        continue  # Skip non-numeric values
        return total, count

    def read_in_batches_by_index(self, file_path: str, col_index: int, batch_size: int = 10000):
        with open(file_path, newline='', encoding='utf-8') as file:
            reader = csv.reader(file)
            header = next(reader, None)  # Skip header
            batch = []
            for row in reader:
                batch.append(row)
                if len(batch) == batch_size:
                    yield batch
                    batch = []
            if batch:
                yield batch

    def compute_mean_parallel_by_index(self, file_path: str, col_index: int, batch_size: int = 10000):
        total_sum = 0.0
        total_count = 0

        with ProcessPoolExecutor() as executor:
            futures = []
            for batch in self.read_in_batches_by_index(file_path, col_index, batch_size):
                futures.append(executor.submit(self.process_batch, batch, col_index))

            for future in futures:
                partial_sum, partial_count = future.result()
                total_sum += partial_sum
                total_count += partial_count

        if total_count == 0:
            raise Exception("No valid numeric data found in column index " + str(col_index) + ".")


        mean = total_sum / total_count
        return mean

import getpass
import time
from security.security import Authenticator

class LoginManager:
    """
    Handles user input and authentication flow.
    Stores user credentials in memory if login is successful.
    """
    def __init__(self, authenticator: Authenticator):
        self.authenticator = authenticator
        self.max_attempts = 3

    def handleLogin(self):
        for attempt in range(1, self.max_attempts + 1):
            print(f"Attempt {attempt} of {self.max_attempts}")
            username = input("\nUsername: ").strip()
            password = getpass.getpass("Password: ").strip()  # Hidden input

            if self.authenticator.login(username, password):
                print(f"Logged in user: {self.authenticator.user.name}")
                return
            else:
                time.sleep(3)

        print("Maximum login attempts exceeded. Exiting.")
        sys.exit()