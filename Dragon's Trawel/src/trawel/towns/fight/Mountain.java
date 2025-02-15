package trawel.towns.fight;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.QRMenuItem;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;
import trawel.towns.services.Oracle;

public class Mountain extends Feature{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tier;
	private int explores;
	private int exhaust;
	private int time;
	
	@Override
	public QRType getQRType() {
		return QRType.MOUNTAIN;
	}
	
	public Mountain(String name, int tier) {
		this.tier = tier;
		this.name = name;
		explores = 0;
		exhaust = 0;
		this.tutorialText = "This is a mountain.";
		background_area = "mountain";
		background_variant = 1;
	}
	
	@Override
	public String getColor() {
		return extra.F_COMBAT;//unsure
	}
	
	@Override
	public void go() {
		Networking.setArea("mountain");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|mountain|Mountain|");
		MenuGenerator mGen = new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "explore";
					}

					@Override
					public boolean go() {
						explore();
						return false;
					}
				});
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "visit hot springs";
					}

					@Override
					public boolean go() {
						Player.player.getPerson().washAll();
						extra.println("You wash the blood off of your armor.");
						Player.bag.graphicalDisplay(-1,Player.player.getPerson());
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "exit";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}};
		
		
		extra.menuGo(mGen);
			
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (exhaust > 0) {
			exhaust--;
		}
		
		this.time += time;
		if (this.time > 12+(extra.getRand().nextInt(30))) {
			cleanTown();
			this.time = 0;
		}
		return null;
	}
	
	private void cleanTown() {
		for (SuperPerson peep: town.getOccupants()) {
			Agent a = (Agent)peep;
			a.getPerson().washAll();
		}
	}
	
	public void explore(){
		explores++;
		exhaust++;
		if (explores == 10) {
			Player.player.addTitle(this.getName() + " wanderer");
		}
		if (explores == 50) {
			Player.player.addTitle(this.getName() + " explorer");
		}
		if (explores == 100) {
			Player.player.addTitle(this.getName() + " guide");
		}
		if (exhaust > 10) {
			if (!extra.chanceIn(1,(int)(exhaust/3))) {
				dryMountain();
				return;
			}
		}
		switch (extra.randRange(1,11)) {
		case 1: rockSlide() ;break;
		case 2: ropeBridge() ;break;
		case 3: goldGoat() ;break;
		case 4: mugger1();break;
		case 5: mugger2();break;
		case 6: mugger3();break;
		case 7: wanderingDuelist();break;
		case 8: oldFighter();break;
		case 9: goldRock();break;
		case 10: findEquip();break;
		case 11: vampireHunter();break;
		}
		Player.addTime(.5);
		Networking.clearSide(1);
	}
	
	private void rockSlide() {
		extra.println("Some rocks start falling down the mountain!");
		extra.println("1 duck and cover");
		extra.println("2 dodge");
		extra.println("3 do nothing");
		switch (extra.inInt(3)) {
		case 1: 
			if (Player.bag.getBluntResist() > tier * 30) {
				extra.println("You survive the rockslide.");
				Player.addXp(tier);
			}else {
				mainGame.die();
			}
			;break;
		case 2: 
			if (Player.bag.getDodge() > 1) {
				extra.println("You survive the rockslide.");
				Player.addXp(tier);
			}else {
				mainGame.die();
			}
			;break;
		case 3:mainGame.die();break;
		
		}
	}
	
	
	private void ropeBridge() {
		extra.println("You come across a rope bridge. Cross it?");
		if (extra.yesNo()) {
			extra.println("You cross the bridge.");
		}else {
			extra.println("You don't cross the bridge.");
		}
	}
	
	private void goldGoat() {
		extra.println("You spot a bag of "+World.currentMoneyString()+" being carried by a mountain goat! Chase it?");
		Boolean result = extra.yesNo();
		extra.linebreak();
		if (result) {
			if (Math.random() > .5) {
				extra.println(extra.PRE_RED+"A fighter runs up and calls you a thief before launching into battle!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getMugger(tier));
				if (winner == Player.player.getPerson()) {
					int gold = extra.randRange(15,20)*tier;
					extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
					Player.addGold(gold);
				}else {
					extra.println("They take the "+World.currentMoneyString()+" sack and leave you rolling down the mountain...");
				}
			}else {
				int gold = extra.randRange(5,10)*tier;
				extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
				Player.addGold(gold);
			}
		}else {
			extra.println("You let the goat run away...");
		}
	}
	
	private void mugger2() {
		extra.println(extra.PRE_RED+"You see a mugger charge at you! Prepare for battle!");
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),  RaceFactory.getMugger(tier));
		if (winner == Player.player.getPerson()) {
		}else {
			extra.println("They fumble through your bags!");
			extra.println(Player.loseGold(50*tier,true));
		}
	}
	
	private void mugger1() {
		extra.println(extra.PRE_RED+"You see someone being robbed! Help?");
		Person robber =  RaceFactory.getMugger(tier);
		robber.getBag().graphicalDisplay(1, robber);
		Boolean help = extra.yesNo();
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),robber);
	
		if (winner == Player.player.getPerson()) {
			int gold = extra.randRange(tier,10*tier);
			extra.println("They give you a reward of " +World.currentMoneyDisplay(gold) + " in thanks for saving them.");
			Player.addGold(gold);
		}else {
			extra.println("They steal from your bags as well!");
			extra.println(Player.loseGold(50*tier,true));
		}
		}else {
			extra.println("You walk away.");
		}
	}
	
	
	private void mugger3() {
		
		extra.println(extra.PRE_RED+"You see a toll road keeper. Mug them for their gold?");
		Person robber = RaceFactory.getPeace(tier);
		robber.getBag().graphicalDisplay(1, robber);
		Boolean help = extra.yesNo();
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(), robber);
		int want = tier*5 + extra.randRange(0,5);
		if (winner == Player.player.getPerson()) {
			want*=extra.randRange(2,4);
			want += extra.randRange(0,5);
			extra.println("You find " + World.currentMoneyDisplay(want) + " in tolls.");
			Player.addGold(want);
		}else {
			
			int lost = Player.loseGold(want);
			if (lost == -1) {
				extra.println("They mutter something about freeloaders.");
			}else {
				if (lost < want) {
					extra.println("They make you pay the toll, but you don't have enough. (-"+World.currentMoneyDisplay(lost)+")");
				}else {
					extra.println("They make you pay the toll. (-"+World.currentMoneyDisplay(lost)+")");
				}
			}
		}
		}else {
			extra.println("You walk away.");
		}
	}
	
	private void wanderingDuelist() {
		extra.println(extra.PRE_RED+"A duelist approaches and challenges you to a duel. Accept?");
		Person robber = RaceFactory.getDueler(tier+1);
		robber.getBag().graphicalDisplay(1, robber);
		Boolean help = extra.yesNo();
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(), robber);
	
		if (winner == Player.player.getPerson()) {
			extra.println("You have won the duel!");
		}else {
			extra.println("They mutter a poem for your death.");
		}
		}else {
			extra.println("You walk away. They sigh.");
		}
	}
	
	private void oldFighter() {
		Person robber = RaceFactory.makeOld(tier+2);
		robber.getBag().graphicalDisplay(1, robber);
		while (true) {
		extra.println("You come across an old fighter, resting on a rock.");
		extra.println("1 Leave");//DOLATER: fix menu
		extra.println("2 "+extra.PRE_RED+"Attack them.");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the fighter alone");return;
		case 2: extra.println("You attack the fighter!");mainGame.CombatTwo(Player.player.getPerson(), robber);return;
		case 3: extra.println("The old fighter turns and answers your greeting.");
		while (true) {
		extra.println("What would you like to ask about?");
		extra.println("1 tell them goodbye");
		extra.println("2 ask for a tip");
		extra.println("3 this mountain");
		int in = extra.inInt(3);
		switch (in) {
			case 1: extra.println("They wish you well.") ;break;
			case 2: Oracle.tip("old");;break;
			case 3: extra.println("\"We are on " + this.getName() + ". Beware, danger lurks on these slopes.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}
	
	private void goldRock() {
		extra.println("You spot a solidified aether rock rolling down the mountain. Chase it?");
		Boolean result = extra.yesNo();
		extra.linebreak();
		if (result) {
			if (Math.random() > .5) {
				extra.println(extra.PRE_RED+"A fighter runs up and calls you a thief before launching into battle!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),  RaceFactory.getMugger(tier));
				if (winner == Player.player.getPerson()) {
					int aether = 100+extra.randRange(150*tier,300*tier);
					extra.println("You pick up " + aether + " aether!");
					Player.bag.addAether(aether);
				}else {
					extra.println("They take the aether rock and leave you rolling down the mountain...");
				}
			}else {
				int aether = 100+extra.randRange(100*tier,200*tier);
				extra.println("You pick up " + aether + " aether!");
				Player.bag.addAether(aether);
			}
		}else {
			extra.println("You let the rock roll away...");
		}
		if (Math.random() > .5) {
			this.rockSlide();
		}
	}
	
	private void findEquip() {
		extra.println("You find a rotting body... With their equipment intact!");
		AIClass.loot(RaceFactory.makeLootBody(tier).getBag(),Player.bag,Player.player.getPerson().getIntellect(),true,Player.player.getPerson());
	}
	
	private void vampireHunter() {
		extra.println(extra.PRE_RED+"A vampire hunter is walking around. Mug them?");
		Person robber = RaceFactory.getPeace(tier);
		robber.getBag().getDrawBanes().add(DrawBane.SILVER);
		robber.getBag().getDrawBanes().add(DrawBane.GARLIC);
		robber.getBag().graphicalDisplay(1, robber);
		Boolean help = extra.yesNo();
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(), robber);
	
		if (winner == Player.player.getPerson()) {
			extra.println("You killed them.");
		}else {
			extra.println("They mutter something about vampire attacks.");
		}
		}else {
			extra.println("You walk away. They warn you to be safe from vampire attacks.");
		}
	}
	
	private void dryMountain() {
		extra.println("You don't find anything. You think you may have exhausted this mountain, for now. Maybe come back later?");
	}
}
