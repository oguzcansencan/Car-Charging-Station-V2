import java.util.Random;

public class CarOwner {

	private String username;
	private int currentEnergy;
	private int energyCapacity;
	
	public CarOwner(String username) {
		this.username = username;
		this.energyCapacity = 40;
		
		Random r = new Random();
		this.currentEnergy = r.nextInt(10)+10;
	}
	
	public void setCurrentEnergy(int energy) {
		this.currentEnergy = energy;
	}
	
	public int getCurrentEnergy() {
		return currentEnergy;
	}
	
	public int getEnergyCapacity() {
		return energyCapacity;
	}
	
	public int getRequiredEnergyToFullyCharge() {
		return energyCapacity - currentEnergy;
	}
	
	public String getCarOwner() {
		return username;
	}
	
	public void transferEnergy() {
		currentEnergy++;
	}
}
