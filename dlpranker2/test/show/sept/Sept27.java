package show.sept;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.Lists;

import show.sept.Entry.KernelTransform;
import show.sept.Entry.KernelType;
import utils.CSV;

public class Sept27 {
	
	public static final String NL = System.getProperty("line.separator");

	//public static final String[] METRICS = new String[] { "MAE", "RMSE", "Spearman" };
	public static final String[] METRICS = new String[] { "MAE", "RMSE" };
	
	public static final String HEADER = "\\begin{table}[tb]" + NL +
			"\\caption{write comment here}" + NL +
			"\\begin{center}" + NL +
			// metodo (Fanizzi+Gaussian) + Transform (Linear, Gaussian, Polynomial), MAE, RMSE, Spearman
			// "\\footnotesize\\begin{tabular}{|c|c|c|c|}";
			"\\footnotesize\\begin{tabular}{|c|c|c|}";

	public static final String FOOTER = "\\end{tabular}" + NL +
			"\\end{center}" + NL +
			"\\clabel{tab:customLabel}" + NL +
			"\\end{table}";
	
	public static void main(String[] args) throws Exception {
		
		if (args.length < 1)
			throw new IllegalArgumentException("Syntax: script <csv> [signature]");
		
		String scsv = args[0];
		
		String methodSignature = (args.length < 2 ? null : args[1]);
		
		if (methodSignature == null && scsv.contains("AllAtomicConcept"))
			methodSignature = "(AC)";
		
		if (methodSignature == null && scsv.contains("FilmSubClasses"))
			methodSignature = "(FSC)";
		
		if (methodSignature == null)
			methodSignature = "";
		
		InputStream inputStream = new FileInputStream(scsv);
		InputStreamReader reader = new InputStreamReader(inputStream);
		Collection<String[]> csv = CSV.read(reader);
		
		List<Entry> entries = Lists.newLinkedList();
		
		int c = 0;
		
		for (String[] _csv : csv) {
			//System.out.println(Arrays.asList(_csv));
			
			if (c++ == 0)
				continue;
			
			String user = _csv[0];
			int nratings = Integer.parseInt(_csv[1]);
			String method = _csv[2];
			
			int nfeatures = Integer.parseInt(_csv[4]);
			KernelType type = (nfeatures >= 0 ? KernelType.Fanizzi : KernelType.Loesch);
			
			double linearAccuracy = Double.parseDouble(_csv[6]);
			double linearMAE = Double.parseDouble(_csv[7]);
			double linearRMSE = Double.parseDouble(_csv[8]);
			double linearSpearman = Double.parseDouble(_csv[9]);
			
			Entry le = new Entry(user, nratings, method, nfeatures, type, KernelTransform.Linear,
					linearAccuracy, linearMAE, linearRMSE, linearSpearman);
			
			entries.add(le);
			
			double gaussianAccuracy = Double.parseDouble(_csv[10]);
			double gaussianMAE = Double.parseDouble(_csv[11]);
			double gaussianRMSE = Double.parseDouble(_csv[12]);
			double gaussianSpearman = Double.parseDouble(_csv[13]);
			
			Entry ge = new Entry(user, nratings, method, nfeatures, type, KernelTransform.Gaussian,
					gaussianAccuracy, gaussianMAE, gaussianRMSE, gaussianSpearman);
			
			entries.add(ge);
			
			double polynomialAccuracy = Double.parseDouble(_csv[10]);
			double polynomialMAE = Double.parseDouble(_csv[11]);
			double polynomialRMSE = Double.parseDouble(_csv[12]);
			double polynomialSpearman = Double.parseDouble(_csv[13]);
			
			Entry pe = new Entry(user, nratings, method, nfeatures, type, KernelTransform.Polynomial,
					polynomialAccuracy, polynomialMAE, polynomialRMSE, polynomialSpearman);
			
			entries.add(pe);
		}
		
		KernelType type = null;
		
		DescriptiveStatistics lstatsAccuracy = new DescriptiveStatistics(),
				lstatsMAE = new DescriptiveStatistics(),
				lstatsRMSE = new DescriptiveStatistics(),
				lstatsSpearman = new DescriptiveStatistics(),
				
				gstatsAccuracy = new DescriptiveStatistics(),
				gstatsMAE = new DescriptiveStatistics(),
				gstatsRMSE = new DescriptiveStatistics(),
				gstatsSpearman = new DescriptiveStatistics(),
				
				pstatsAccuracy = new DescriptiveStatistics(),
				pstatsMAE = new DescriptiveStatistics(),
				pstatsRMSE = new DescriptiveStatistics(),
				pstatsSpearman = new DescriptiveStatistics();
		
		for (Entry entry : entries) {
			
			if (type == null)
				type = entry.type;
			else if (!type.equals(entry.type))
				throw new IllegalStateException("Mismatching Kernel types");
			
			DescriptiveStatistics accuracy = null, mae = null, rmse = null, spearman = null;
			
			switch (entry.transform) {
			case Linear: {
				accuracy = lstatsAccuracy;
				mae = lstatsMAE;
				rmse = lstatsRMSE;
				spearman = lstatsSpearman;
			} break;
			case Gaussian: {
				accuracy = gstatsAccuracy;
				mae = gstatsMAE;
				rmse = gstatsRMSE;
				spearman = gstatsSpearman;
			} break;
			case Polynomial: {
				accuracy = pstatsAccuracy;
				mae = pstatsMAE;
				rmse = pstatsRMSE;
				spearman = pstatsSpearman;
			} break;
			}
			
			accuracy.addValue(entry.accuracy);
			mae.addValue(entry.mae);
			rmse.addValue(entry.rmse);
			
			// Problema: a volte Spearman è NaN o Infinite
			if (!(Double.isNaN(entry.spearman) || Double.isInfinite(entry.spearman))) {
				//System.out.println(entry.spearman);
				spearman.addValue(entry.spearman);
			}
		}
		
		System.out.println(HEADER);
		System.out.println("\\hline");
		
		for (String metric : METRICS) {
			System.out.print(" & " + metric);
		}
		System.out.println("\\\\");
		System.out.println("\\hline \\hline");

		DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.ENGLISH);
		NumberFormat f = new DecimalFormat("#.##", s);
		
