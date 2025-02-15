package trawel.battle.attacks;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.battle.attacks.TargetFactory.TargetType;
import trawel.battle.attacks.TargetFactory.TypeBody;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;

public class TargetHolder {
	/**
	 * do not use directly, get the map
	 * a list of %'s on how damaged a part is
	 */
	private double[] condition;

	/**
	 * if this is null, then there are no restrictions
	 * otherwise it will be limited to the targettype given
	 * DOLATER: can potentially have targettypes that indicate multiple masks as 'flags'
	 */
	private TargetType config;
	private final TypeBody plan;
	
	public TargetHolder(TypeBody type) {
		plan = type;
		config = null;
		
		condition = new double[plan.getUniqueParts()];
		for (int i = condition.length-1;i >= 0;i--) {
			condition[i] = 1;
		}
	}
	
	public int getVariant() {
		if (config == null) {
			return 0;
		}
		for (int i = 0; i < plan.types.length;i++) {
			if (plan.types[i] == config) {
				return i;
			}
		}
		return 0;
	}
	
	
	/**
	 * the damage % on a part
	 * does not list subparts
	 */
	public double getStatus(int spot) {
		return condition[plan.getMap(spot)];
	}
	
	/**
	 * the damage % on a part
	 * gets the final part in the attach chain
	 */
	public double getRootStatus(int spot) {
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			return getRootStatus(aspot);
		}
		return condition[plan.getMap(spot)];
	}
	
	public int getRootSpot(int spot) {
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			return getRootSpot(aspot);
		}
		return spot;
	}
	
	public String getPartName(int spot) {
		return plan.spotName(spot);
	}
	
	public TargetReturn getTargetReturn(int spot) {
		return plan.getTargetReturn(spot);
	}
	
	public double multStatus(int spot, double mult) {
		double store = condition[plan.getMap(spot)]*mult;
		condition[plan.getMap(spot)] = store;
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			multStatus(aspot,mult);
		}
		return store;
	}
	
	public double addStatus(int spot, double add) {
		double store = condition[plan.getMap(spot)]+add;
		condition[plan.getMap(spot)] = Math.max(0,store);
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			addStatus(aspot,add);
		}
		return store;
	}

	public TargetType getConfig() {
		return config;
	}

	/**
	 * can change mid battle
	 * if this is null, then there are no restrictions
	 */
	public void setConfig(TargetType config) {
		this.config = config;
	}

	public TypeBody getPlan() {
		return plan;
	}
	
	/**
	 * returns the map spot of a target
	 * use getPartName, getTarget and getStatus on this number
	 */
	public TargetReturn randTarget() {
		return plan.randTarget(config);
	}
	
	public void debug_print(boolean full) {
		if (full) {
			List<Integer> seen = new ArrayList<Integer>();
			for (TargetReturn b: plan.rootTargetReturns()) {
				printSpot(b.spot,1);
				if (seen.contains(b.spot)) {
					extra.println("    ...");
				}else {
					seen.add(b.spot);
					List<TargetReturn> children = plan.getDirectChildren(b.spot);
					if (children.size() > 0) {
						debug_printLAYER(children,2);
					}
				}
			}
		}
		extra.print("  >");
		for (int i = 0; i < condition.length;i++) {
			extra.print(extra.format2.format(condition[i])+" ");
		}
		extra.println();
		
	}
	
	private void debug_printLAYER(List<TargetReturn> layer, int num) {
		for (TargetReturn tr: layer) {
			List<TargetReturn> children = plan.getDirectChildren(tr.spot);
			printSpot(tr.spot,num);
			if (children.size() > 0 && num < 20) {
				debug_printLAYER(children,num+2);
			}
		}
	}
	private void printSpot(int spot, int buffer) {
		TargetReturn tr = plan.getTargetReturn(spot);
		extra.println(extra.spaceBuffer(buffer)+
				getPartName(spot) + " " +spot+": " + plan.getMap(spot) + "-" + (tr.tar.passthrough ? "p" : extra.format2.format(getStatus(spot))) + " attach: " + (plan.getAttach(spot) != -1 ? plan.getAttach(spot) : "-")
				);
	}

	/**
	 * will return 0 if no mapping found
	 */
	public double getStatusOnMapping(int mapping) {
		return condition[plan.getSlotByMappingNumber(mapping, getVariant())];
	}

}