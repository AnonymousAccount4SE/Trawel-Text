package trawel.towns.services;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.BarkManager;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.BasicSideQuest;
import trawel.quests.QBMenuItem;
import trawel.quests.QRMenuItem;
import trawel.quests.Quest;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

//sells booze which increases temp hp for a few fights,
//has a resident which changes with time
public class Inn extends Feature implements QuestBoardLocation{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tier;
	private int resident;
	private double timePassed;
	private int wins = 0;
	private String residentName;
	private int nextReset;
	private boolean playerwatch;
	
	private boolean canQuest = true;
	
	public ArrayList<Quest> sideQuests = new ArrayList<Quest>();

	
	private final static int RES_COUNT = 8;

	@Override
	public QRType getQRType() {
		return QRType.INN;
	}
	
	public Inn(String n, int t,Town twn, SuperPerson owner) {
		name = n;
		tier = t;
		town = twn;
		timePassed = extra.randRange(1,30);
		resident = extra.randRange(1,RES_COUNT);
		nextReset = extra.randRange(4,30);
		playerwatch = false;
		tutorialText = "Inns are a great place to buy beer and have various residents.";
		this.owner = owner;
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}
	
	@Override
	public void init() {
		try {
			while (sideQuests.size() < 3) {
				generateSideQuest();
			}
			}catch (Exception e) {
				canQuest = false;
			}
	}
	
