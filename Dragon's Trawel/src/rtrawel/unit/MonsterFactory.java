package rtrawel.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.items.ArmorFactory;
import rtrawel.items.Item;
import rtrawel.items.WeaponFactory;
import rtrawel.unit.RUnit.RaceType;

public class MonsterFactory {
	private static HashMap<String,MonsterData> data = new HashMap<String, MonsterData>();
	public static void init(){
		data.put("wolf pup",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 3;
			}

			@Override
			public int getKnowledge() {
				return 0;
			}

			@Override
			public int getMaxHp() {
				return 9;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 10;
			}

			@Override
			public int getAgility() {
				return 5;
			}

			@Override
			public int getDexterity() {
				return 5;
			}

			@Override
			public int getResilence() {
				return 2;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				return list;
			}

			@Override
			public String getName() {
				return "wolf pup";
			}

			@Override
			public String getDesc() {
				return "Not quite the fearsome foe, this pup is easily slain.";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int getXp() {
				return 2;
			}

			@Override
			public int getGold() {
				return 3;
			}

			@Override
			public String getDrop() {
				return "canine's canine";
			}

			@Override
			public double getDropChance() {
				return 1.0/32.0;
			}

			@Override
			public String getRareDrop() {
				return "wolf pelt";
			}

			@Override
			public double getRareDropChance() {
				return 1.0/64.0;
			}

			@Override
			public int getKillsTilKnown() {
				return 5;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 10;
			}

			@Override
			public String getWeapon() {
				return "wolf pup teeth";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.BEAST);
			}});
		
		data.put("fearless fella",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 8;
			}

			@Override
			public int getKnowledge() {
				return 0;
			}

			@Override
			public int getMaxHp() {
				return 14;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 2;
			}

			@Override
			public int getAgility() {
				return 0;
			}

			@Override
			public int getDexterity() {
				return 0;
			}

			@Override
			public int getResilence() {
				return 4;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				return list;
			}

			@Override
			public String getName() {
				return "fearless fella";
			}

			@Override
			public String getDesc() {
				return "Look out, he's got a knife!.";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int getXp() {
				return 4;
			}

			@Override
			public int getGold() {
				return 4;
			}

			@Override
			public String getDrop() {
				return "leather hood";
			}

			@Override
			public double getDropChance() {
				return (1.0/32.0);
			}

			@Override
			public String getRareDrop() {
				return "simple stabber";
			}

			@Override
			public double getRareDropChance() {
				return (1.0/64.0);
			}

			@Override
			public int getKillsTilKnown() {
				return 4;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 8;
			}

			@Override
			public String getWeapon() {
				return "fella knife";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.HUMANOID);
			}});
	}
	
	
	
	
	public static MonsterData getMonsterByName(String str) {
		return data.get(str);
	}
}
