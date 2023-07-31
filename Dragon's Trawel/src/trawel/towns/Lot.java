package trawel.towns;

import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.fight.Arena;
import trawel.towns.misc.Garden;
import trawel.towns.nodes.Mine;
import trawel.towns.nodes.NodeFeature;
import trawel.towns.services.Inn;

public class Lot extends Feature {

	private static final long serialVersionUID = 1L;
	private int tier;
	/**
	 * -1 = can add
	 * -2 = added
	 */
	private double constructTime = -1;
	private String construct;
	public Lot(Town town) {
		this.town = town;
		tier = town.getTier();
		name = "lot";
		tutorialText = "This is a lot you own. \n Go to it to decide what you want to build.";
	}
	
	@Override
	public String getColor() {
		return extra.F_BUILDABLE;
	}

	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|lot|Lot|");
		if (construct == null) {
		int inncost = tier*600;
		int arenacost = tier *400;
		int minecost = tier*1200;
		int gardencost = tier*50;
		extra.println("What do you want to build? You have " +Player.bag.getGold() + " gold.");
		extra.println("1 inn " + inncost + " gold.");
		extra.println("2 arena " + arenacost + " gold.");
		extra.println("3 donate to town");
		extra.println("4 mine " + minecost + " gold.");
		extra.println("5 garden " + gardencost + " gold.");
		extra.println("6 exit");
		
		switch(extra.inInt(6)) {
		case 1: 
			if (Player.bag.getGold() >= inncost) {
			extra.println("Build an inn here?");
			if (extra.yesNo()) {	
			Player.bag.addGold(-inncost);
			construct = "inn";
			constructTime = 24*3;
			name = "inn under construction";
			}}else {
				extra.println("Not enough gold!");
			}
			
			break;
		case 2: 
			extra.println("Build an arena here?");
			if (Player.bag.getGold() >= arenacost) {
			if (extra.yesNo()) {
			Player.bag.addGold(-arenacost);
			construct = "arena";
			constructTime = 24*2;
			name = "arena under construction";
			}}else {
				extra.println("Not enough gold!");
			}break;
		case 3: 
			extra.println("Donate to the town?");
			if (extra.yesNo()) {
				town.replaceFeature(this,new TravelingFeature(this.town));
			}break;
		case 4: 
			if (Player.bag.getGold() >= minecost) {
			extra.println("Build a mine?");
			if (extra.yesNo()) {
			Player.bag.addGold(-minecost);
			construct = "mine";
			constructTime = 24*7;
			name = "mine under construction";
			}}else {
				extra.println("Not enough gold!");
			}break;
		case 5:
			if (Player.bag.getGold() >= gardencost) {
				extra.println("Build a garden?");
				if (extra.yesNo()) {
				Player.bag.addGold(-gardencost);
				construct = "garden";
				constructTime = 24;
				name = "garden under construction";
				}}else {
					extra.println("Not enough gold!");
				}break;
		case 6: return;
		}
		if (construct != null) {
			tutorialText = "Your " + construct + " is under construction.";
		}
		}else {
			extra.println("Your " + construct + " is being built.");
		}
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (constructTime >= 0) {
			constructTime-=time;
		if (construct != null && constructTime <= 0) {
			Feature add = null;
			switch (construct) {//TODO: enquene add might be better off in time events
			case "inn": add = (new Inn("your inn (" + town.getName() + ")",tier,town,Player.player));break;
			case "arena":add = (new Arena("your arena (" + town.getName() + ")",tier,1,24,200,1,Player.player));break;
			case "mine": add = (new Mine("your mine (" + town.getName() + ")",town,Player.player,NodeFeature.Shape.NONE));break;
			case "garden": add = (new Garden(town));
			}
			town.enqueneReplace(this,add);
			constructTime = -2;
		}
		}
		return null;
	}
	
	public double getConstructTime() {
		return constructTime;
	}

}
