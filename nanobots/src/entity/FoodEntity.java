package entity;

import static com.google.common.base.Preconditions.checkArgument;

public class FoodEntity extends MortalEntity {
	private int energy;

	FoodEntity(int inEnergy) {
		energy = inEnergy;
	}

	@Override
	public int getEnergy() {
		return energy;
	}

	/**
	 * @return amount successfully harvested
	 */
	public int harvest(int amount) {
		checkArgument(amount >= 0);

		if (amount > energy) {
			int amountHarvested = energy;
			energy = 0;
			return amountHarvested;
		}

		energy -= amount;
		return amount;
	}

	@Override
	public void tick() {
	}

	@Override
	public String toString() {
		return "Food  ";
	}

}
