package game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import action.cmd.ActionCmd;
import action.cmd.Attack;
import action.cmd.ContinuePreviousAction;
import action.cmd.Harvest;
import action.cmd.Move;
import action.cmd.Reproduce;
import action.cmd.TargettedAction;
import action.cmd.Transmit;
import action.cmd.Wait;

public class Settings {
	public static Settings inst;

	private int MEMORY_SIZE = 16;

	private int MESSAGE_LENGTH = 16;
	private int MESSAGE_RANGE = 8;

	private int BOT_MAX_ENERGY = 100;
	private int VISION_RAD = 2;
	private int HARVEST_ENERGY = 20;
	private int BOT_NEWBORN_ENERGY = 50;

	private int ATTACK_DAMAGE = 25;

	private Map<Class<? extends TargettedAction>, Integer> ACTION_RANGES;
	private Map<Class<? extends ActionCmd>, Integer> ACTION_COSTS;
	private List<Class<? extends ActionCmd>> ACTION_EXECUTION_ORDER;

	private boolean locked = false;

	/**
	 * Loads default settings
	 */
	public static void load() {
		inst = new Settings();
	}

	/**
	 * Loads settings from file
	 * 
	 * @param file
	 */
	public static void load(File file) {
		// TODO settings.load(file)
		inst = new Settings();
		// load from file

		assert (false);
	}

	private Settings() {
		ACTION_COSTS = new HashMap<Class<? extends ActionCmd>, Integer>(10);
		ACTION_COSTS.put(Attack.class, 10);
		ACTION_COSTS.put(Harvest.class, 0);
		ACTION_COSTS.put(Move.class, 0);
		ACTION_COSTS.put(Reproduce.class, 75);
		ACTION_COSTS.put(Transmit.class, 10);
		ACTION_COSTS.put(Wait.class, 0);
		ACTION_COSTS.put(ContinuePreviousAction.class, 0);

		ACTION_RANGES = new HashMap<Class<? extends TargettedAction>, Integer>(10);
		ACTION_RANGES.put(Attack.class, 2);
		ACTION_RANGES.put(Harvest.class, 1);
		ACTION_RANGES.put(Move.class, 2);
		ACTION_RANGES.put(Reproduce.class, 1);

		ACTION_EXECUTION_ORDER = new ArrayList<Class<? extends ActionCmd>>(10);
		ACTION_EXECUTION_ORDER.add(Attack.class);
		ACTION_EXECUTION_ORDER.add(Move.class);
		ACTION_EXECUTION_ORDER.add(Harvest.class);
		ACTION_EXECUTION_ORDER.add(Transmit.class);
		ACTION_EXECUTION_ORDER.add(Reproduce.class);
		ACTION_EXECUTION_ORDER.add(Wait.class);
		ACTION_EXECUTION_ORDER.add(ContinuePreviousAction.class);
	}

	// ACTION EXECUTION ORDER
	public static List<Class<? extends ActionCmd>> getActionExecutionOrder() {
		return inst.ACTION_EXECUTION_ORDER;
	}

	// ACTION RANGES
	public static void setActionRange(Class<? extends TargettedAction> type,
			Integer range) {
		if (inst.locked)
			return;
		assert (range > 0);

		inst.ACTION_RANGES.put(type, range);
	}

	public static int getActionRange(Class<? extends TargettedAction> type) {
		return inst.ACTION_RANGES.get(type);
	}

	// ACTION COSTS
	public static void setActionCost(Class<? extends ActionCmd> type,
			Integer cost) {
		if (inst.locked)
			return;
		assert (cost > 0);

		inst.ACTION_COSTS.put(type, cost);
	}

	public static int getActionCost(Class<? extends ActionCmd> type) {
		return inst.ACTION_COSTS.get(type);
	}

	public static int getMemorySize() {
		return inst.MEMORY_SIZE;
	}

	public static void setMemorySize(int s) {
		if (inst.locked)
			return;
		inst.MEMORY_SIZE = s;
	}

	public static int getMessageLength() {
		return inst.MESSAGE_LENGTH;
	}

	public static void setMessageLength(int messageLength) {
		if (inst.locked)
			return;
		inst.MESSAGE_LENGTH = messageLength;
	}

	public static int getBotMaxEnergy() {
		return inst.BOT_MAX_ENERGY;
	}

	public static void setBotMaxEnergy(int botMaxEnergy) {
		if (inst.locked)
			return;
		inst.BOT_MAX_ENERGY = botMaxEnergy;
	}

	public static int getMessageRange() {
		return inst.MESSAGE_RANGE;
	}

	public static void setMessageRange(int msgRange) {
		if (inst.locked)
			return;
		inst.MESSAGE_RANGE = msgRange;
	}

	public static int getVisionRadius() {
		return inst.VISION_RAD;
	}

	public static void setVisionRadius(int visRad) {
		if (inst.locked)
			return;
		inst.VISION_RAD = visRad;
	}

	public static int getNewbornEnergy() {
		return inst.BOT_NEWBORN_ENERGY;
	}

	public static void setNewbornEnergy(int newbornEnergy) {
		if (inst.locked)
			return;
		inst.BOT_NEWBORN_ENERGY = newbornEnergy;
	}

	public static int getHarvestEnergy() {
		return inst.HARVEST_ENERGY;
	}

	public static void setHarvestEnergy(int harvestEnergy) {
		if (inst.locked)
			return;
		inst.HARVEST_ENERGY = harvestEnergy;
	}

	public static int getAttackDamage() {
		return inst.ATTACK_DAMAGE;
	}

	public static void setAttackDamage(int attackDamage) {
		if (inst.locked)
			return;
		inst.ATTACK_DAMAGE = attackDamage;
	}

	public static void lock() {
		inst.locked = true;
	}

}
