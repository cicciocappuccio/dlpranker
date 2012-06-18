package it.uniba.di.lacam.fanizzi.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogUtils {
	
	private Logger log;
	private FileHandler hand;
	
	public LogUtils(String fileName) 
	{
		try
		{
			  BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			  System.out.println("Create LogFile: experiment " + (new Date()).toString());
			  File file = new File(fileName + ".log");
			  if(file.exists())
			  {
				  hand = new FileHandler(fileName + ".log", true);
				  log = Logger.getLogger("aman raj");
//				  LogRecord rec = new LogRecord(Level.WARNING,"Do something here!");
				  LogRecord rec = new LogRecord(Level.INFO,"LogFile: experiment" + (new Date()).toString());
/*				  LogRecord rec3 = new LogRecord(Level.SEVERE,"Do something here!");
				  hand.publish(rec1);
				  hand.publish(rec2);*/
				  hand.publish(rec);
				  log.addHandler(hand);
//				  System.out.println("Operation successfully!");
			  }
			  else
			  {
				  System.out.println("File is not exist");
			  }
		}
		catch (IOException e){}
	}

	public void writeWarning(String warning)
	{
		LogRecord rec = new LogRecord(Level.WARNING,warning);
		hand.publish(rec);
		log.addHandler(hand);
	}

	public void writeInfo(String info)
	{
		LogRecord rec = new LogRecord(Level.INFO, info);
		hand.publish(rec);
		log.addHandler(hand);
	}

	public void writeSevere(String severe)
	{
		LogRecord rec = new LogRecord(Level.SEVERE, severe);
		hand.publish(rec);
		log.addHandler(hand);
	}

	public void writeConfig (String config)
	{
		LogRecord rec = new LogRecord(Level.CONFIG, config);
		hand.publish(rec);
		log.addHandler(hand);
	}

	public void writeFine (String fine)
	{
		LogRecord rec = new LogRecord(Level.FINE, fine);
		hand.publish(rec);
		log.addHandler(hand);
	}

	public void writeFiner (String finer)
	{
		LogRecord rec = new LogRecord(Level.FINER, finer);
		hand.publish(rec);
		log.addHandler(hand);
	}

	public void writeFinest (String finest)
	{
		LogRecord rec = new LogRecord(Level.FINEST, finest);
		hand.publish(rec);
		log.addHandler(hand);
	}
	
	public void write (Level livello, String finest)
	{
		LogRecord rec = new LogRecord(livello, finest);
		hand.publish(rec);
		log.addHandler(hand);
	}
}
