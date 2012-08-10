package entity;

public class FoodEntity extends Entity implements DynamicEntity, MortalEntity {
	private int energy;

	public FoodEntity(int inEnergy) {
		energy = inEnergy;
	}

	public int getEnergy() {
		return energy;
	}

	public void addEnergy(int energy) {
		this.energy += energy;
	}

	@Override
	public void tick() {
	}

}
