import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Station {
		
	public static final String[] weatherString = {"Rainy", "Windy", "Sunny"};
	public static final String[] energySourceString = {"Electricity", "Wind", "Solar"};
	public static final int[] chargingEnergyUnits = {10, 3, 5};

	private String name;
	private int weather;
	private int numOfChargingSlots;
	private List<CarOwner> queue;
	private CarOwner[] currentlyCharging;
	private int currentEnergyUnit;
	private int energyCapacity;
	
	public Station(String name, int weather, int numOfChargingSlots) {
		this.name = name;
		this.weather = weather;
		this.numOfChargingSlots = numOfChargingSlots;
		this.currentlyCharging = new CarOwner[numOfChargingSlots];
		queue = new ArrayList<CarOwner>();
		energyCapacity = 300;
		Random r = new Random();
		currentEnergyUnit = r.nextInt(50)+100;
		
		System.out.println(name + " is created with " + numOfChargingSlots + " charging slots. Energy Status: " + currentEnergyUnit + "/" + energyCapacity);
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getWeather() {
		return weather;
	}
	
	public void setWeather(int weather) {
		if(weather == this.weather)
			return;
		System.out.print("   @@@@@ " + name + ": Weather is changed from " + weatherString[this.weather] + " to ");
		this.weather = weather;
		System.out.println(weatherString[this.weather] + " @@@@@   ");
	}
	
	public CarOwner[] getCurrentlyCharging() {
		return this.currentlyCharging;
	}
	
	// Get the number of available charging slots in the station
	public int getAvailableSlots() {
		int availableSlots = 0;
		
		for (int i = 0; i < currentlyCharging.length; i++) {			
			if(currentlyCharging[i] == null) {			
				availableSlots++;
			}
		}
		return availableSlots;
	}
	
	// Calculating queue time based on the cars that are already charging in the slots and the cars that are in the queue
	public int calculateQueueTime() {
		if(getAvailableSlots() != 0) {
			return 0;
		}
		
		int time = 0;
		
		int[] timers = new int[numOfChargingSlots];
		for (int i = 0; i < currentlyCharging.length; i++) {			
			timers[i] = currentlyCharging[i].getRequiredEnergyToFullyCharge();
		}
		
		int lowestValue = timers[0];
		for (int i = 1; i < timers.length; i++) {
			if(timers[i] < lowestValue) {
				lowestValue = timers[i];
			}
		}
		
		if(queue.isEmpty()) {
			return lowestValue;
		}
				
		for (int k = 0; k < queue.size(); k++) {
			time += lowestValue;
			
			for (int i = 0; i < timers.length; i++) {
				timers[i] -= lowestValue;
				if(timers[i] == 0 && !queue.isEmpty()) {
					timers[i] = queue.get(k).getRequiredEnergyToFullyCharge();
					
				}
			}
			
			lowestValue = timers[0];
			for (int i = 1; i < timers.length; i++) {
				if(timers[i] < lowestValue) {
					lowestValue = timers[i];
				}
			}
		}
		
		time += lowestValue;
		
		return time;
				
	}
	
	// New car arrived to the station
	public boolean addNewCustomer(CarOwner user) {
		System.out.println("\tNew Car Arrived to " + name + ": " + user.getCarOwner() + " " + user.getCurrentEnergy() + "/" + user.getEnergyCapacity());
		
		for (int i = 0; i < currentlyCharging.length; i++) {
			if(currentlyCharging[i] == null) {
				currentlyCharging[i] = user;
				System.out.println("\t\t" + user.getCarOwner() + " took charging slot #" + (i+1));
				return true;
			}
		}
		
		int queueTime = calculateQueueTime();
		
		// If the waiting time is over 45 secs, car will leave
		if(queueTime > 45) {
			System.out.println("\t\t Current Queue Time is over 45 (" + queueTime + "). " + user.getCarOwner() + " left.");
			return false;
		}
		queue.add(user);
		System.out.println("\t\t" + user.getCarOwner() + " is in the queue (Pos:" + (queue.size()-1) + " - Est Waiting Time: " + queueTime + ")");
		return true;
	}
	
	// Generating energy to the station
	public void generateEnergy(int num) {
		synchronized (this) {
			String weatherString = Station.weatherString[weather];			
			System.out.print(name + ": Energy Generated (" + weatherString + "): " + currentEnergyUnit + "+" + num + "=");
			currentEnergyUnit += num;
			if (currentEnergyUnit > energyCapacity)
				currentEnergyUnit = energyCapacity;
			System.out.println(currentEnergyUnit);
		}
		
	}
	
	// Transferring energy to the vehicles that are in charging slots
	public void transferEnergy(int num) {
		synchronized (this) {
			System.out.print(name + ": Energy Transferred (" + (numOfChargingSlots-getAvailableSlots()) + " Vehicles): " + currentEnergyUnit + "-" + num + "=");
			currentEnergyUnit -= num;
			System.out.println(currentEnergyUnit);
			if(currentEnergyUnit*100 / energyCapacity < 20) {
				System.out.println("\tCurrent Energy is below 20%.");
			}			
		}		
	}
	
	// Checking if any car is fully charged
	public void chargingCheck() {
		for (int i = 0; i < currentlyCharging.length; i++) {
			if(currentlyCharging[i] != null && currentlyCharging[i].getCurrentEnergy() == currentlyCharging[i].getEnergyCapacity()) {
				System.out.println(currentlyCharging[i].getCarOwner() + " is fully charged! and left the station");
				currentlyCharging[i] = null;
				if(queue.size() != 0) {
					currentlyCharging[i] = queue.get(0);
					System.out.println(currentlyCharging[i].getCarOwner() + " started charging at the slot #" + (i+1));
					queue.remove(0);
				}
			}
		}
	}
	
	public int getCurrentEnergyUnit() {
		return this.currentEnergyUnit;
	}
	
	public int getEnergyCapacity() {
		return this.energyCapacity;
	}
	
	public int getNumOfChargingSlots() {
		return this.numOfChargingSlots;
	}
	
	public List<CarOwner> getQueue(){
		return this.queue;
	}
}
