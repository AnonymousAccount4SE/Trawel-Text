import java.awt.Color;
import java.util.ArrayList;
/**
 * A combat holds some of the more battle-focused commands.
 * @author Brian Malone
 * 2/8/2018
 */

public class Combat {
	//instance variables
	private Person attacker;
	private Person defender;
	public ArrayList<Person> survivors;
	public ArrayList<Person> killed;
	private boolean newTarget = false;
	//constructor
	/**
	 * Holds a fight to the death, between two people.
	 * @param manOne(Person)
	 * @param manTwo (Person)
	 */
	public Combat(Person manOne, Person manTwo,World w) {
		if (Player.getTutorial()) {
			extra.println("Welcome to a battle! You can turn the tutorial off in the 'you' menu.");
			extra.println("Choose your attack below: higher is better, except in the case of delay!");
			extra.println("Delay is how long an action takes- it determines turn order and skipping.");
			extra.println("For example, two 30 delay actions would go through before one 100 delay action.");
			extra.println("sbp stands for sharp blunt pierce- the three damage types.");
			extra.println("Hp is restored at the start of every battle.");
		}
		//Setup
		manOne.battleSetup();
		//extra.println("");
		manTwo.battleSetup();
		manTwo.displayStatsShort();
		attacker = manOne;
		defender = manTwo;
		
		
		attacker = manTwo;
		defender = manOne;
		setAttack(manTwo,manOne);
		setAttack(manOne,manTwo);
		extra.println("");
		extra.println(extra.choose("Our two fighters square off...","They look tense.","It's time to fight.","They look ready to fight.","The battle has begun."));
		BardSong song = w.startBardSong(manOne,manTwo);
		do {//combat loop
			if (manOne.getTime() < manTwo.getTime()) {
				attacker = manOne;
				defender = manTwo;
			}else {
				attacker = manTwo;
				defender = manOne;
			}
			if (attacker.hasSkill(Skill.BLITZ)) {
				attacker.advanceTime(3);
			}
			defender.advanceTime(attacker.getTime());
			
			handleTurn( attacker,  defender,  song);
			
			
			if (manOne.isAlive() && manTwo.isAlive()) {
			setAttack(attacker,defender);}
		}
		while(manOne.isAlive() && manTwo.isAlive());
		
		extra.println(extra.choose("The dust settles...","The body drops to the floor.","Death has come.","The battle is over."));
		extra.println(defender.getName() + extra.choose(" lies dead..."," walks the earth no more..."," has been slain."));
		song.addKill(attacker,defender);
		attacker.getBag().getHand().addKill();
		if (extra.chanceIn(1,2) || attacker.getLevel() < defender.getLevel()) {
			attacker.getTaunts().addTaunt("It was I who " + extra.choose("slew","slaughtered","struck down","killed") + " " + defender.getName()+ "!");
		}
		if (extra.chanceIn(1,2)) {
			attacker.getTaunts().addTaunt(defender.getTaunts().getTaunt());
		}
		if (extra.chanceIn(1,2)) {
			attacker.getTaunts().addBoast(defender.getTaunts().getBoast());
		}
		//if (extra.chanceIn(1,2) || attacker.getLevel() < defender.getLevel()) {
			//attacker.getTaunts().addBoast("It was I who " + extra.choose("slew","slaughtered","struck down","killed") + " " + defender.getName()+ "!");
		//}//t'was I who struck down X who slew Y who slew Z!
		if (extra.chanceIn(1,3)) {
			attacker.getTaunts().removeTaunt();
		}
		if (extra.chanceIn(1,4)) {
			attacker.getTaunts().removeBoast();
		}
	}
	
	
	public Combat(World w,ArrayList<Person>... people) {
		int size = people.length;
		BardSong song = w.startBardSong();
		ArrayList<Person> totalList = new ArrayList<Person>();
		ArrayList<Person> killList = new ArrayList<Person>();
		
		for (ArrayList<Person> peoples: people) {
			for (Person p: peoples) {
				if (p.isPlayer()) {
					Networking.setBattle(Networking.BattleType.NORMAL);
				}
				p.battleSetup();
				totalList.add(p);
			Person otherperson = null;
			while (otherperson == null) {
				int rand = extra.randRange(0,size-1);
				ArrayList<Person> otherpeople = people[rand];
				if (otherpeople.contains(p) || otherpeople.size() == 0) {
					continue;
				}
				otherperson = extra.randList(otherpeople);
			}
			if (p.isPlayer()) {
				otherperson.displayStatsShort();
				p.getBag().graphicalDisplay(-1,p);
				otherperson.getBag().graphicalDisplay(1,otherperson);
			}
			setAttack(p,otherperson);
			p.getNextAttack().defender = otherperson;
			}
		}
		while(true) {
			Person quickest= null;
			double lowestDelay = Double.MAX_VALUE;
			for (Person p: totalList) {
				if (lowestDelay > p.getTime()) {
					lowestDelay = p.getTime();
					quickest = p;
				}
			}
			if (quickest.hasSkill(Skill.BLITZ)) {
				lowestDelay-=3;
			}
			
			
			for (Person p: totalList) {
				p.advanceTime(lowestDelay);
			}
			
			Person defender = quickest.getNextAttack().defender;
			boolean wasAlive = defender.isAlive();
			newTarget = false;
			handleTurn(quickest,defender,song);
			if (!defender.isAlive() && wasAlive) {
				extra.println("They die!");
				quickest.getBag().getHand().addKill();
				if (quickest.hasSkill(Skill.KILLHEAL)){
					quickest.addHp(5*quickest.getLevel());
				}
				song.addKill(quickest,defender);
				totalList.remove(defender);
				killList.add(defender);
				for (ArrayList<Person> list: people) {
					if (list.contains(defender)) {
						list.remove(defender);
						break;
					}
				}
			}else {
				if (newTarget) {
					//the defender has been befuddled or confused
					Person otherperson = null;
					while (otherperson == null) {
						int rand = extra.randRange(0,size-1);
						ArrayList<Person> otherpeople = people[rand];
						if ((otherpeople.contains(defender) && extra.chanceIn(3,quickest.getMageLevel()+3)) || otherpeople.size() == 0) {
							continue;
						}
						otherperson = extra.randList(otherpeople);
						if (otherperson == defender) {
							continue;
						}
						defender.getNextAttack().defender = otherperson;
					}
				}
			}
			
			int sides = 0;
			for (ArrayList<Person> list: people) {
				if (list.size() > 0) {
					sides++;
				}
			}
			
			if (sides == 1) {
				break;//end battle
			}
		
			
			Person otherperson = null;
			while (otherperson == null) {
				int rand = extra.randRange(0,size-1);
				ArrayList<Person> otherpeople = people[rand];
				if (otherpeople.contains(quickest) || otherpeople.size() == 0) {
					continue;
				}
				otherperson = extra.randList(otherpeople);
			}
			if (quickest.isPlayer()) {
				otherperson.displayStatsShort();
				quickest.getBag().graphicalDisplay(-1,quickest);
				otherperson.getBag().graphicalDisplay(1,otherperson);
			}
			setAttack(quickest,otherperson);
			quickest.getNextAttack().defender = otherperson;
			if (quickest.hasSkill(Skill.LIFE_MAGE)) {
				for (ArrayList<Person> list: people) {
					if (list.contains(quickest)) {
						for (Person p: list) {
							if (p.getHp() < p.getMaxHp()) {p.addHp(1);}
						}
					}
				}
			}
			
		}
		
		survivors = totalList;
		killed = killList;
		
		
	}
	
