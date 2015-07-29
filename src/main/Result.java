package main;

import java.util.Locale;

public class Result {
	
	public long build, work;
	public long hits, iter, size;
	
	public Result() {
		build = work = 0;
		hits = iter = size = 0;
	}
	
	public double ratio() {
		return 1 - (iter - hits) / (double) (size - hits);
	}
	
	public double bms() { return build / 1000000.0; }
	public double wms() { return  work / 1000000.0; }
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(String.format(Locale.US, "Build: %7.2f ms ", bms()));
		str.append(String.format(Locale.US,  "Work: %7.2f ms ", wms()));
		str.append(String.format(Locale.US, "Ratio: %7.4f%%" , ratio() * 100));
		str.append(" Hits: " + hits + "/" + iter + "/" + size);
		return str.toString();
	}
	
}
