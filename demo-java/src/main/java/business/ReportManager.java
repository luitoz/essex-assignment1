package business;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import model.Report;

public class ReportManager {
	int batchSize = 10000;

	public void configureReport(Report report) {
		// report should be saved to database here
		System.out.println("Report configuration succeeded: "+ report);
	}

	public Report generateReport(Report report) {
//		int columnIndex = 20;
		int columnIndex = report.getColumnPosition(); // zero-based index of the target column

		InputStream zipInputStream = readZipFile(report.getDatasetLocation());

		if (report.getOperationType() == 1) {// calculate mean
			Instant start = Instant.now();
			System.out.println("Init processing at " + start);
			double mean = computeMeanInParallel(zipInputStream, columnIndex, batchSize);
			Instant end = Instant.now();
			System.out.println("Finish processing at " + end);
			Duration duration = Duration.between(start, end);
			System.out.println("Duration: " + duration.toMillis() + " ms");
			report.setOutcome(mean);
			showReport(report);
		} else {
			throw new RuntimeException("not implemented");
		}
		return report;

	}

	private void showReport(Report report) {
		System.out.println("\n--- Report Details ---");
		System.out.println("Column position: " + report.getColumnPosition());
		System.out.println("Operation type: " + report.getOperationTypeDesc());
		System.out.println("Dataset location: " + report.getDatasetLocation());
		System.out.println("Outcome: " + report.getOutcome());
		System.out.println("--------");
	}

	private InputStream readZipFile(String location) {
		InputStream is = null;
		try {
			// 1Locate the ZIP in the classpath
			URL zipUrl = this.getClass().getClassLoader().getResource(location);
			if (zipUrl == null) {
				throw new RuntimeException("dataset not found in classpath");
			}
			// close resource afterward
			ZipFile zipFile = new ZipFile(new File(zipUrl.toURI()));
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				System.out.println("Entry Name: " + entry.getName());
				if (!entry.isDirectory()) {
					is = zipFile.getInputStream(entry);
				}
			}
		} catch (Exception e) {
//			System.err.println("Error generating report: " + e.getMessage());
			//chain exceptions to find the exception root
			throw new RuntimeException(e);
		}
		return is;
	}

	private double computeMeanInParallel(InputStream inputStream, int colIndex, int batchSize) {
		ExecutorService executor = Executors.newWorkStealingPool(); // uses all available cores
		List<Future<double[]>> futures = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String header = reader.readLine(); // skip header
			List<String[]> batch = new ArrayList<>();

			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",", -1); // handle empty strings
				batch.add(fields);

				if (batch.size() == batchSize) {
					List<String[]> batchCopy = new ArrayList<>(batch);
					futures.add(executor.submit(() -> processBatch(batchCopy, colIndex)));
					batch.clear();
				}
			}

			if (!batch.isEmpty()) {
				futures.add(executor.submit(() -> processBatch(batch, colIndex)));
			}
		} catch (Exception e) {
			System.err.println("Error generating report: " + e.getMessage());
			throw new RuntimeException(e);
		} finally {
//			System.out.println("closed zipfile resource");
			try {
				inputStream.close();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}

		double totalSum = 0.0;
		long totalCount = 0;

		for (Future<double[]> future : futures) {
			double[] result;
			try {
				result = future.get();
			} catch (Exception e) {
				System.err.println("Error generating report: " + e.getMessage());
				throw new RuntimeException(e);
			}
			totalSum += result[0];
			totalCount += (long) result[1];
		}

		executor.shutdown();

		return totalCount == 0 ? 0.0 : totalSum / totalCount;
	}

	private double[] processBatch(List<String[]> batch, int colIndex) {
//		System.out.println("init processBatch " + Thread.currentThread().getId());
		double sum = 0.0;
		int count = 0;

		for (String[] row : batch) {
			if (colIndex < row.length) {
				String value = row[colIndex];
				if (value != null && !value.trim().isEmpty()) {
					try {
						sum += Double.parseDouble(value.trim());
						count++;
					} catch (NumberFormatException e) {
						throw new RuntimeException(e);
									
					}
				}
			}
		}
//		System.out.println("end processBatch " + Thread.currentThread().getId());
		return new double[] { sum, count };
	}
}