	//instance methods
	/**
	 * Calculate if an attack hits, and how much damage it would deal, countered by the armor.
	 * @param att The attack being used (Attack)
	 * @param def the inventory of the defender (Inventory)
	 * @param off the inventory of the attacker (Inventory)
	 * @param armMod the armormod (double)
	 * @param printString (boolean) - if true, print the attack
	 * @return
	 */
	public int handleAttack(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		if (att.getName().contains("examine")){
			if  (!attacker.isPlayer()) {
				extra.println("They examine you...");
				return -2;
			}
			attacker.displayStats();
			attacker.displayArmor();
			attacker.displayHp();
			defender.displayStats();
			defender.displayArmor();
			defender.displayHp();
			if (attacker.hasSkill(Skill.HPSENSE)) {
			defender.displaySkills();}
			extra.print(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
			return -2;
		}
		if (defender.isAlive()) {
		extra.print(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));}else {
			extra.print(att.attackStringer(attacker.getName(),defender.getName() + "'s corpse",off.getHand().getName()));	
		}
		double damMod = off.getDam();
		if ((def.getDodge()/(att.getHitmod()*off.getAim()))*Math.random() > 1.0){
			return -1;//do a dodge
		}
		//return the damage-armor, with each type evaluated individually
		Networking.send("PlayHit|" +def.getSoundType(att.getSlot()) + "|"+att.getSoundIntensity() + "|" +att.getSoundType()+"|");
		return (int)((extra.zeroOut((att.getSharp()*damMod)-(def.getSharp(att.getSlot())*armMod))*Math.random())+extra.zeroOut((att.getBlunt()*damMod)-(def.getBlunt(att.getSlot())*armMod)*Math.random())+extra.zeroOut((att.getPierce()*damMod)-(def.getPierce(att.getSlot())*armMod)*Math.random()));
		
	}
	
