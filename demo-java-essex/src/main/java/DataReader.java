

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class DataReader {
//	private static final String FILE = "C:\\Users\\luis_\\OneDrive - University of Essex\\essex\\intro-computing\\assignment1\\xaa";
	private static final String FILE = "/Users/luistorres/Library/CloudStorage/OneDrive-UniversityofEssex/essex/intro-computing/assignment1/US_Accidents_March23_sampled_500k.csv";
	public static void main(String[] args) {
		final int BATCH_SIZE = 10000;
		List<String[]> batch = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				FILE))) {
			String line;
			boolean first = true;
			while ((line = reader.readLine()) != null) {

				if (first) {
					first = false;
				} else {
					batch.add(line.split(","));
					if (batch.size() >= BATCH_SIZE) {
						processBatch(batch);
						batch.clear();
					}

				}
			}
			if (!batch.isEmpty()) {
				processBatch(batch);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void processBatch(List<String[]> batch) {
//	    int parallelism = ForkJoinPool.commonPool().getParallelism();
//	    System.out.println("Parallelism: " + parallelism);
		System.out.println(batch.parallelStream()
				.map(line ->  {
					String strLine=null;
					try {
						strLine = line[20];
					} catch (Exception e) {
						e.printStackTrace();
						strLine="";
					}
	                 return strLine;
	             })// temperature
				.filter(line -> !line.isEmpty() )
				.mapToDouble(Double::parseDouble)
				.reduce(0, (sum, i) -> sum + i));
//			    .average()
//			    .orElse(0.0));
				
	}

}
