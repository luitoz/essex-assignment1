import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CSVParallelMean {
	private static int batchNumber = 0;
	// TODO use url from code 1-3 in collab containing file id
	private static final String FILE = "C:\\Users\\luis_\\OneDrive - University of Essex\\essex\\intro-computing\\assignment1\\US_Accidents_March23_sampled_500k.zip";

	public static void main(String[] args) {
		int columnIndex = 20; // zero-based index of the target column
		int batchSize = 30000;
		double mean = 0;
		zipfileex();
		/*InputStream zipInputStream = readZipFile();
		try {
			mean = computeMeanInParallel(zipInputStream, columnIndex, batchSize);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Mean of column at index " + columnIndex + ": " + mean);
		*/
	}
	
	static void testUrl() {
		String urlString = "https://github.com/luitoz/my-first-binder/blob/main/README.md"; // Replace with your file URL
//		String urlString = "https://drive.usercontent.google.com/download?id=1U3u8QYzLjnEaSurtZfSAS_oh9AT2Mn8X&export=download&authuser=0";
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	 public static void zipfileex() {
	        String zipUrl = "https://raw.githubusercontent.com/luitoz/my-first-binder/main/xaa.zip"; // Replace with your ZIP URL
	        String targetFileName = "xaa"; // File inside ZIP
	        try {
	            // Step 1: Download ZIP to temp file
	            File tempZip = File.createTempFile("download", ".zip");
	            try (InputStream in = new URL(zipUrl).openStream();
	                 OutputStream out = new FileOutputStream(tempZip)) {
	                in.transferTo(out);
	            }
	            
	            System.out.println("File downloaded to: " + tempZip.getAbsolutePath());

	            // Step 2: Use ZipFile to read the downloaded ZIP
	            try (ZipFile zipFile = new ZipFile(tempZip)) {
	                Enumeration<? extends ZipEntry> entries = zipFile.entries();
	                while (entries.hasMoreElements()) {
	                    ZipEntry entry = entries.nextElement();
	                    System.out.println("Found: " + entry.getName());
	                    if (entry.getName().equalsIgnoreCase(targetFileName)) {
	                        System.out.println("Reading: " + targetFileName);
	                        try (BufferedReader reader = new BufferedReader(
	                                new InputStreamReader(zipFile.getInputStream(entry)))) {
	                            String line;
	                            while ((line = reader.readLine()) != null) {
	                                System.out.println(line); // process as needed
	                            }
	                        }
	                    }
	                }
	            }

	            // Optionally delete the temp file
	            tempZip.deleteOnExit();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	public static InputStream readZipFile() {
		InputStream is = null;
		try {
			//close resource afterward
			ZipFile zipFile = new ZipFile(new File(FILE));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.println("Entry Name: " + entry.getName());
                if (!entry.isDirectory()) {
                    is = zipFile.getInputStream(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
	}

	public static double computeMeanInParallel(InputStream inputStream, int colIndex, int batchSize)
			throws Exception {
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
		}
		finally {
			System.out.println("closed zipfile resource");
			inputStream.close();
		}

		double totalSum = 0.0;
		long totalCount = 0;

		for (Future<double[]> future : futures) {
			double[] result = future.get(); // [sum, count]
			totalSum += result[0];
			totalCount += (long) result[1];
		}

		executor.shutdown();

		return totalCount == 0 ? 0.0 : totalSum / totalCount;
	}

	private static double[] processBatch(List<String[]> batch, int colIndex) {
		System.out.println("init processBatch " + batchNumber);
		double sum = 0.0;
		int count = 0;

		for (String[] row : batch) {
			if (colIndex < row.length) {
				String value = row[colIndex];
				if (value != null && !value.trim().isEmpty()) {
					try {
						sum += Double.parseDouble(value.trim());
						count++;
					} catch (NumberFormatException ignored) {
					}
				}
			}
		}
		System.out.println("end processBatch" + batchNumber++);
		return new double[] { sum, count };
	}
}