	/**
	 * does stuff that can't go in handleAttack because it's permanent and handleAttack is used in ai stuff.
	 * handles elemental damage
	 * should scale of off % of hp damaged or something to avoid confusing the ai
	 */
	public void handleAttackPart2(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender, int damageDone) {
		double percent = ((double)extra.zeroOut(damageDone))/((double)defender.getMaxHp());
		if (off.getHand().isEnchantedHit() && !(att.getName().contains("examine"))) {
			
			def.burn(def.getFire(att.getSlot())*percent*off.getHand().getEnchantHit().getFireMod()/2,att.getSlot());
			
			defender.advanceTime(-(percent*defender.getTime()*off.getHand().getEnchantHit().getFreezeMod()*def.getFreeze(att.getSlot())));
			
			defender.takeDamage((int)(percent*off.getHand().getEnchantHit().getShockMod()/3*def.getShock(att.getSlot())));
		}
		if (attacker.hasSkill(Skill.DSTRIKE) && percent >= .80) {
			defender.takeDamage((int) (((1-percent)*defender.getMaxHp())+10));
		}
	}
	
	/**
	 * 
	 * @param attacker
	 * @param defender
	 * @return damagedone
	 */
	public void handleTurn(Person attacker, Person defender, BardSong song) {
		if (attacker.hasEffect(Effect.RECOVERING)) {
			attacker.removeEffect(Effect.RECOVERING);
			attacker.addHp(attacker.getLevel()*5);
		}
		if (defender.hasSkill(Skill.COUNTER)) {
			defender.advanceTime(2);
		}
		if (attacker.hasSkill(Skill.SPUNCH)) {
			defender.advanceTime(-2);
		}
		if (attacker.hasSkill(Skill.CURSE_MAGE)) {
			defender.addEffect(Effect.CURSE);
		}
		if (attacker.getBag().getRace().racialType != Race.RaceType.BEAST && extra.chanceIn(1,4)) {
			if (extra.chanceIn(1,3)) {
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","boasts")+ " \"" + attacker.getTaunts().getBoast()+"\"");		
			}else {
				if (attacker.isRacist() && !attacker.getBag().getRace().equals(defender.getBag().getRace()) && extra.chanceIn(1,3)) 
				{
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" +defender.getBag().getRace().randomInsult()+"\"");
				}else {
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" + attacker.getTaunts().getTaunt()+"\"");
					}				
			}
		}
		
		if (!attacker.getNextAttack().isMagic()) {
		int damageDone = this.handleAttack(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),0.05,attacker,defender);
		this.handleAttackPart2(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),0.05,attacker,defender,damageDone);
		if (damageDone > 0) {
			//Wound effects
			inflictWound(attacker,defender,damageDone);
			song.addAttackHit(attacker,defender);
			if (!extra.printMode) {
				Networking.sendColor(Color.ORANGE);
			}
			if (defender.takeDamage(damageDone)) {
				//extra.print(" " + choose("Striking them down!"," They are struck down."));
				if (!extra.printMode) {
					Networking.sendColor(Color.RED);
				}
			}
		}else {
			if (damageDone == -1) {
				song.addAttackMiss(attacker,defender);
				if (!extra.printMode) {
					Networking.sendColor(Color.YELLOW);
				}
					extra.print((String)extra.choose(" They miss!"," The attack is dodged!"," It's a miss!"," It goes wide!"," It's not even close!"));
					if (defender.hasSkill(Skill.SPEEDDODGE)) {
						defender.advanceTime(10);
						if (defender.hasSkill(Skill.DODGEREF)) {
							defender.addHp(attacker.getLevel());
						}
					}
				}else {
					if (damageDone == 0) {
						song.addAttackArmor(attacker,defender);
						if (!extra.printMode) {
							Networking.sendColor(Color.BLUE);
						}
					extra.print(" "+(String)extra.choose("But it is ineffective...","The armor deflects the blow!","However, the attack fails to deal damage through the armor."));
					if (defender.hasSkill(Skill.ARMORHEART)) {
						defender.addHp(attacker.getLevel());
					}
					if (defender.hasSkill(Skill.ARMORSPEED)) {
						defender.advanceTime(10);
					}
					}else {
						
					}
				}
		}
		}else {
			//the attack is a magic spell
			handleMagicSpell(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),0.05,attacker,defender);
			song.addAttackHit(attacker,defender);
		}
		
			extra.println("");
			
				Person p = defender;
				float hpRatio = ((float)p.getHp())/(p.getMaxHp());
				//extra.println(p.getHp() + p.getMaxHp() +" " + hpRatio);
				if (Math.random()*5 >= 2) {song.addHealth(p);}
				if (hpRatio == 1) {
					extra.println(p.getName() + " is untouched.");
				}else {
				if (hpRatio > .9) {
					extra.println(p.getName() + " looks barely scratched.");
				}else {
					if (hpRatio > .7) {
						extra.println(p.getName() + " looks a little hurt.");
					}else {
						if (hpRatio > .5) {
							extra.println(p.getName() + " looks a bit damaged.");
						}else {
							if (hpRatio > .25) {
								extra.println(p.getName() + " looks moderately damaged.");
							}else {
								if (hpRatio > .1) {
									extra.println(p.getName() + " looks close to death.");
								}else {
									extra.println(p.getName() + " looks like they're dying.");
								}
							}
						}
					}
				}
				}
			//}
		if (attacker.hasSkill(Skill.BLOODTHIRSTY)) {
			attacker.addHp(attacker.getLevel());
		}
		if (attacker.hasSkill(Skill.HPSENSE) || defender.hasSkill(Skill.HPSENSE)) {
			extra.println(defender.getHp()+"/" + defender.getMaxHp() );
		}
		if (defender.hasSkill(Skill.MIMIC_CHEST)) {
			if (extra.chanceIn(1,2)){
				defender.getBag().swapRace(RaceFactory.getRace(extra.choose("hiding-mimic","open-mimic")));
				Networking.clearSide(1);
				defender.getBag().graphicalDisplay(1,defender);
			}
		}
		//TODO: bleedout death quotes
		if (attacker.hasEffect(Effect.I_BLEED)) {
			attacker.takeDamage(1);
		}
		if (attacker.hasEffect(Effect.BLEED)) {
			attacker.takeDamage(1);
		}
		if (attacker.hasEffect(Effect.MAJOR_BLEED)) {
			attacker.takeDamage(2);
		}
	}
	private void inflictWound(Person attacker2, Person defender2, int damage) {
		if (defender2.hasSkill(Skill.TA_NAILS) && extra.randRange(1,5) == 1) {
			extra.print(" They shrug off the blow!");
		}else {
		defender2.inflictWound(attacker2.getNextAttack().getWound());
		extra.print(" " +attacker2.getNextAttack().getWound().active);
		switch (attacker2.getNextAttack().getWound()) {
		case CONFUSED:
			newTarget = true;
			break;
		case SLICE: case DICE:
			attacker2.advanceTime(10);
			break;
		case HACK: case TAT:
			defender2.takeDamage(damage/10);
			break;
		case CRUSHED:
			defender2.takeDamage((int)attacker2.getNextAttack().getTotalDam()/10);
			break;
		}}
	}


	private void handleMagicSpell(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		extra.println(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
		if  (att.getSkill() == Skill.ELEMENTAL_MAGE) {
		def.burn(def.getFire(att.getSlot())*(att.getSharp()/100),att.getSlot());
		
		defender.advanceTime(-((att.getPierce()/100)*defender.getTime()*def.getFreeze(att.getSlot())));
		
		defender.takeDamage((int)((att.getBlunt())*def.getShock(att.getSlot())));
		}
		if  (att.getSkill() == Skill.DEATH_MAGE) {
			defender.getNextAttack().wither(att.getSharp()/100);
			if (!defender.hasSkill(Skill.LIFE_MAGE)) {
			defender.takeDamage((int)((att.getBlunt())));}
			int i = 0;
			while (i < att.getPierce()) {
				defender.addEffect(Effect.BURNOUT);
				i++;
			}
			}
		if(att.getSkill() == Skill.ARMOR_MAGE) {
			off.restoreArmor(((double)att.getSharp())/100);
		}
		if(att.getSkill() == Skill.ILLUSION_MAGE) {
			newTarget = true;
		}
	}


	private void setAttack(Person manOne, Person manTwo) {
		//try {
		manOne.setAttack(AIClass.chooseAttack(manOne.getStance().part(manOne, manTwo),manOne.getIntellect(),this,manOne,manTwo));}
		/*catch (java.lang.ArrayIndexOutOfBoundsException e){
	
			int i = 0;
			i++;
			
		}
		
	}*/
}
