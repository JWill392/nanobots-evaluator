package entity;

import static com.google.common.base.Preconditions.checkArgument;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Entity.Type;

public class FoodEntity extends MortalEntity {
	public static final Replay.Entity.Type TYPE = Type.FOOD;
	private int energy;

	FoodEntity(int inEnergy) {
		super();

		setEnergy(inEnergy);
	}

	@Override
	public Type getType() {
		return TYPE;
	}

	@Override
	public int getEnergy() {
		return energy;
	}

	private void setEnergy(int e) {
		data.setEnergy(e);
		energy = e;
	}

	/**
	 * @return amount successfully harvested
	 */
	public int harvest(int amount) {
		checkArgument(amount >= 0);

		if (amount > energy) {
			int amountHarvested = energy;
			setEnergy(0);
			return amountHarvested;
		}

		setEnergy(energy - amount);
		return amount;
	}

	@Override
	public void tick() {
	}

	@Override
	public String toString() {
		return "FoodEntity [energy=" + energy + "]";
	}

}
