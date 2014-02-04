package uk.ac.cam.darknet.common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerFactory {
	
	private static Logger logger;
	
	public static Logger getLogger() {
		if(logger == null) {
			
			logger = Logger.getLogger("DarkLogger");
			logger.setLevel(Level.ALL);
			
			FileHandler fh;
			
			try{
				fh = new FileHandler("log/main.log", true);
				fh.setFormatter(new SimpleFormatter());
				
				logger.addHandler(fh);
				
			} catch(IOException e) {
				System.err.println("IOException opening log file! " + e.getMessage());
			}
			
		}
		return logger;
	}
}
