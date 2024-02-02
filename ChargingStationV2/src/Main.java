import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends LogFile {
	
	public static void main(String[] args) {
		
		setupLogger(systemLogger, "system");		
		SystemFunctionalityStartedlog();
		
		CarOwner u1 = new CarOwner("user#1"),
				u2 = new CarOwner("user#2"),
				u3 = new CarOwner("user#3"),
				u4 = new CarOwner("user#4"),
				u5 = new CarOwner("user#5"),
				u6 = new CarOwner("user#6"),
				u7 = new CarOwner("user#7"),
				u8 = new CarOwner("user#8");
		
		Station st1 = new Station("Station #1", 0, 2);
		StationCreated(st1);
		Station	st2 = new Station("Station #2", 1, 2);
		StationCreated(st2);		
				
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		
		// 2 Stations are started generating and transferring energy
		scheduler.scheduleAtFixedRate(getRunnableGlobal(st1), 0, 2, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(getRunnableGlobal(st2), 0, 2, TimeUnit.SECONDS);
		
		// 8 Vehicles are scheduled for charging
		scheduler.schedule(getRunnableNewCustomer(st1, u1), 6, TimeUnit.SECONDS);
		scheduler.schedule(getRunnableNewCustomer(st1, u2), 7, TimeUnit.SECONDS);
		scheduler.schedule(getRunnableNewCustomer(st2, u3), 6, TimeUnit.SECONDS);
		scheduler.schedule(getRunnableNewCustomer(st2, u4), 12, TimeUnit.SECONDS);
		scheduler.schedule(getRunnableNewCustomer(st2, u5), 12, TimeUnit.SECONDS);
		scheduler.schedule(getRunnableNewCustomer(st2, u6), 12, TimeUnit.SECONDS);
		scheduler.schedule(getRunnableNewCustomer(st2, u7), 12, TimeUnit.SECONDS);
		scheduler.schedule(getRunnableNewCustomer(st2, u8), 12, TimeUnit.SECONDS);
		
		// Changing weathers for two stations
		scheduler.scheduleAtFixedRate(getRunnableWeatherChange(st1, scheduler), 0, 5, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(getRunnableWeatherChange(st2, scheduler), 1, 7, TimeUnit.SECONDS);
		
		scheduler.schedule(getRunnableShutdown(scheduler), 100, TimeUnit.SECONDS);
		
	}
	
	private static Runnable getRunnableShutdown(ScheduledExecutorService scheduler) {
		return () -> {
			scheduler.shutdown();
			SystemFunctionalityEndlog();
		};
	}
	
	private static Runnable getRunnableWeatherChange(Station station, ScheduledExecutorService scheduler) {
		// Weather changes randomly
		return () -> {
			Random r = new Random();
			int oldWeatherInfo = station.getWeather();
			station.setWeather(r.nextInt(2));
			int getCurrentWeatherInfo = station.getWeather();
			if(oldWeatherInfo != getCurrentWeatherInfo) {
				WeatherChange(station, oldWeatherInfo);
			}
			
		};
	}
	
	private static Runnable getRunnableNewCustomer(Station station, CarOwner carOwner) {
		// New vehicles arrives to the station
		return () -> {			
			if(station.addNewCustomer(carOwner)) {
				StationChargingSlotsAndQueue(station);
			}
		};
	}
	
	private static Runnable getRunnableGlobal(Station station) {
		return () -> {
			// Generating energy for the specific station based on the weather
			int generatedEnergy = Station.chargingEnergyUnits[station.getWeather()];
			station.generateEnergy(generatedEnergy);
			StationGeneratedEnergy(station, generatedEnergy);

			// Transferring energy to the vehicles that are already in the charging slots 
			int transferredEnergy = 0;
			for (int i = 0; i < station.getCurrentlyCharging().length; i++) {
				if(station.getCurrentlyCharging()[i] != null) {
					station.getCurrentlyCharging()[i].transferEnergy();
					transferredEnergy++;
				}
			}
			station.transferEnergy(transferredEnergy);
			StationTransferredEnergy(station, transferredEnergy);
			station.chargingCheck();
			StationChargingSlotsAndQueue(station);
		};
	}
	
}
