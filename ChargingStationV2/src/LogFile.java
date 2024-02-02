import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogFile {

	public static final String systemLoggerStr = "SystemLogger";
	public static final String chargingStationLoggerStr = "ChargingStationFunctionality";
	public static final String energyManagementLoggerStr = "EnergyManagementFunctionality";
	public static final String weatherloggerStr = "WeatherFunctionality";

	public static final Logger systemLogger = Logger.getLogger(systemLoggerStr);
	public static final Logger chargingStationLogger = Logger.getLogger(chargingStationLoggerStr);
	public static final Logger energyManagementLogger = Logger.getLogger(energyManagementLoggerStr);
	public static final Logger Weatherlogger = Logger.getLogger(weatherloggerStr);

	public static void SystemFunctionalityStartedlog() {
		String text = "System of station changed started.";
		systemLogger.info(text);
		writeFile(text, systemLoggerStr);
	}
	
	public static void SystemFunctionalityEndlog() {
		String text = "System of station changed ended.";
        systemLogger.info(text);
        writeFile(text, systemLoggerStr);
    }
	
	public static void StationCreated(Station station) {
		String text = "STATION CREATED: " 
				+ station.getName() 
				+ " - " + "CurrentEnergyStatus: " + station.getCurrentEnergyUnit() + "/" + station.getEnergyCapacity() 
				+ " - " + "ChargingSlots: " + station.getNumOfChargingSlots()
				+ " - " + "CurrentEnergySource: " + Station.weatherString[station.getWeather()];
		systemLogger.info(text);
        writeFile(text, chargingStationLoggerStr);
	}
	
	public static void StationEnergySourceSwitched(Station station, int oldWeatherInfo) {
		String text = "STATION ENERGY SOURCE CHANGED: "
				+ station.getName()
				+ " - " + "From: " + Station.energySourceString[oldWeatherInfo]
				+ " - " + "To: " + Station.energySourceString[station.getWeather()];
		systemLogger.info(text);
        writeFile(text, chargingStationLoggerStr);
	}
	
	public static void StationGeneratedEnergy(Station station, int generated) {
		String text = "ENERGY GENERATED: "
				+ station.getName()
				+ " - " + "From: " + (station.getCurrentEnergyUnit() - generated)
				+ " - " + "To: " + station.getCurrentEnergyUnit();
		systemLogger.info(text);
        writeFile(text, energyManagementLoggerStr);
	}
	
	public static void StationTransferredEnergy(Station station, int transferred) {
		String text = "ENERGY TRANSFERRED: "
				+ station.getName()
				+ " - " + "From: " + (station.getCurrentEnergyUnit() + transferred)
				+ " - " + "To: " + station.getCurrentEnergyUnit();
		CarOwner[] arr = station.getCurrentlyCharging();
		for (int i = 0; i < arr.length; i++) {
			text += " - " + "Slot #" + (i+1) + " ";
			if(arr[i] != null) {
				text += arr[i].getCarOwner() + " (" + arr[i].getCurrentEnergy() + "/" + arr[i].getEnergyCapacity() + ")";
			}else {
				text += "None";
			}

		}
		systemLogger.info(text);
        writeFile(text, energyManagementLoggerStr);
	}
	
	public static void StationChargingSlotsAndQueue(Station station) {
		String text = "STATION STATUS: "
				+ station.getName()
				+ " - " + "CurrentEnergyStatus: " + station.getCurrentEnergyUnit() + "/" + station.getEnergyCapacity();
		CarOwner[] arr = station.getCurrentlyCharging();
		for (int i = 0; i < arr.length; i++) {
			text += " - " + "Slot #" + (i+1) + " ";
			if(arr[i] != null) {
				text += arr[i].getCarOwner() + " (" + arr[i].getCurrentEnergy() + "/" + arr[i].getEnergyCapacity() + ")";
			}else {
				text += "None";
			}

		}
		int counter = 1;
		for (CarOwner carOwner : station.getQueue()) {
			text += " - Queue #" + counter + " ";
			text += carOwner.getCarOwner() + " (" + carOwner.getCurrentEnergy() + "/" + carOwner.getEnergyCapacity() + ")";
			counter++;
		}
		systemLogger.info(text);
        writeFile(text, chargingStationLoggerStr);
	}
	
	public static void WeatherChange(Station station, int oldWeatherInfo) {
		String text = "WEATHER CHANGED: "
				+ station.getName()
				+ " - " + "From: " + Station.weatherString[oldWeatherInfo]
				+ " - " + "To: " + Station.weatherString[station.getWeather()];
		systemLogger.info(text);
        writeFile(text, weatherloggerStr);
	}

	public static void writeFile(String content, String fileName) {

		try {

			LocalDateTime currentDateTime = LocalDateTime.now();

			DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String formattedDate = currentDateTime.format(dateformatter);

			File file = new File(fileName + "-" + formattedDate + ".txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			// Define a custom date and time format (optional)
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			// Format the current date and time using the specified format
			String formattedDateTime = currentDateTime.format(formatter);

			// Open the file for writing (true flag for append mode)
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
				writer.write(formattedDateTime + "    ");
				writer.write(content);
				writer.newLine();
				writer.close();
			}
			
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setupLogger(Logger logger, String logFileName) {
        try {
        	
        	File logFile = new File(logFileName + ".log");
            boolean append = logFile.exists();
            
            
            FileHandler fileHandler = new FileHandler(logFileName + ".log", append);


            // Set the log format
            fileHandler.setFormatter(new SimpleFormatter());

            // Add the handler to the logger
            logger.addHandler(fileHandler);

            // Set the logging level (e.g., INFO, WARNING, SEVERE)
            logger.setLevel(Level.INFO);

        } catch (IOException e) {
            logger.log(Level.INFO, "Error setting up logger.", e);
        }
    }

}
