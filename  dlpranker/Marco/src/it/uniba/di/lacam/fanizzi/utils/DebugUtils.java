package it.uniba.di.lacam.fanizzi.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DebugUtils {

	private DebugUtils() { }
	
	private static final String NL = System.getProperty("line.separator");
	
    public static String toString(double[] arg) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("[ ");
    	for (double val : arg) {
    		sb.append(val + " ");
    	}
    	sb.append("]");
    	return sb.toString();
    }

    public static String toString(int[] arg) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("[ ");
    	for (double val : arg) {
    		sb.append(val + " ");
    	}
    	sb.append("]");
    	return sb.toString();
    }
    
    public static String toString(Integer[] arg) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("[ ");
    	for (double val : arg) {
    		sb.append(val + " ");
    	}
    	sb.append("]");
    	return sb.toString();
    }
    
	public static String toString(double value) {
		String ret = null;
		if (Double.isNaN(value)) {
			ret = "NaN";
		} else if (Double.isInfinite(value)) {
			ret = "Inf";
		} else {
			DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.ENGLISH);
			s.setDecimalSeparator('.');
			NumberFormat f = new DecimalFormat("#.######", s);
			ret = f.format(value);
		}
		return ret;
	}
	
	public static String toString(List<Object> list) {
		StringBuffer ret = new StringBuffer();
		if (list != null) {
			ret.append("{");
			for (Object obj : list) {
				ret.append(toString(obj) + " ");
			}
			ret.append("}");
		} else {
			ret.append("list:null");
		}
		return ret.toString();
	}
	
	public static String toString(Object obj) {
		StringBuffer ret = new StringBuffer(); 
		if (obj != null) {
			ret.append(obj.getClass().getName() + ":" + obj);
		}
		return ret.toString();
	}

	
}
