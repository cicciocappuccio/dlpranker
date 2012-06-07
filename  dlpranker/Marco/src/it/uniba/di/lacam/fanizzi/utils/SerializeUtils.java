package it.uniba.di.lacam.fanizzi.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;

public class SerializeUtils {

	private SerializeUtils() { }
	
	public static String serialize(Object obj) throws Exception {
		XStream xs = new XStream();
		return xs.toXML(obj);
	}
	
	public static Object deserialize(String xml) throws Exception {
		XStream xs = new XStream();
		Object ret = null;
		try {
			ret = xs.fromXML(xml);
		} catch (ConversionException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
}
