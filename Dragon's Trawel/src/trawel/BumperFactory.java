package trawel;
import java.util.ArrayList;
import java.util.List;

import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.Quest.TriggerType;


public class BumperFactory {
	
	public class Response{
		DrawBane db;
		double mag;
		public Response(DrawBane d, double mag) {
			db = d;
			this.mag = mag;
		}
	}
	public static ArrayList<Bumper> bumperList = new ArrayList<Bumper>();
	public static ArrayList<Bumper> shipList = new ArrayList<Bumper>();
	public BumperFactory(){
		Bumper b = new Bumper() {

			@Override
			public void activate(int level) {
				if (level >= 5 && extra.chanceIn(2,5)) {
					List<Person> list = new ArrayList<Person>();
					//int count = extra.randRange(1,3);
					list.add(RaceFactory.makeAlphaWolf(extra.zeroOut(level-1)+1));
					for (int i = 0;i < 3;i++) {
						list.add(RaceFactory.makeWolf(extra.zeroOut(level-3)+1));
					}

					extra.println(extra.PRE_RED+"A large pack of wolves ambush you!");
					List<Person> survivors = mainGame.HugeBattle(list,Player.list());
					if (survivors.contains(Player.player.getPerson())) {
						Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 4);
					}
				}else {
					if (level >= 3) {
						List<Person> list = new ArrayList<Person>();
						//int count = extra.randRange(1,3);
						for (int i = 0;i < 3;i++) {
							list.add(RaceFactory.makeWolf(extra.zeroOut(level-3)+1));
						}
	
						extra.println(extra.PRE_RED+"A pack of wolves descend upon you!");
						List<Person> survivors = mainGame.HugeBattle(list,Player.list());
						if (survivors.contains(Player.player.getPerson())) {
							Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 3);
						}
					}else {
						Person p = RaceFactory.makeWolf(level);
	
						extra.println(extra.PRE_RED+"A wolf attacks you!");
						if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
							Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 1);
						}
					}
				}
			}};
			b.responses.add(new Response(DrawBane.MEAT,5));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			//b.minAreaLevel = 3;//replaced with solowoofs
			bumperList.add(b);

			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeWolf(level);

					extra.println(extra.PRE_RED+"A wolf attacks you!");
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
						Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 1);
					}

				}};
		b.responses.add(new Response(DrawBane.MEAT,2));
		b.responses.add(new Response(DrawBane.NOTHING,.3));
		b.responses.add(new Response(DrawBane.REPEL,-10));
		
		
		bumperList.add(b);
		
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeFellReaver(level);
					
					extra.println(extra.PRE_RED+"A fell reaver appears!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.CEON_STONE,3));
			b.minAreaLevel = 5;
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeEnt(level);
					
					extra.println(extra.PRE_RED+"An ent appears!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.ENT_CORE,5));
			b.minAreaLevel = 3;
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeVampire(level);
					
					extra.println(extra.PRE_RED+"A vampire jumps from the shadows!");
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
							Player.player.questTrigger(TriggerType.CLEANSE,"vampire", 1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.BLOOD,3));
			b.responses.add(new Response(DrawBane.GARLIC,-8));
			b.responses.add(new Response(DrawBane.SILVER,-.5));
			b.minAreaLevel = 4;
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.getMugger(level);
					
					extra.println(extra.PRE_RED+"A thief charges you!");
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
						Player.player.questTrigger(TriggerType.CLEANSE,"bandit", 1);
				}
					
				}};
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBear(level);
					
					extra.println(extra.PRE_RED+"A bear attacks you!");
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
						Player.player.questTrigger(TriggerType.CLEANSE,"bear", 1);
				}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,4));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.HONEY,.7));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			b.minAreaLevel = 2;
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBat(level);
					
					extra.println(extra.PRE_RED+"A bat attacks you!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			bumperList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeUnicorn(level);
					
					extra.println(extra.PRE_RED+"A unicorn accosts you for holding the virgin captive!");
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
							Player.player.questTrigger(TriggerType.CLEANSE,"unicorn", 1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.VIRGIN,10));
			b.minAreaLevel = 8;
			bumperList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					List<Person> list = new ArrayList<Person>();
					int count = 5;//extra.randRange(4,5);
					for (int i = 0;i < count;i++) {
						list.add(RaceFactory.makeHarpy(extra.zeroOut(level-4)+1));}
					extra.println(extra.PRE_RED+"A flock of harpies attack!");
					List<Person> survivors = mainGame.HugeBattle(list,Player.list());
					if (survivors.contains(Player.player.getPerson())) {
						Player.player.questTrigger(TriggerType.CLEANSE,"harpy", count);
					}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,1.25));
			b.responses.add(new Response(DrawBane.SILVER,1.25));
			b.responses.add(new Response(DrawBane.GOLD,1.25));
			b.responses.add(new Response(DrawBane.REPEL,-1));
			b.minAreaLevel = 6;
			bumperList.add(b);
			
			//ships
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.getMugger(level);
					
					extra.println(extra.PRE_RED+"A pirate challenges you for your booty!");
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
						Player.player.questTrigger(TriggerType.CLEANSE,"bandit", 1);
				}
					
				}};
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			shipList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeDrudgerStock(level);
					
					extra.println(extra.PRE_RED+"A drudger attacks your ship!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			shipList.add(b);
			
		
	}
}