		// each transform is on a new row
		for (KernelTransform transform : KernelTransform.values()) {
			System.out.print(type.toString() + "(" + methodSignature + ")"
					+ (transform == KernelTransform.Linear ? "" : "+" + transform.toString()));
			
			DescriptiveStatistics accuracy = null, mae = null, rmse = null, spearman = null;
			
			switch (transform) {
			case Linear: {
				accuracy = lstatsAccuracy;
				mae = lstatsMAE;
				rmse = lstatsRMSE;
				spearman = lstatsSpearman;
			} break;
			case Gaussian: {
				accuracy = gstatsAccuracy;
				mae = gstatsMAE;
				rmse = gstatsRMSE;
				spearman = gstatsSpearman;
			} break;
			case Polynomial: {
				accuracy = pstatsAccuracy;
				mae = pstatsMAE;
				rmse = pstatsRMSE;
				spearman = pstatsSpearman;
			} break;
			}
			
			for (String metric : METRICS) {
				DescriptiveStatistics stats = null;
				
				if (metric.equals("ACCURACY")) {
					stats = accuracy;
				} else if (metric.equals("MAE")) {
					stats = mae;
				} else if (metric.equals("RMSE")) {
					stats = rmse;
				} else if (metric.equals("Spearman")) {
					stats = spearman;
				} else {
					throw new IllegalStateException("Something went wrong.");
				}
				
				String mean = f.format(stats.getMean());
				String devstd = f.format(stats.getStandardDeviation());
				System.out.print("\t& $" + mean + " \\pm " + devstd + "$");				
			}	
			
			System.out.println("\\\\");
		}
		
		System.out.println("\\hline");
		System.out.println(FOOTER);
	}

}
