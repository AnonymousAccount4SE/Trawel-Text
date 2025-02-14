package trawel;
import java.util.ArrayList;
import java.util.List;

import trawel.battle.Combat;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.Stance;
import trawel.earts.ASpell;
import trawel.personal.Person;
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.body.Race;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Player;
import trawel.towns.World;
import trawel.towns.services.Store;

/**
 * @author Brian Malone
 * 2/8/2018
 * The Class that makes the decisions for the Persons
 * entirely static
 */

public class AIClass {
	
	//should account for running out of weight, and gold/weight when selling sometimes

	//static methods
	
	/**
	 * Get a random attack from the stance.
	 * @param theStance - (Stance)
	 * @return a random attack (Attack)
	 */
	public static Attack randomAttack(Stance theStance){
		return theStance.getAttack(extra.getRand().nextInt(theStance.getAttackCount()));
	}
	
	public static Attack eArtASpell(ASpell a, Person d) {
		switch (a) {
		case ELEMENTAL_BURST:
			return (new Attack(Skill.ELEMENTAL_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		case DEATH_BURST:
			return (new Attack(Skill.DEATH_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		case ARMOR_UP:
			return (new Attack(Skill.ARMOR_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		case BEFUDDLE:
			return (new Attack(Skill.ILLUSION_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		}
		throw new RuntimeException("ASpell not defined.");
	}
	
	/**
	 * Choose which attack in a stance to use. Supply with an intellect level for varying levels of 
	 * smarts
	 * @param theStance - (Stance) the stance from which to take the attack
	 * @param smarts - (int) intellect, how smart the attacker is 
	 * @return an attack (Attack)
	 */
	public static Attack chooseAttack(Stance theStance, int smarts, Combat com, Person attacker, Person defender) {
		
			int j = 1;
			List<Attack> attacks = theStance.giveList();
			
			if (!attacker.isPlayer()) {
			int times = 1;
			if (attacker.hasSkill(Skill.MAGE_POWER)) {
				times++;
				attacks.remove(0);
			}
			while (times > 0) {
			if (attacker.hasSkill(Skill.ELEMENTAL_MAGE)) {
				attacks.add(new Attack(Skill.ELEMENTAL_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			if (attacker.hasSkill(Skill.DEATH_MAGE)) {
				attacks.add(new Attack(Skill.DEATH_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			if (attacker.hasSkill(Skill.ARMOR_MAGE)) {
				attacks.add(new Attack(Skill.ARMOR_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			if (attacker.hasSkill(Skill.ILLUSION_MAGE)) {
				attacks.add(new Attack(Skill.ILLUSION_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			times--;}
			if (attacker.hasSkill(Skill.GOOFFENSIVE)) {
				Material mat = attacker.getBag().getHand().getMat();
				if (attacker.hasSkill(Skill.SHIELD)){
					switch (extra.randRange(1, 2)) {
					case 1: attacks.add(new Attack("bash",1,100.0,0*mat.sharpMult,10*mat.bluntMult,0,"X` bashes Y` with the their shield!",1,"blunt").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
					case 2: attacks.add(new Attack("smash",.9,90.0,0*mat.sharpMult,12*mat.bluntMult,0,"X` smashes Y` with the their shield!",1,"blunt").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
					}
				}else {
					if (attacker.hasSkill(Skill.PARRY)){
						switch (extra.randRange(1, 3)) {
						case 1: attacks.add(new Attack("slice",1,90.0,10*mat.sharpMult,0*mat.bluntMult,0*mat.pierceMult,"X` slices Y` with the their parrying dagger!",0,"sharp").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
						case 2: attacks.add(new Attack("dice",.8,70.0,8*mat.sharpMult,0*mat.bluntMult,0*mat.pierceMult,"X` dices Y` with the their parrying dagger!",0,"sharp").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
						case 3: attacks.add(new Attack("stab",1.1,90.0,0*mat.sharpMult,0*mat.bluntMult,8*mat.pierceMult,"X` stabs at Y` with the their parrying dagger!",0,"pierce").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
						}
					}
				}
			}
			if (attacker.hasSkill(Skill.KUNG_FU)) {
			switch (extra.randRange(1,3)) {
			case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(attacker.getFighterLevel(), defender,null,attacker));break;
			case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(attacker.getFighterLevel(), defender,null,attacker));break;
			case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(attacker.getFighterLevel(), defender,null,attacker));break;
			}
			}
			if (attacker.hasSkill(Skill.WAIT)) {
				attacks.add(new Attack("wait",0,20.0,0,0,0,"X` waits for a better chance!",-1,"wait"));
			}
			}else {
				if (Player.player.eaBox.berTrainLevel > 0) {
					attacks = theStance.giveList();
					switch (extra.randRange(1,3)) {
					case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(Player.player.eaBox.berTrainLevel, defender,null,attacker));break;
					case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(Player.player.eaBox.berTrainLevel, defender,null,attacker));break;
					case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(Player.player.eaBox.berTrainLevel, defender,null,attacker));break;
					}
				}
				if (Player.player.eaBox.aSpell1 != null) {
					attacks.add(eArtASpell(Player.player.eaBox.aSpell1,defender));
				}
				if (Player.player.eaBox.aSpell2 != null) {
					attacks.add(eArtASpell(Player.player.eaBox.aSpell2,defender));
				}
				if (Player.player.eaBox.exeTrainLevel > 0) {
					attacks.add(new Attack(Skill.EXECUTE_ATTACK,Player.player.eaBox.getExeExe(), defender.getBag().getRace().targetType));
				}
				
				if (Player.player.eaBox.drunkTrainLevel > 0) {
					switch (extra.randRange(1,3)) {
					case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(Player.player.eaBox.drunkTrainLevel, defender,null,attacker));break;
					case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(Player.player.eaBox.drunkTrainLevel, defender,null,attacker));break;
					case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(Player.player.eaBox.drunkTrainLevel, defender,null,attacker));break;
					}
					attacks.add(new Attack(Skill.DRUNK_DRINK,Player.player.eaBox.drunkTrainLevel, defender.getBag().getRace().targetType));
				}
				
				if (Player.player.eaBox.huntTrainLevel > 0) {
					attacks.add(new Attack("wait",0,20.0,0,0,0,"X` waits for a better chance!",-1,"wait"));
					attacks.add(new Attack(Skill.MARK_ATTACK,Player.player.eaBox.huntTrainLevel, defender.getBag().getRace().targetType));
				}
				if (Player.player.eaBox.bloodTrainLevel > 0) {
					attacks.add(new Attack(Skill.BLOOD_SURGE,Player.player.eaBox.bloodTrainLevel, defender.getBag().getRace().targetType));
					attacks.add(new Attack(Skill.BLOOD_HARVEST,Player.player.eaBox.bloodTrainLevel, defender.getBag().getRace().targetType));
				}
				

			}


			if (attacker.isPlayer()) {
				int numb = 9;
				while (numb == 9 || numb < 1) {
					if (numb == 9) {
						switch (mainGame.attackDisplayStyle) {
						case CLASSIC:
							extra.println("     name                hit    delay    sharp    blunt     pierce");
							for(Attack a: attacks) {
								extra.print(j + "    ");
								a.display(1,attacker,defender);
								j++;
							}
						case TWO_LINE1:
							extra.println("Attacks:");
							for(Attack a: attacks) {
								extra.print(j + " ");
								a.display(2,attacker,defender);
								j++;
							}
							break;
						}
						extra.println("9 debug examine");
						numb = extra.inInt(attacks.size(),true);
					}else {
						numb = -numb;//restore attack choice
					}
					if (numb == 9) {
						extra.print("You have ");
						attacker.displayHp();
						defender.displayStats();
						
						defender.displaySkills();
						defender.debug_print_status(0);
						
						defender.displayArmor();
						defender.displayHp();
						//new debug examine code
						extra.println("Press 9 to repeat attacks.");
						numb = extra.inInt(attacks.size(),true);
						if (numb != 9) {
							numb = -numb;//store attack choice
						}
					}
				}
				return attacks.get(numb-1);
			}
			return attackTest(attacks,smarts,com, attacker, defender);
	}
	
	/**
	 * Has the ai simulate a couple attacks, and choose which one is best.
	 * Basically simulates choosing each of the different attacks, and deciding which will deal the most damage.
	 * It accounts for enemy armor and weapon material, but not the potential for the enemy to kill them before they do a slower attack.
	 * @param attacks (Stance)
	 * @param rounds (int)
	 * @param com (Combat) - the combat this is taking place in (so we can access the handleattack method)
	 * @param attacker (Person) - the person who is doing the attack
	 * @param defender (Person) - the person who is defending from the attack
	 * @return the chosen attack (Attack)
	 */
	public static Attack attackTest(List<Attack> attacks,int rounds, Combat com, Person attacker, Person defender) {
		int size = attacks.size();
		int i = size-1;
		int j = 0;
		double[] damray = new double[size];
		
		extra.offPrintStack();
		while (i >= 0) {
			j = 0;
			do {
				if (!attacks.get(i).isMagic()) {
				damray[i]+=100*extra.zeroOut((double)com.handleAttack(attacks.get(i),defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender).damage);}else {
					if (attacks.get(i).getSkill() == Skill.DEATH_MAGE) {
						damray[i]+=100*extra.zeroOut(attacks.get(i).getBlunt());
						if (defender.hasSkill(Skill.LIFE_MAGE)) {
							damray[i] = 0;
						}
					}
				}
				j++;
			}while (j < rounds);
			damray[i]/= (rounds*attacks.get(i).getSpeed());
			i--;
		}
		extra.popPrintStack();
		j=0;
		i=0;//will now hold position of the highest one
		double highestValue = -1;
		while (j < size) {
			//extra.println(theStance.getAttack(j).getName() + " " + damray[j]);//debug
			if (damray[j] > highestValue) {
				highestValue = damray[j];
				i = j;
			}
			j++;
		}
		
		
		if (highestValue <=0) {return extra.randList(attacks);}//if they're all zero, just return a random one
		//extra.println("Chose: " + theStance.getAttack(i).getName() + " " + damray[i]);//debug
		return attacks.get(i);
	}
	
	
	
	/**
	 * Checks to see if there's any items in the inventory that cost zero gold.
	 * Any that do probably make it (nigh) impossible to win, so
	 * it discards them in favor of a level 1 item, randomly generated.
	 * Also checks to see if the total modifier for the item is very low.
	 * Returns true if an item was replaced this way.
	 * 
	 * NOTE: this function should be replaced with not handing out such items in the first place and relegated to a backup
	 * 
	 * @param inv (Inventory)
	 */
	public static boolean checkCheap(Inventory inv) {
		return false;
		//local vars
		/*
		int i=0;
		boolean soldSomething = false; //did we sell something yet?
		Item hold; //the item we're currently looking at
		EnchantConstant holdEnchant;
		while (i < 5) {//check to see if an armor causes any zero's or is overwhelmingly negative
			hold = inv.getArmorSlot(i);
			if (hold.getEnchant() != null) {
				holdEnchant = hold.getEnchant();
			
			if (hold.getCost() < 2 || (holdEnchant.getAimMod()*holdEnchant.getDamMod()*holdEnchant.getDodgeMod()*holdEnchant.getHealthMod()*holdEnchant.getSpeedMod()) < .5) {
			Services.sellItem((Armor)hold,inv,true);
			soldSomething = true;
			}}
			i++;
		}//end while
		hold = inv.getHand();
		if (hold != null) {
			if (hold.getEnchant() != null) {//if there's an enchant, check to see if the weapon causes any zero's or is overwhelmingly negative
				holdEnchant = hold.getEnchant();
			
			if (hold.getCost() < 2 || (holdEnchant.getAimMod()*holdEnchant.getDamMod()*holdEnchant.getHealthMod()*holdEnchant.getSpeedMod()) < .5) {//*holdEnchant.getDodgeMod() //not being able to dodge isn't an instant loss
				Services.sellItem((Weapon)hold,inv,true);
				soldSomething = true;
			}
			}
		}
		return soldSomething;
		*/
		
		//for now, this is done elsewhere again
	}
	
	/**
	 * Look over this person's equipment to make sure it doesn't render them impossible to win.
	 * @param man (Person)
	 */
	public static void checkYoSelf(Person man) {
		//extra.println(man.getName() + " starts looking over their "+bpmFunctions.choose("equipment","gear","inventory","belongings")+".");
		if (!man.isPlayer()) {
			while (checkCheap(man.getBag()));
		}
		//extra.println(man.getName() + " has taken stock of their "+bpmFunctions.choose("equipment","gear","inventory","belongings")+".");
		//man.displayStatsShort();
	}
	
	/**
	 * Take the inventory out of @param loot and put it, or any money gained by selling it, into @param stash
	 * @param loot (Inventory)
	 * @param stash (Inventory)
	 * @param smarts (int)
	 * @param sellStuff if the items can be atom-smashed into aether
	 */
	public static void loot(Inventory loot, Inventory stash, int smarts, boolean sellStuff, Person p) {
		int i = 0;
		boolean normalLoot = loot.getRace().racialType == Race.RaceType.HUMANOID;
		if (normalLoot && smarts < 0 && Player.getTutorial()) {
			extra.println("You are now looting something! The first item presented will be the new item, the second, your current item, and finally, the difference will be shown. Some items may be autosold if all their visible stats are worse.");
		}
		if (normalLoot) {
			while (i < 5) {
				if (compareItem(stash.getArmorSlot(i),loot.getArmorSlot(i),smarts,true,p)) {
					if (sellStuff) {
							//Services.sellItem(stash.swapArmorSlot(loot.getArmorSlot(i),i),stash,false);
							Services.aetherifyItem(stash.getArmorSlot(i),stash);
							stash.swapArmorSlot(loot.getArmorSlot(i),i);//we lose the ref to the thing we just deleted here
							loot.setArmorSlot(null,i);
						}else {
							loot.swapArmorSlot(stash.swapArmorSlot(loot.getArmorSlot(i),i), i);
						}
				}else {
					if (sellStuff) {
						//Services.sellItem(loot.getArmorSlot(i),loot,stash,false);}
						Services.aetherifyItem(loot.getArmorSlot(i),stash);
						loot.setArmorSlot(null,i);
					}
				}


				if (smarts < 0 && Networking.connected()) {
					Networking.charUpdate();
					String depth = null;
					switch (i) {
					case 0:depth= "-6|";break; //head
					case 1:depth= "-3|";break; //arms
					case 2:depth= "-5|";break; //chest
					case 3:depth= "-1|";break; //legs
					case 4:depth= "-2|";break; //feet
					}
					Networking.send("RemoveInv|1|" + depth);
				}
				i++;
			}
			if (compareItem(stash.getHand(),loot.getHand(),smarts,true,p)) {
				if (sellStuff) {
						Services.sellItem(stash.swapWeapon(loot.getHand()),stash,false);
						Services.aetherifyItem(stash.getHand(),stash);
						stash.swapWeapon(loot.getHand());//we lose the ref to the thing we just deleted here
						loot.setWeapon(null);
					}else {
						loot.swapWeapon(stash.swapWeapon(loot.getHand()));
					}
			}else {
				if (sellStuff) {
					Services.aetherifyItem(loot.getHand(), stash);
					loot.setWeapon(null);
				}
			}
			Networking.send("RemoveInv|1|2|");
		}else {
			if (sellStuff) {
				while (i < 5) {
					if (loot.getArmorSlot(i).canAetherLoot()) {
						Services.aetherifyItem(loot.getArmorSlot(i),stash);
						loot.setArmorSlot(null,i);
					}
					if (loot.getHand().canAetherLoot()) {
						Services.aetherifyItem(loot.getHand(), stash);
						loot.setWeapon(null);
					}
					i++;
				}
			}
		}
		if (smarts < 0) {
			Networking.charUpdate();
			/*if (Player.hasSkill(Skill.LOOTER) && normalLoot) {
				stash.addGold(10);
				extra.println("You take the extra coins they had stored away in their " + extra.choose("spleen","appendix","imagination","lower left thigh","no-no place","closed eyes") + ". +10 gold");
			}*/
			for (DrawBane db: loot.getDrawBanes()) {
				stash.addNewDrawBane(db);
			}
		}else {
			//TODO drawbane taking ai
		}
		if (sellStuff) {
			int money = loot.getGold();
			stash.addGold(money);
			int aether = loot.getAether();
			stash.addAether(aether);
			loot.removeCurrency();
			if (!extra.getPrint()) {
				extra.println(p.getName() + " claims the " + aether + " aether and " + World.currentMoneyDisplay(money) + ".");
			}
		}
	}
	
	/**
	 * Returns true if they want to replace it
	 * @param hasItem (Item)
	 * @param toReplace (Item)
	 * @param smarts (int)
	 * @return if you should swap items (boolean)
	 */
	public static boolean compareItem(Item hasItem, Item toReplace,int smarts, boolean autosellOn, Person p) {
		//p is the person comparing it, and is used to apply skills and feats that modify stats
		//for the player, the base stats should be the same, but the 'diff' should show the actual difference
		//when swapped
		if (Armor.class.isInstance(hasItem)) {
			if (autosellOn && worseArmor((Armor)hasItem,(Armor)toReplace)) {
				if (smarts < 0) {
					extra.print(extra.PRE_YELLOW+"Automelted the ");
					toReplace.display(1);
					Networking.waitIfConnected(100L);
				}
				return false;
			}
		}
		if (smarts < 0){
			extra.println("Use the");
			toReplace.display(1);
			extra.println("instead of your");
			hasItem.display(1);
			displayChange(hasItem,toReplace, p);
			return extra.yesNo();
		}
		if (!Weapon.class.isInstance(hasItem)){
		return (toReplace.getAetherValue()>hasItem.getAetherValue());
	}else {
		if (smarts < 2) {
			return (toReplace.getAetherValue()>hasItem.getAetherValue());
			}
		if (((Weapon)(toReplace)).score() > ((Weapon)(hasItem)).score()){
			return true;	
			}
		return false;
		}
	}
	
	public static boolean compareItem(Inventory bag, Item toReplace,int smarts, boolean autosellOn, Person p) {
		Item item = null;
		if (Armor.class.isInstance(toReplace)) {
			Armor a = (Armor)toReplace;
			item = bag.getArmorSlot(a.getArmorType());
		}else {
			if (Weapon.class.isInstance(toReplace)) {
				item = bag.getHand();
			}
		}
		return compareItem(item,toReplace,smarts,autosellOn, p);
	}
	
	public static boolean compareItem(Item current, Item next, Person p, Store s) {
		if (!p.isPlayer()) {
			return compareItem(current,next,2,false,p);
		}
		extra.println("Buy the");
		next.display(s,true);
		extra.println("replacing your");
		current.display(s,false);
		displayChange(current,next, p,s);
		return extra.yesNo();
	}

	private static boolean worseArmor(Armor hasItem, Armor toReplace) {
		if (toReplace.getBluntResist() > hasItem.getBluntResist() || 
				toReplace.getSharpResist() > hasItem.getSharpResist() || 
				toReplace.getPierceResist() > hasItem.getPierceResist() //|| toReplace.getDexMod() >hasItem.getDexMod()
				) 
		{
		return false;}
		//enchant compare
		if (hasItem.isEnchanted()) {
			if (toReplace.isEnchanted()) {
				Enchant e = hasItem.getEnchant();
				Enchant e2 = toReplace.getEnchant();
				if (e.getAimMod() < e2.getAimMod() || e.getDamMod() < e2.getDamMod() || e.getDodgeMod() < e2.getDodgeMod() || e.getHealthMod() < e2.getHealthMod() || e.getSpeedMod() < e2.getSpeedMod()) {
					return false;
				}
			}else {
				Enchant e = hasItem.getEnchant();
				if (e.getAimMod() < 1 || e.getDamMod() < 1 || e.getDodgeMod() < 1 || e.getHealthMod() < 1 || e.getSpeedMod() < 1) {
					return false;
				}
			}
		}else {
			if (toReplace.isEnchanted()) {
				Enchant e = toReplace.getEnchant();
				if (e.getAimMod() > 1 || e.getDamMod() > 1 || e.getDodgeMod() > 1 || e.getHealthMod() > 1 || e.getSpeedMod() > 1) {
					return false;
				}
			}//no else
		}
		
		return true;
	}

	public static void displayChange(Item hasItem, Item toReplace, Person p) {
		displayChange(hasItem,toReplace,p,null);
	}
	
	public static void displayChange(Item hasItem, Item toReplace, Person p, Store s) {
		//p is used to display absolute stat changes instead of just raw stats like the non-diff
		extra.println();
		int costDiff = 0;
		String costName = null;
		if (s == null) {
			costName = "aether";
			costDiff = toReplace.getAetherValue() - hasItem.getAetherValue();
		}else {
			costName = s.getTown().getIsland().getWorld().moneyString();
			costDiff = s.getDelta(hasItem,toReplace);
			//costDiff = (int) (Math.ceil(s.getMarkup()*toReplace.getMoneyValue()) - hasItem.getMoneyValue());//DOLATER match rounding across places
		}
		
		if (Armor.class.isInstance(hasItem)) {
			Armor hasArm = (Armor) hasItem;
			Armor toArm = (Armor) toReplace;
			if (Player.getTutorial()) {
				extra.println("sbp = sharp, blunt, pierce");
			}
			extra.println(extra.PRE_MAGENTA+"Difference: sbp: " 
			+ extra.hardColorDelta1(toArm.getSharpResist(),hasArm.getSharpResist())
			+ " " + extra.hardColorDelta1(toArm.getBluntResist(),hasArm.getBluntResist())
			+ " " + extra.hardColorDelta1(toArm.getPierceResist(),hasArm.getPierceResist())
			+ " " + priceDiffDisp(costDiff,costName,s)
			);
			//" dex: " + extra.format2(toArm.getDexMod()-hasArm.getDexMod()) +
			if (hasItem.getEnchant() != null || toReplace.getEnchant()!= null) {
				displayEnchantDiff(hasItem.getEnchant(),toReplace.getEnchant());
			}
		}else {
			if (Weapon.class.isInstance(hasItem)) {
				Weapon hasWeap = (Weapon)hasItem;
				Weapon toWeap = (Weapon)toReplace;
				if (Player.getTutorial()) {
					extra.println("hd = highest damage, ad = average damage, bs = battlescore, q = weapon qualities");
				}
				//TODO: this will break if a qual is in it more than twice
				boolean isQDiff = !toWeap.qualList.containsAll(hasWeap.qualList) && !hasWeap.qualList.containsAll(toWeap.qualList);
				int qualDiff = isQDiff ? toWeap.qualList.size()-hasWeap.qualList.size() : 0;
				
				extra.println(extra.PRE_MAGENTA+"Difference: hd/ad/bs: " 
				+ (extra.hardColorDelta2(toWeap.highest(),hasWeap.highest()))
				+ "/" + (extra.hardColorDelta2(toWeap.average(),hasWeap.average()))
				+ "/" + (extra.hardColorDelta2(toWeap.score(),hasWeap.score()))
				//if the qualities are the same, 'q=', if neither has any, do not display
				+ (isQDiff ? " q " + extra.colorBaseZeroTimid(qualDiff) : (toWeap.qualList.size() > 0 ? (" q =") : ""))
				+ " " + priceDiffDisp(costDiff,costName,s)
				);
				if (((Weapon)hasItem).getEnchant() != null || ((Weapon)toReplace).getEnchant()!= null) {
					displayEnchantDiff(((Weapon)hasItem).getEnchant(),((Weapon)toReplace).getEnchant());
				}
			}else {
				extra.println(priceDiffDisp(costDiff,costName,s));
			}
		}
		
	}
	
	/**
	 * this method works differently if given a Store or not.
	 * <br>
	 * if given a store, the delta should be provided from its delta function
	 * <br>
	 * otherwise, do (item wanting to take) - (item that they have)
	 * @param delta
	 * @param name
	 * @param s
	 * @return
	 */
	public static String priceDiffDisp(int delta,String name, Store s) {
		if (s == null) {
			return "[c_white]"+name+": " + (delta != 0 ? extra.colorBaseZeroTimid(delta) : "=");
		}
		if (delta < 0) {//costs less, might be gaining money
			return extra.TIMID_BLUE + "requires " +  Math.abs(delta) + " buy value";
		}else {//costs more, losing money
			return extra.TIMID_GREY + "will return " +delta + " " + name;
		}
	}
	
	
	private static void displayEnchantDiff(Enchant hasItem, Enchant toReplace) {
		if (hasItem == null) {
			enchantDiff(1,toReplace.getAimMod(),"aim");
			enchantDiff(1,toReplace.getDamMod(),"damage");
			enchantDiff(1,toReplace.getDodgeMod(),"dodge");
			enchantDiff(1,toReplace.getHealthMod(),"health");
			enchantDiff(1,toReplace.getSpeedMod(),"speed");
			enchantDiff(0,toReplace.getFireMod(),"fire");
			enchantDiff(0,toReplace.getShockMod(),"shock");
			enchantDiff(0,toReplace.getFreezeMod(),"frost");
		}else {
			if (toReplace == null) {
				enchantDiff(hasItem.getAimMod(),1,"aim");
				enchantDiff(hasItem.getDamMod(),1,"damage");
				enchantDiff(hasItem.getDodgeMod(),1,"dodge");
				enchantDiff(hasItem.getHealthMod(),1,"health");
				enchantDiff(hasItem.getSpeedMod(),1,"speed");
				enchantDiff(hasItem.getFireMod(),0,"fire");
				enchantDiff(hasItem.getShockMod(),0,"shock");
				enchantDiff(hasItem.getFreezeMod(),0,"frost");
			}else {
				enchantDiff(hasItem.getAimMod(),toReplace.getAimMod(),"aim");
				enchantDiff(hasItem.getDamMod(),toReplace.getDamMod(),"damage");
				enchantDiff(hasItem.getDodgeMod(),toReplace.getDodgeMod(),"dodge");
				enchantDiff(hasItem.getHealthMod(),toReplace.getHealthMod(),"health");
				enchantDiff(hasItem.getSpeedMod(),toReplace.getSpeedMod(),"speed");
				enchantDiff(hasItem.getFireMod(),toReplace.getFireMod(),"fire");
				enchantDiff(hasItem.getShockMod(),toReplace.getShockMod(),"shock");
				enchantDiff(hasItem.getFreezeMod(),toReplace.getFreezeMod(),"frost");
				//enchantDiff(hasItem,toReplace,"aim");
			}
		}
		
	}
	
	private static void displayEnchantDiff(EnchantConstant hasItem, EnchantConstant toReplace) {
		if (hasItem == null) {
			enchantDiff(1,toReplace.getAimMod(),"aim");
			enchantDiff(1,toReplace.getDamMod(),"damage");
			enchantDiff(1,toReplace.getDodgeMod(),"dodge");
			enchantDiff(1,toReplace.getHealthMod(),"health");
			enchantDiff(1,toReplace.getSpeedMod(),"speed");
		}else {
			if (toReplace == null) {
				enchantDiff(hasItem.getAimMod(),1,"aim");
				enchantDiff(hasItem.getDamMod(),1,"damage");
				enchantDiff(hasItem.getDodgeMod(),1,"dodge");
				enchantDiff(hasItem.getHealthMod(),1,"health");
				enchantDiff(hasItem.getSpeedMod(),1,"speed");
			}else {
				enchantDiff(hasItem.getAimMod(),toReplace.getAimMod(),"aim");
				enchantDiff(hasItem.getDamMod(),toReplace.getDamMod(),"damage");
				enchantDiff(hasItem.getDodgeMod(),toReplace.getDodgeMod(),"dodge");
				enchantDiff(hasItem.getHealthMod(),toReplace.getHealthMod(),"health");
				enchantDiff(hasItem.getSpeedMod(),toReplace.getSpeedMod(),"speed");
				//enchantDiff(hasItem,toReplace,"aim");
			}
		}
		
	}
	
	private static void displayEnchantDiff(EnchantHit hasItem, EnchantHit toReplace) {
		if (hasItem == null) {
			enchantDiff(0,toReplace.getFireMod(),"fire");
			enchantDiff(0,toReplace.getShockMod(),"shock");
			enchantDiff(0,toReplace.getFreezeMod(),"frost");
		}else {
			if (toReplace == null) {
				enchantDiff(hasItem.getFireMod(),0,"fire");
				enchantDiff(hasItem.getShockMod(),0,"shock");
				enchantDiff(hasItem.getFreezeMod(),0,"frost");
			}else {
				enchantDiff(hasItem.getFireMod(),toReplace.getFireMod(),"fire");
				enchantDiff(hasItem.getShockMod(),toReplace.getShockMod(),"shock");
				enchantDiff(hasItem.getFreezeMod(),toReplace.getFreezeMod(),"frost");
			}
		}
	}
	
	private static void enchantDiff(float has, float get, String name) {
		if (has-get != 0) {
			extra.println(" " +extra.hardColorDelta2(get,has) + " " + name + " mult");
		}
	}
	
	/*
	public static void displayDifference(Item hasItem, Item toReplace) {
		extra.println("Difference: " +(hasItem));
	}*/


}
