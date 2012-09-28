package show.sept;

public class Entry {

	public enum KernelMethod { Online };
	public enum KernelType { Fanizzi, Loesch };
	public enum KernelTransform { Linear, Gaussian, Polynomial };
	
	// learning characteristics
	public String user;
	public int nratings;
	public KernelMethod method;
	
	// method used
	public int nfeatures;
	public KernelType type;
	public KernelTransform transform;
	
	// performance
	public double accuracy;
	public double mae;
	public double rmse;
	public double spearman;
	
	public Entry(String user, int nratings, String smethod,
			int nfeatures, KernelType type, KernelTransform transform,
			double accuracy, double mae, double rmse, double spearman) {
		
		if ("SIMPLE_ONLINE".equals(smethod)) {
			this.method = KernelMethod.Online;
		}
		this.user = user;
		this.nratings = nratings;
		this.nfeatures = nfeatures;
		
		this.type = type;
		this.transform = transform;
		
		this.accuracy = accuracy;
		this.mae = mae;
		this.rmse = rmse;
		this.spearman = spearman;
	}

	@Override
	public String toString() {
		return "Entry [user=" + user + ", nratings=" + nratings + ", method=" + method + ", nfeatures=" + nfeatures + ", type=" + type + ", transform=" + transform + ", accuracy=" + accuracy + ", mae=" + mae + ", rmse=" + rmse + ", spearman=" + spearman
				+ "]";
	}	
}
