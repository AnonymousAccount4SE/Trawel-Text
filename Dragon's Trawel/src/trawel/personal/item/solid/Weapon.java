package trawel.personal.item.solid;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.nustaq.serialization.annotations.OneOf;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import trawel.Services;
import trawel.WorldGen;
import trawel.extra;
import trawel.battle.Combat;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.battle.attacks.TargetFactory.TargetType;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.item.magic.Enchant.Type;

/**
 * 
 * @author dragon
 * before 2/11/2018
 * A Weapon is an Item.
 * It is made up of a certain material, and of a certain type. It my also have an Enchantment of some sort.
 * Different materials have different properties, different types have different attacks, which
 * are stored in the weapon's stance elsewhere.
 */

public class Weapon extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//instance variables
	private int weight = 1;//how much does it weigh?
	private float baseEnchant;//how good is the material at receiving enchantments?
	private Enchant enchant;
	//private boolean IsEnchantedConstant = false;
	//private EnchantHit enchantHit;
	//private boolean IsEnchantedHit = false;//likely unused
	//private Stance martialStance = new Stance(); //what attacks the weapon can use
	
	
	private String weapName;
	@OneOf({"iron","tin","copper","bronze","steel","silver","gold"})
	private String material;
	private int cost;
	//private int level;
	//private int effectiveCost;
	private int kills;
	
	//private Material mat;
	//private transient DamTuple dam;
	private double highDam, avgDam, bScore;
	
	public List<WeaponQual> qualList = new ArrayList<WeaponQual>();
	
	public enum WeaponQual{
		DESTRUCTIVE("Destructive","Destroys some armor on hit."),
		PENETRATIVE("Penetrative","Ignores some local armor."),
		PINPOINT("Pinpoint","Ignores some global armor."),
		RELIABLE("Reliable","Deals damage even when blocked by armor, equal to the weapon level."), 
		DUELING("Dueling","In large fights, attack the same opponent repeatedly."),
		WEIGHTED("Weighted","Less accurate attacks deal more damage."),
		REFINED("Refined","Upon dealing damage, deals bonus damage equal to weapon level."),
		ACCURATE("Accurate","Flat accuracy bonus to all attacks."),
		CARRYTHROUGH("Carrythrough","Missing an attack makes your next attack on another target quicker."),
		;
		public String name, desc;
		WeaponQual(String name,String desc) {
			this.name = name;
			this.desc = desc;
		}
	}
	
	//constructors
	/**
	 * Standard weapon constructor. Makes a weapon of level level
	 * @param newLevel (int)
	 */
	public Weapon(int newLevel, Material materia, String weaponName) {
		
		//material = (String)bpmFunctions.choose("iron",bpmFunctions.choose("steel","silver",bpmFunctions.choose("gold","platinum",bpmFunctions.choose("adamantine","mithril"))));
		//mat = materia;
		material = materia.name;
		level = newLevel;
		baseEnchant = materia.baseEnchant;
		weight *= materia.weight;
		cost = (int) materia.cost;
		//choosing the type of weapon
		weapName = weaponName;
		kills = 0;
		//cost*=level;
		

		switch (weapName) {
		case "longsword":
			cost *= 1;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case "broadsword":
			cost *= 2;
			weight *=3;
			cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case "mace":
			cost *= 2;
			weight *=3;
			cost *= 1+ 0.1 * addQuals(WeaponQual.DESTRUCTIVE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
			;break;
		case "spear":
			cost *= 1;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case "axe":
			cost *= 1;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case "rapier":
			cost *= 2;
			weight *=3;//I think rapiers were heavy? The blunt damage doesn't really reflect this though.
			cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case "dagger":
			cost *= .7;
			weight *=1;
			cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case "claymore":
			cost *= 3;
			weight *=5;
			cost *= 1+ 0.1 * addQuals(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
			;break;
		case "lance":
			cost *= 2;
			weight *=3;
			cost *= 1+ 0.1 * addQuals(WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case "shovel":
			cost *= .8;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
			;break;


	//not normal weapons start
		case "generic teeth":
			cost *= 1;
			weight *=3;
			;break;
		case "standing reaver":
			cost *= 1;
			weight *=3;	
			;break;
		case "generic teeth and claws":
			cost *= 1;
			weight *=3;	
			;break;
		case "branches":
			cost *= 1;
			weight *=3;	
			;break;
		case "generic fists":
			cost *= 1;
			weight *=1;
			;break;
		case "unicorn horn":
			cost *= 3;
			weight *=3;	
			;break;
		case "generic talons":
			cost *= 1;
			weight *=3;	
			;break;
		case "fishing spear":
			cost *= .2f;
			weight *=1;
			;break;
		case "anchor":
			cost *= 1;
			weight *=5;
			;break;
		}
		//random chance, partially based on enchantment power, to enchant the weapon
		//effectiveCost = cost;
		if (baseEnchant > extra.randFloat()*3f) {
			if (extra.chanceIn(2, 3)) {
				enchant = EnchantConstant.makeEnchant(baseEnchant,cost);//removed level, like with armor
				//effectiveCost=(int)extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
				//IsEnchantedConstant = true;
			}else {
				enchant = new EnchantHit(baseEnchant);//no level
				//effectiveCost=(int)extra.zeroOut(cost * enchantHit.getGoldMult());
			}
		}
		
		refreshBattleScore();//no longer lazy loaded
	}
	
	public Weapon(int newLevel) {
		this(newLevel,MaterialFactory.randWeapMat(),(String)extra.choose("longsword","broadsword","mace","spear","axe","rapier","dagger",extra.choose("claymore","lance","shovel")));
	}
	public Weapon(int newLevel, String weaponName) {
		this(newLevel,MaterialFactory.randWeapMat(),weaponName);
	}
	
	/***
	 * used for testing
	 * 
	 */
	public Weapon(boolean useSquid) {
		this(1,useSquid ? MaterialFactory.randWeapMat() : MaterialFactory.randMat(false, true),(String)extra.choose("longsword","broadsword","mace","spear","axe","rapier","dagger",extra.choose("claymore","lance","shovel")));
	}

	//instance methods
	
	public boolean coinLoot() {
		switch (weapName) {
		case "generic teeth": return false;
		case "standing reaver": return false;
		case "generic teeth and claws": return false;
		case "branches": return false;
		case "generic fists": return false;
		case "unicorn horn": return false;//maybe make a drawbane for this
		case "generic talons": return false;
		case "fishing spear": return true;
		case "anchor": return true;
		default: return true;//normal weapons
		}
	}
	
	/**
	 * Returns true if the weapon is enchanted
	 * @return isEnchantedConstant (boolean)
	 */
	public boolean isEnchantedConstant() {
		return enchant != null && enchant.getEnchantType() == Enchant.Type.CONSTANT;
	}
	
	public boolean isEnchantedHit() {
		return enchant != null && enchant.getEnchantType() == Enchant.Type.HIT;
	}
	
	/**
	 * get the reference to the enchantment on the weapon
	 * @return enchant (EnchantConstant)
	 */
	public Enchant getEnchant() {
		return enchant;
	}

	/**
	 * Returns the stance of the weapon.
	 * @return the martialStance (Stance)
	 */
	public Stance getMartialStance() {
		return WeaponAttackFactory.getStance(this.getBaseName());
	}
	
	/**
	 * Returns the full name of the weapon.
	 * @return String
	 */
	public String getName() {
		if (this.isEnchantedConstant()){
			EnchantConstant conste = ((EnchantConstant)enchant);
		return (getModiferName() + " " +conste.getBeforeName() +MaterialFactory.getMat(material).color+ material + "[c_white] " +  weapName + conste.getAfterName());}
		if (this.isEnchantedHit()){
			;
			if (isKeen()) {
				return (getModiferName() + " " + ((EnchantHit)enchant).getName() + MaterialFactory.getMat(material).color+material  + "[c_white] " + weapName);
			}else {
			return (getModiferName() +" "+MaterialFactory.getMat(material).color+ material + "[c_white] " +  weapName + ((EnchantHit)enchant).getName());}
			
		}
			return (getModiferName() + " " +MaterialFactory.getMat(material).color+ material  + "[c_white] " + weapName);
	}
	
	
	/**
	 * Get the weight of the item
	 * @return weight (int)
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * get the cost of the item
	 * @return cost (int)
	 */
	public int getCost() {
		if (this.isEnchantedConstant()) {
			return (int) (level*cost * enchant.getGoldMult()+enchant.getGoldMod());
		}
		if (this.isEnchantedHit()) {
			return (int) (level*cost * enchant.getGoldMult());
		}
		return cost*level;
	}
	
	/**
	 * get the base cost of the item
	 * @return base cost (int)
	 */
	public int getBaseCost() {
		return cost*level;
	}

	
	/**
	 * Swap out the current enchantment for a new one, if a better one is generated.
	 * Returns if a better one was generated or not.
	 * @param level (int)
	 * @return changed enchantment? (boolean)
	 */
	public boolean improveEnchantChance(int level) {
		if (this.isEnchantedConstant()) {
			Enchant pastEnchant = enchant;
			enchant = Services.improveEnchantChance(enchant, level, baseEnchant);
			//effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			return pastEnchant != enchant;
		}else {
			//IsEnchantedConstant = true;
			enchant = EnchantConstant.makeEnchant(baseEnchant,cost);//new EnchantConstant(level*baseEnchant);
			//effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			return true;
		}
	}
	
	/**
	 * @return the baseName (String)
	 */
	public String getBaseName() {
		return weapName;
	}

	/**
	 * Returns the average damage of all the weapon's attacks. Factors in speed and damage, but not aiming.
	 * @return - average (int)
	 */
	/*public double averageDamage() {
		double average = 0;
		int size = this.getMartialStance().getAttackCount();
		int i = 0;
		Attack holdAttack;
		//does not account for aiming, since that is *very* opponent dependent
		while (i < size) {
			holdAttack = this.getMartialStance().getAttack(i);
			average +=holdAttack.getHitmod()*(holdAttack.getBlunt()+holdAttack.getPierce()+holdAttack.getSharp())/holdAttack.getSpeed();
			i++;
		}
		
		
		return (int)(average/size);
	}*/
	
	/**
	 * Returns the damage/time (ie dps) of the most powerful attack the weapon has
	 * @return highest damage (int)
	 */
	public void highestDamage() {
		return;
	}
	
	private void refreshBattleScore() {
		double high = 0;
		double damage = 0;
		double average = 0;
		double bs = 0;
		int size = this.getMartialStance().getAttackCount();
		int i = 0;
		Attack holdAttack;
		//does not account for aiming, since that is *very* opponent dependent
		while (i < size) {
			holdAttack = this.getMartialStance().getAttack(i);
			damage = (holdAttack.getHitmod()*100*holdAttack.getTotalDam(this))/holdAttack.getSpeed()*level;
			average +=damage/size;
			if (damage > high) {
				high = damage;
			}
			for (int t = 0; t < battleTests;t++) {
				for (int j = WorldGen.getDummyInvs().size()-1; j >=0;j--) {
					bs+=Combat.handleTestAttack(holdAttack.impair(10,TargetType.HUMANOID,this),WorldGen.getDummyInvs().get(j),Armor.armorEffectiveness).damage/holdAttack.getSpeed() ;
				}
			}
			i++;
		}
		bs/=(battleTests*WorldGen.getDummyInvs().size());
		this.highDam = high*level;//TODO: unsure if need to do level
		this.avgDam = average*level;
		this.bScore = (bs*level)/(size);
		//dam = new DamTuple(high,average,(bs*level)/(size));
	}
	
	/**
	 * Returns the damage/time (ie dps) of the most powerful attack the weapon has
	 * @return highest damage (int)
	 */
	private void highestDamageThreaded() {
		/*if (dam != null) {
			return dam;
		}*/
		Attack holdAttack;
		double high = 0;
		double damage = 0;
		double average = 0;
		final List<Attack> attacks = this.getMartialStance().giveList();
		int size = attacks.size();
		int i = 0;
		double bs = 0;
		while (i < size) {
			holdAttack = attacks.get(i++);
			damage = (holdAttack.getHitmod()*100*holdAttack.getTotalDam(this))/holdAttack.getSpeed()*level;
			average +=damage;
			if (damage > high) {
				high = damage;
			}
		}
		average/=size;
		List<FutureTask<Double>> runners = new ArrayList<FutureTask<Double>>();
		List<Inventory> tests = new ArrayList<Inventory>();
		TestArmor building = new TestArmor(attacks,tests,this);
		runners.add(new FutureTask<Double>(building));
		for (int p = WorldGen.getDummyInvs().size()-1; p >= 0;p--) {
			if (p != 0 && tests.size() > 3) {
				tests = new ArrayList<Inventory>();
				building = new TestArmor(attacks,tests,this);
				runners.add(new FutureTask<Double>(building));
			}
			building.tests.add(WorldGen.getDummyInvs().get(p));
		}
		//Lock lock = new ReentrantLock();
		
		//synchronized(lock) {
			for (FutureTask<Double> ta: runners) {
				ta.run();
			}
			try {
				for (FutureTask<Double> ta: runners) {
					bs+=ta.get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
		
		bs/=(battleTests*WorldGen.getDummyInvs().size());
		//dam = new DamTuple(high,average,(bs*level)/(size));
		//return dam;
	}
	
	protected class TestArmor implements Callable<Double>{

		private static final long serialVersionUID = 1L;
		public final List<Attack> attacks;
		public final List<Inventory> tests;
		public final Weapon weap;
		public TestArmor(List<Attack> attacks,List<Inventory> tests,Weapon weapon) {
			this.attacks = attacks;
			this.tests = tests;
			weap = weapon;
		}

		@Override
		public Double call() throws RuntimeException {

			double bs = 0;
			int size = attacks.size();
			int i = 0;
			Attack holdAttack;
			//does not account for aiming, since that is *very* opponent dependent
			while (i < size) {
				holdAttack = attacks.get(i++);
				for (int t = 0; t < battleTests;t++) {
					for (int j = tests.size()-1; j >=0;j--) {
						bs+=Combat.handleTestAttack(holdAttack.impair(10,TargetType.HUMANOID,weap),tests.get(j),Armor.armorEffectiveness).damage/holdAttack.getSpeed() ;
					}
				}
			}
			return bs;
		}
		
	}
	
	public static int battleTests = 3;//was 50, then got converted into goes at all the dummy inventories (20 now)
	
	public class DamTuple implements java.io.Serializable{
		
		private static final long serialVersionUID = 1L;
		public final double highest;
		public final double average;
		public final double battleScore;
		public DamTuple(double h, double a, double b) {
			highest = h;
			average = a;
			battleScore = b;
		}
	}
	
	public double highest() {
		return this.highDam;
	}
	
	public double average() {
		return this.avgDam;
	}
	
	public double score() {
		return this.bScore;
	}

	@Override
	public void display(int style,float markup) {
		switch (style) {
		case 0: extra.println(material +" "+weapName+": "+extra.format(this.score()));
		break;
		case 1:
			extra.println(this.getName()
			+ " hd/ad/bs: " + extra.format(this.highest()) + "/" + extra.format(this.average())+"/"+extra.format(this.score())+" value: " + (int)(this.getCost()*markup));
			if (this.isEnchantedConstant()) {
				this.getEnchant().display(1);
			}
			if (this.isEnchantedHit()) {
				this.getEnchant().display(1);
			}
			for (WeaponQual wq: qualList) {
				extra.println(wq.name + ": "+wq.desc);
			}
			;break;
		case 2:
			extra.println(this.getName()
			+ " hd/ad/bs: " + extra.format(this.highest()) + "/" + extra.format(this.average())+ "/"+extra.format(this.score())+" value: " + (int)(this.getCost()*markup) + " kills: " +this.getKills());
			if (this.isEnchantedConstant()) {
				this.getEnchant().display(2);
			}
			if (this.isEnchantedHit()) {
				this.getEnchant().display(2);
			}
			for (WeaponQual wq: qualList) {
				extra.println(wq.name + ": "+wq.desc);
			}
			;break;
		}
	}
	@Override
	public void display(int style) {
		this.display(style, 1);
	}
	


	@Override
	public String getType() {
		return "weapon";
	}

	public String getMaterial() {
		return material;
	}

	public int getKills() {
		return kills;
	}

	public void addKill() {
		this.kills++;
	}

	public Material getMat() {
		return MaterialFactory.getMat(material);
	}
	
	public void levelUp() {
		level++;
		//dam = null;
		refreshBattleScore();
	}
	
	public boolean isKeen() {
		if (this.isEnchantedHit()) {
			return ((EnchantHit)enchant).isKeen();
		}
		return false;
	}
	
	public void deEnchant() {
		enchant = null;
	}

	public void forceEnchantHit(int i) {
		this.enchant = new EnchantHit(true,baseEnchant);
		
	}

	public static double getRarity(String str) {
		switch (str) {
		case "claymore": case " lance": case "shovel":
			return (1/8.0)/3.0;
		default:
			return 1.0/8.0;
		}
	}
	
	public static Weapon genTestWeapon(int useSquid) {
		Weapon[] arr = new Weapon[3];
		boolean useS = useSquid == 1;
		arr[2] = new Weapon(useS);
		arr[1] = new Weapon(useS);
		arr[0] = new Weapon(useS);
		double highest = 0;
		double lowest = 99999;
		for (Weapon w: arr) {
			if (w.score() > highest) {
				highest = w.score();
			}
			if (w.score() < lowest) {
				lowest = w.score();
			}
		}
		for (Weapon w: arr) {
			if (w.score() != highest && w.score()  != lowest) {
				return w;
			}
		}
		return arr[0];
	}
	
	public static Weapon genMidWeapon(int newLevel) {
		Weapon[] arr = new Weapon[3];
		arr[2] = new Weapon(newLevel);
		arr[1] = new Weapon(newLevel);
		arr[0] = new Weapon(newLevel);
		double highest = 0;
		double lowest = 99999;
		for (Weapon w: arr) {
			if (w.score() > highest) {
				highest = w.score();
			}
			if (w.score() < lowest) {
				lowest = w.score();
			}
		}
		for (Weapon w: arr) {
			if (w.score() != highest && w.score()  != lowest) {
				return w;
			}
		}
		return arr[0];
	}
	public static Weapon genMidWeapon(int newLevel,String name) {
		Weapon[] arr = new Weapon[3];
		arr[2] = new Weapon(newLevel,name);
		arr[1] = new Weapon(newLevel,name);
		arr[0] = new Weapon(newLevel,name);
		double highest = 0;
		double lowest = 99999;
		for (Weapon w: arr) {
			if (w.score() > highest) {
				highest = w.score();
			}
			if (w.score() < lowest) {
				lowest = w.score();
			}
		}
		for (Weapon w: arr) {
			if (w.score() != highest && w.score()  != lowest) {
				return w;
			}
		}
		return arr[0];
	}
	
	private int addQuals(WeaponQual ...quals) {
		List<WeaponQual> wqs = Arrays.asList(quals);
		int added = 0;
		for (int i = 0; i < 5;i++) {
			if (added >= 3) {
				return added;
			}
			WeaponQual wq = extra.randList(wqs);
			if (!this.qualList.contains(wq)) {
				qualList.add(wq);
				added++;
			}
		}
		return added;
	}
	
	public static final String[] weaponTypes = new String[]{"longsword","broadsword","mace","spear","axe","rapier","dagger","claymore","lance","shovel"};
	
	public static void rarityMetrics() throws FileNotFoundException {
		final int attempts = 1_000_000;
		PrintWriter writer = new PrintWriter("rmetrics.csv");
		//List<Weapon> weaponList = new ArrayList<Weapon>();
		HashMap<String,Integer> weaponCount = new HashMap<String,Integer>();
		HashMap<String,Integer> materialCount = new HashMap<String,Integer>();
		HashMap<String,Integer> combCount = new HashMap<String,Integer>();
		double battleTotal = 0;
		for (int i = 0; i < attempts;i++) {
			//weaponList.add(Weapon.genMidWeapon(1));
			Weapon weap = genMidWeapon(1);
			weaponCount.put(weap.getBaseName(), weaponCount.getOrDefault(weap.getBaseName(),0)+1);
			materialCount.put(weap.getMaterial(), materialCount.getOrDefault(weap.getMaterial(),0)+1);
			String temp = weap.getMaterial() +weap.getBaseName();
			combCount.put(temp, combCount.getOrDefault(temp,0)+1);
			battleTotal+=weap.score();
			//weaponList.add(weap);
		}
		battleTotal/=attempts;
		extra.println("total score: "+battleTotal);
		writer.write(",");
		for (String str: Weapon.weaponTypes) {
			writer.write(str+",");
			extra.println(str+": "+weaponCount.getOrDefault(str,0));
		}
		writer.write("\n");
		for (Material m: MaterialFactory.matList) {
			if (!m.weapon) {
				continue;
			}
			writer.write(m.name+",");
			extra.println(m.name+": "+materialCount.getOrDefault(m.name,0));
			for (String str: Weapon.weaponTypes) {
				writer.write(combCount.getOrDefault(m.name+str,0)+",");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		extra.println("---");
		
	}
	/***
	 * behold, a horrible performance test
	 * @throws FileNotFoundException
	 */
	public static void duoRarityMetrics() throws FileNotFoundException {
		final int trials = 100;
		final int attempts = 10_000;
		PrintWriter writer1 = new PrintWriter("rmetrics1.csv");
		PrintWriter writer2 = new PrintWriter("rmetrics2.csv");
		//List<Weapon> weaponList = new ArrayList<Weapon>();
		HashMap<String,Integer> weaponCount1 = new HashMap<String,Integer>();
		HashMap<String,Integer> materialCount1 = new HashMap<String,Integer>();
		HashMap<String,Integer> combCount1 = new HashMap<String,Integer>();
		HashMap<String,Integer> weaponCount2 = new HashMap<String,Integer>();
		HashMap<String,Integer> materialCount2 = new HashMap<String,Integer>();
		HashMap<String,Integer> combCount2 = new HashMap<String,Integer>();
		
		List<HashMap<String,Integer>> maps = Arrays.asList(weaponCount1,materialCount1,combCount1,weaponCount2,materialCount2,combCount2);
		
		for (int warmup = 0; warmup < 200;warmup++) {
			genTestWeapon(0);
			genTestWeapon(1);
		}
		extra.println("warmup complete");
		
		long[] time = {0,0};
		long[] temptime = {0,0};
		double[] battleTotal = {0,0};
		long starttime;
		int mult = 1;
		for (int j = 0; j <=trials;j++) {
			extra.println("trial " + j + " - "+ temptime[0] +" _ "+  temptime[1]);
			for (int s = 0; s <= 1; s++) {
				starttime = System.nanoTime();
				mult = s+1;
				for (int i = 0; i < attempts;i++) {
					Weapon weap = genTestWeapon(s);
					maps.get((1*mult)-1).put(weap.getBaseName(), maps.get((1*mult)-1).getOrDefault(weap.getBaseName(),0)+1);
					maps.get((2*mult)-1).put(weap.getMaterial(), maps.get((2*mult)-1).getOrDefault(weap.getMaterial(),0)+1);
					String temp = weap.getMaterial() +weap.getBaseName();
					maps.get((3*mult)-1).put(temp, maps.get((3*mult)-1).getOrDefault(temp,0)+1);
					battleTotal[s]+=weap.score();
				}
				temptime[s]=System.nanoTime()-starttime;
				time[s] += temptime[s];
			}
		}
		battleTotal[0]/=attempts;
		battleTotal[1]/=attempts;
		extra.println("total score 1: "+battleTotal[0]);
		extra.println("total score 2: "+battleTotal[1]);
		extra.println("old way took: " + time[0]/1_000_000_000 + " total");
		extra.println("squid way took: " + time[1]/1_000_000_000 + " total");
		for (int s = 0; s <= 1; s++) {
			mult = s+1;
			PrintWriter writer = s == 0 ? writer1 : writer2;
			writer.write(",");
			extra.println("starting " + (s+1));
			for (String str: Weapon.weaponTypes) {
				writer.write(str+",");
				extra.println(str+": "+maps.get((1*mult)-1).getOrDefault(str,0));
			}
			writer.write("\n");
			for (Material m: MaterialFactory.matList) {
				if (!m.weapon) {
					continue;
				}
				writer.write(m.name+",");
				extra.println(m.name+": "+maps.get((2*mult)-1).getOrDefault(m.name,0));
				for (String str: Weapon.weaponTypes) {
					writer.write(maps.get((3*mult)-1).getOrDefault(m.name+str,0)+",");
				}
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		}
		extra.println("---");
		MaterialFactory.materialWeapDiag();
	}
	
}