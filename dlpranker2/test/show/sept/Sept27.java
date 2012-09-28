package show.sept;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.Lists;

import show.sept.Entry.KernelTransform;
import show.sept.Entry.KernelType;
import utils.CSV;

public class Sept27 {
	
	public static final String NL = System.getProperty("line.separator");

	public static final String HEADER = "\\begin{table}[tb]" + NL +
			"\\caption{write comment here}" + NL +
			"\\begin{center}" + NL +
			// metodo (Fanizzi+Gaussian), MAE, RMSE, Spearman
			"\\footnotesize\\begin{tabular}{|c|c|c|c|}";

	public static final String FOOTER = "\\end{tabular}" + NL +
			"\\end{center}" + NL +
			"\\clabel{tab:customLabel}" + NL +
			"\\end{table}";
	
	public static void main(String[] args) throws Exception {
		String scsv = args[0];
		
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
		
		DescriptiveStatistics lstats = new DescriptiveStatistics(),
				gstats = new DescriptiveStatistics(), pstats = new DescriptiveStatistics();
		
		for (Entry entry : entries) {
			switch (entry.transform) {
			
			}
			System.out.println(entry);
		}
		
	}

}
