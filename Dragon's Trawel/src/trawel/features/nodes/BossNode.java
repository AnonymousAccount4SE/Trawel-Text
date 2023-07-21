package trawel.features.nodes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import trawel.DrawBane;
import trawel.Networking;
import trawel.Person;
import trawel.Player;
import trawel.RaceFactory;
import trawel.extra;
import trawel.mainGame;

public class BossNode extends NodeConnector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Person> people;
	private int state = 0;
	private int type;
	public BossNode(int tier,int t){
		type = t;
		setConnects(new ArrayList<NodeConnector>());
		level = tier;
		Person p;
		switch (type) {
		case 0:
			name = "The Fatespinner (Boss)";
			interactString = "challenge The Fatespinner";
			people = new ArrayList<Person>();
			people.add(RaceFactory.makeMimic(extra.zeroOut(tier-3)+1));
			people.add(RaceFactory.makeMimic(extra.zeroOut(tier-3)+1));
			p = RaceFactory.getBoss(tier);
			p.setTitle("The Fatespinner");
			p.getBag().getDrawBanes().add(DrawBane.TELESCOPE);
			people.add(p);
		break;
		case 1:
			name = "The Hell Baron (Boss)";
			interactString = "challenge The Hell Baron";
			people = new ArrayList<Person>();
			p = RaceFactory.getBoss(tier);
			p.setTitle("The Baron of Hell");
			p.getBag().getDrawBanes().add(DrawBane.LIVING_FLAME);
			people.add(p);
		break;
		}
	}
	
	private boolean fatespinner() {
		if (state == 0) {
			extra.println(extra.PRE_RED+"You challenge the fatespinner!");
			List<Person> list = people;
			List<Person> survivors = mainGame.HugeBattle(list,Player.list());
			if (survivors.contains(Player.player.getPerson())) {
			forceGo = false;
			interactString = "approach the fatespinner's corpse";
			people = null;
			state = 1;
			name = name + "'s corpse";
			Networking.sendStrong("Achievement|boss1|");
			return false;}else {
				people = survivors;
				return true;
			}
		}else {
			extra.println("Here lies the body of the fatespinner...");
			return false;
		}
		
		
	}
	
	private boolean hellbaron() {
		if (state == 0) {
			extra.println(extra.PRE_RED+"You challenge the hell baron!");
			List<Person> list = people;
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),list.get(0));
			if (winner == Player.player.getPerson()) {
			forceGo = false;
			interactString = "approach the hell baron's corpse";
			people = null;
			state = 1;
			name = name + "'s corpse";
			Networking.sendStrong("Achievement|boss2|");
			return false;}else {
				return true;
			}
		}else {
			extra.println("Here lies the body of the hell baron...");
			return false;
		}
		
		
	}

	@Override
	protected boolean interact() {
		switch (type) {
		case 0: return fatespinner();
		case 1: return hellbaron();
		}
		throw new RuntimeException("Invalid boss");
	}

	@Override
	protected String shapeName() {
		return "NOPE";
	}
	
	@Override
	protected DrawBane[] dbFinds() {
		return null;
	}

}