	private void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		BasicSideQuest bsq = BasicSideQuest.getRandomSideQuest(this.getTown(),this);
		if (bsq != null) {
		sideQuests.add(bsq);
		}
	}

	@Override
	public void go() {
		Networking.setArea("inn");
		if (owner == Player.player && moneyEarned > 0) {
			extra.println("You take the " + moneyEarned + " in profits.");
			Player.addGold(moneyEarned);
			moneyEarned = 0;
		}
		Networking.sendStrong("Discord|imagesmall|inn|Inn|");
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "beer ("+tier+"gp)";
					}

					@Override
					public boolean go() {
						buyBeer();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return getResidentName();
					}

					@Override
					public boolean go() {
						goResident();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "bard";
					}

					@Override
					public boolean go() {
						extra.println("Silence reigns.");
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "backroom";
					}

					@Override
					public boolean go() {
						backroom();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "bathe (" +(tier*2)+" gp)";
					}

					@Override
					public boolean go() {
						bathe();
						return false;
					}
				});
				if (town.getOccupants().size() >=2){
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "watch duel (" + extra.format(nextReset-timePassed+1) + " hours)";
					}

					@Override
					public boolean go() {
						playerwatch = true; occupantDuel(); Player.addTime((nextReset-timePassed+1));
						mainGame.globalPassTime();
						return false;
					}
				});
				}
				mList.add(new MenuBack("leave"));
				return mList;
			}});
	}

	

	private void backroom() {
		Inn inn = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new QBMenuItem(q,inn));
				}
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timePassed += time;
		
		if (timePassed > nextReset) {
			moneyEarned +=tier*5*(nextReset/10);
			timePassed = 0;
			occupantDuel();
			resident = extra.randRange(1,RES_COUNT);
			nextReset = extra.randRange(4,30);
			if (canQuest) {this.generateSideQuest();}
		}
		return null;//TODO make inn time matter more
	}
	
	private void occupantDuel() {
		if (town.getOccupants().size() >=2){
			Agent agent1= ((Agent)town.getOccupants().get(0));
			Agent agent2= ((Agent)town.getOccupants().get(1));
			if (playerwatch == false) {extra.changePrint(true); }playerwatch =false;
			if (mainGame.CombatTwo(agent1.getPerson(),agent2.getPerson(),town.getIsland().getWorld()) == agent2.getPerson()) {
				town.getOccupants().remove(0);
			}else {
				town.getOccupants().remove(1);
			}
			extra.changePrint(false);
			
		  }
	}
	
	private void goResident() {
		switch (resident) {
		case 1: goOldFighter();break;
		case 2: goDancers();break;
		case 3: goOracle();break;
		default:
			if (town.getOccupants().size() == 0){
			barFight();
			}else {
			goAgent((Agent)town.getOccupants().get(0));}
			;break;
		}
	}
	
	private String getResidentName() {
		residentName = "resident: ";
		switch(resident) {
		case 1: residentName += "A group of old fighters";break;
		case 2: residentName += "A group of dancers";break;
		case 3: residentName += "An oracle.";break;
		default:
			if (town.getOccupants().size() == 0){residentName += "Open Bar";}else {
				Person p = ((Agent)town.getOccupants().get(0)).getPerson();
				residentName += p.getName()+ " (" +p.getLevel() +")";
			}
			
			;break;
		}
		return residentName;
		
	}
	
	private void goAgent(Agent agent) {
		extra.println("1 "+extra.PRE_RED+"fight");
		extra.println("2 chat");
		extra.println("3 leave");
		switch(extra.inInt(3)) {
		case 3: return;
		case 1: if (mainGame.CombatTwo(Player.player.getPerson(),agent.getPerson()) == Player.player.getPerson()) {
			town.getOccupants().remove(0);
			return;}else {break;}
		case 2:
			if (extra.chanceIn(1,2)) {
				BarkManager.getBoast(Player.player.getPerson(),true);
				//extra.println("You "+extra.choose("boast")+ " \"" + Player.player.getPerson().getTaunts().getBoast()+"\"");		
		}else {
			BarkManager.getTaunt(Player.player.getPerson());
				//extra.println("You "+extra.choose("taunt")+ " \"" + Player.player.getPerson().getTaunts().getTaunt()+"\"");				
		}
			if (extra.chanceIn(1,2)) {
				BarkManager.getBoast(agent.getPerson(), true);//extra.println(agent.getPerson().getName() + " "+extra.choose("boasts")+ " \"" + agent.getPerson().getTaunts().getBoast()+"\"");		
		}else {
			BarkManager.getTaunt(agent.getPerson());
				//extra.println(agent.getPerson().getName() + " "+extra.choose("taunts")+ " \"" + agent.getPerson().getTaunts().getTaunt()+"\"");				
		} 
			
			;break;
		}
			
			goAgent(agent);
		
	}

	private void buyBeer() {
		//resident = 4;//???????
		if (Player.getGold() >= 2*tier) {
			extra.println("Pay "+2*tier+" gold for a beer?");
			if (extra.yesNo()) {
				Player.player.getPerson().addBeer();
				moneyEarned +=tier;
				Player.addGold(-2*tier);
			}
			}else {
				extra.println("You can't afford that!");
			}
	}
	
	private void goOldFighter() {
		while (true) {
			extra.println("There's an old fighter here, at the inn.");
			extra.println("1 Leave");//DOLATER: fix menu
			extra.println("2 Chat with them");
			switch (extra.inInt(2)) {
			default: case 1: extra.println("You leave the fighter");return;
			case 2: extra.println("The old fighter turns and answers your greeting.");
			while (true) {
			extra.println("What would you like to ask about?");
			extra.println("1 tell them goodbye");
			extra.println("2 ask for a tip");
			extra.println("3 this inn");
			extra.println("4 "+extra.PRE_RED+" a duel");
			int in = extra.inInt(4);
			switch (in) {
				case 1: extra.println("They wish you well.") ;break;
				case 2: Oracle.tip("old");;break;
				case 3: extra.println("\"We are in " + this.getName() + ". It is pleasant here.\"");break;
				case 4: extra.println("You challenge the fighter!");
				Person p = RaceFactory.makeOld(tier+2);
				p.getBag().removeDrawBanes();
				mainGame.CombatTwo(Player.player.getPerson(),p);return;
			}
			if (in == 1) {
				break;
			}
			}
			}
		}
	}
	
	private void goDancers() {
		extra.println("There are some dancers dancing excellently.");
		extra.println("They put on a good show.");
	}
	
	private void goOracle() {
		extra.println("There's an oracle staying at the inn.");
		new Oracle("inn",tier).go();
	}
	
	private void barFight() {
		extra.println(extra.PRE_RED+"There is no resident, but there is room for a barfight... start one?");
		if (extra.yesNo()) {
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),RaceFactory.getDueler(this.tier));
			if (winner.isPlayer()) {
				wins++;
				if (Player.player.animalQuest == 3) {
					extra.println("Hi, I'm Micheal SnowDancer. I really like the cut of your gib.");
					extra.println("Tell you what. Win at epino arena and I'll give you a reward.");
					Player.player.animalQuest++;
				}
			if (wins == 3) {
				Player.player.addTitle(this.getName() + " barfighter");
			}
			if (wins == 5) {
				Player.player.addTitle(this.getName() + " barbrewer");
			}
			if (wins == 10) {
				Player.player.addTitle(this.getName() + " barmaster");
			}
			
		}}
	}

	@Override
	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
	
	private void bathe() {
		//resident = 4;//???
		if (Player.getGold() >= (tier*2)) {
			extra.println("Pay "+(tier*2)+" "+World.currentMoneyString()+" for a bath?");
			if (extra.yesNo()) {
				Player.player.getPerson().washAll();
				Player.addGold(-(tier*2));
			}
			}else {
				extra.println("You can't afford that!");
			}
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
