package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.FightFactory;

public class VillageFactory {

	public static List<Village> villages = new ArrayList<Village>();
	
	public static Village init() {
		Village homa = new Village("homa");
		villages.add(homa);
		homa.conts.add(new Inn(5));
		homa.addFight(FightFactory.getFightByName("homa_pup1"));
		homa.addFight(FightFactory.getFightByName("homa_fella1"));
		Village unun = new Village("unun");
		villages.add(unun);
		unun.conts.add(new Inn(10));
		new Connection(homa,unun,null);
		return villages.get(0);
	}
}
