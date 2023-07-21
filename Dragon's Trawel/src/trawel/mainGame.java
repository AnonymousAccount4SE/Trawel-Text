package trawel;
import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import scimech.combat.MechCombat;
import scimech.handlers.SaveHandler;
import scimech.mech.Mech;
import scimech.people.Pilot;
import scimech.units.mechs.DebugMech;
import scimech.units.mechs.Dynamo;
import scimech.units.mechs.Hazmat;
import scimech.units.mechs.Musketeer;
import scimech.units.mechs.Packrat;
import scimech.units.mechs.Pirate;
import scimech.units.mechs.Pyro;
import scimech.units.mechs.Swashbuckler;
import trawel.factions.HostileTask;
import trawel.quests.QuestReactionFactory;
import trawel.townevents.TownFlavorFactory;
/**
 * 
 * @author Brian Malone
 * 2/5/2018
 * The main method.
 * calls all the other classes, and holds the defining gameplay types and inputs.
 */
public class mainGame {
	//public static final boolean bumpEnabled = true;//a boolean for an update

	public static final String VERSION_STRING = "v0.7.4";


	//instance variables
	public static Scanner scanner = new Scanner(System.in);
	
	public static int starting_level = 1;
	public static boolean debug = false;
	public static boolean inEclipse = false;
	public static boolean autoConnect = false;
	public static boolean noDisconnect = false;
	public static boolean noThreads = true;
	public static boolean permaNoThreads = false;

	public static boolean GUIInput = true;
	
	public static Story story = null;
	
	public static Date lastAutoSave = new Date();
	
	public static boolean noBards = true;
	
	public static boolean doAutoSave = true;
	
	public static boolean logStreamIsErr = false;
	

	public static PrintStream logStream;

	public static int attackType = 2;
	
	public static boolean delayWaits = false;
	
	private static boolean finalSetup1 = false;
	private static boolean basicSetup1 = false;
	
	public static void mainMenu() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				extra.changePrint(false);
				Networking.sendStrong("Discord|desc|Main Menu|");
				Networking.send("Visual|MainMenu|");
				List<MenuItem> mList = new ArrayList<MenuItem>();
				/*mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Choose a Game Mode:";
					}}
				);*/
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Play Game";
					}

					@Override
					public boolean go() {
						Networking.clearSides();
						gameTypes();
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Basic Tutorial";
					}

					@Override
					public boolean go() {
						extra.println("Thanks for playing Trawel! Here's a few tips about learning how to play:");
						extra.println("All of Trawel proper, and most of the side games, only require inputing a number between 1 and 9.");
						extra.println();
						extra.println("There are a few games in Trawel, but the one simply called 'Trawel' has the following advice:");
						extra.println("Always be on the lookout for better gear than you currently have. Your power level is largely determined by how powerful your gear is- not just it's level.");
						extra.println("There are three primary attack and defense types, sharp, blunt, and pierce.");
						extra.println("Sharp is edged and cutting. Swords are good at it, and chainmail is good at defending from it. Some materials are softer, like Gold, and thus bad at it.");
						extra.println("Blunt is heavy and crushing. Maces are good at it, and gold is good at defending from it- and also dealing it.");
						extra.println("Pierce is pointy and puncturing. Spears are good at it, and metals are better at defending from it.");
						extra.println("If you're feeling tactical, you can read your opponent's equipment to try to determine which type they are weak to!");
						extra.println("As you play the game, you'll get a grasp of the strengths and weaknesses of varying materials and weapons. It's part of the fun of the game!");
						extra.println("Attacks have a delay amount and a hitchance, along with damage types.");
						extra.println("Delay is how long it takes for the attack to happen- it can be thought of how 'slow' the attack is, so lower is better.");
						extra.println("Hitchance is the opposite- higher is more accurate. However, it is not a percent chance to hit, as it does not account for the opponent's dodge, which can change over time.");
						extra.println("Enchantments can be both good and bad, so keep an eye out for gear that has low stats but boosts overall stats a high amount- or gear that makes you much weaker!");
						extra.println("When looting equipment, you are shown the new item, then your current item, and then the stat changes between the two- plus for stat increases, minus for stat decreases. The difference will not show stats that remain the same.");
						extra.println("Value can be a good rough indicator of quality, but it does not account for the actual effectiveness of the item, just the rarity and tier.");
						extra.println("For example, gold (a soft metal) sharp/piercing weapons are expensive but ineffective.");
						extra.println("When in combat, you will be given 3 (by default, skills and circumstance may change this) random attacks ('opportunities') to use your weapon.");
						extra.println("Pay close attention to hit, delay, and sbp (sharp, blunt, pierce) damage.");
						extra.println("More simply: Higher is better, except in the case of delay.");
						extra.println("Well, you made it through bootcamp. Have fun!");
						extra.println("-realDragon");
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Load Trawel Save";
					}

					@Override
					public boolean go() {
						Networking.clearSides();
						for (int i = 1; i < 9; i++) {
							extra.println(i+" slot: "+WorldGen.checkNameInFile(""+i));
						}
						extra.println("9 autosave: "+WorldGen.checkNameInFile("auto"));
						int in = extra.inInt(9);
						WorldGen.load(in == 9 ? "auto" : in+"");
						boolean runit;
						try {
							Player.player.getPerson();
							runit = true;
						}catch (Exception e) {
							runit = false;
						}
						 if (runit) {
							 adventureBody();
						 }
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Credits";
					}

					@Override
					public boolean go() {
						credits();
						return false;
					}});
				
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "GitHub (opens in browser)";
					}

					@Override
					public boolean go() {
						openWebpage("https://github.com/realDragon11/Trawel-Text");
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Advanced (beta)";
					}

					@Override
					public boolean go() {
						//TODO: just reconnecting for now, need to have accessibility options later
						advancedOptions();
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Exit";
					}

					@Override
					public boolean go() {
						System.exit(0);
						return false;
					}});
				
				return mList;
			}
		});
	}
	
	private static void advancedOptions() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "These first options deal with reconnecting to Trawel Graphical, and only apply to the Steam version. They only work on the command line, otherwise you may have to restart or use the command line to finish reconnecting.";
					}}
				);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Quick Reconnect";
					}

					@Override
					public boolean go() {
						Networking.connect(6510); 
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Full Reconnect";
					}

					@Override
					public boolean go() {
							extra.println("Port?"); Networking.connect(extra.inInt(65535)); 
						return true;
					}});
				return mList;
			}
		});
	}
	
	
	private static void credits() {
		extra.println("Made by Brian 'dragon' Malone");
		extra.println("With thanks to the GameMaker discords");
		extra.println("Achievement icons can be found on game-icons.net");
		extra.println("Music by manicInsomniac");
		extra.println("Art:");
		extra.println("Character, armor, and weapon art by SmashCooper and Duster. Background art by Damrok and he-who-shall-not-be-named");
		extra.println("Sounds:");
		extra.println("Stock Media provided by Soundrangers / FxProSound / SoundIdeasCom / PrankAudio / hdaudio / agcnf_media / sounddogs / AbloomAudio / Yurikud / SoundMorph => through Pond5");
	}
	
	private static void baseSetup1() {
		if (!basicSetup1) {
			new MaterialFactory();
			new RaceFactory();
			new TargetFactory();
			new StyleFactory();
			new Oracle().load();
			new TauntsFactory();
			new BookFactory();
			new BumperFactory();
			new WeaponAttackFactory();
			new TownFlavorFactory();
			new QuestReactionFactory();
			WorldGen.initDummyInvs();
			story = new StoryNone();
			basicSetup1 = true;
		}
	}

	
	private static void gameTypes() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Choose a Game Mode:";
					}}
				);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Trawel Quickstart (base game, recommended)";
					}

					@Override
					public boolean go() {
						adventure1(false,false,false,false);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Trawel Slowstart (base game)";
					}

					@Override
					public boolean go() {
						adventure1(false,true,true,true);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "rTrawel (experimental jrpg, prototype, nongraphical)";
					}

					@Override
					public boolean go() {
						rtrawel.TestRunner.run();
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "SciMechs (experimental mech game, prototype, nongraphical)";
					}

					@Override
					public boolean go() {
						SaveHandler.clean();
						while (true) {
							extra.println("Choose your mechs");
							List<Mech> mechs = mechsForSide(true);
							extra.println("Save mechs?");
							if (extra.yesNo()) {
								SaveHandler.clean();
								SaveHandler.imprintMechs(mechs);
								SaveHandler.save();
							}
							extra.println("Choose their mechs");
							mechs.addAll(mechsForSide(false));
							
							MechCombat mc = new MechCombat(mechs);
							mc.go();
							
							extra.println(mc.activeMechs.get(0).playerControlled == true ? "You win!" : "You lose!"+ "\n Would you like the quit?");
							if (extra.yesNo()) {
								return true;
							}
						}
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "various tests";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator(){

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> mList = new ArrayList<MenuItem>();
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Cheat Trawel";
									}

									@Override
									public boolean go() {
										adventure1(true,false,false,false);
										return true;
									}});
								mList.add(new MenuLine() {

									@Override
									public String title() {
										return "Graphical Tests:";
									}}
								);
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Model Mode";
									}

									@Override
									public boolean go() {
										modelMode();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Time Test";
									}

									@Override
									public boolean go() {
										Calender.timeTest();
										return true;
									}});
								mList.add(new MenuLine() {

									@Override
									public String title() {
										return "Backend Tests:";
									}}
								);
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Power Metrics (creates csv)";
									}

									@Override
									public boolean go() {
										baseSetup1();
										try {
											WeaponAttackFactory.weaponMetrics();
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										}
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Rarity Metrics (creates csv)";
									}

									@Override
									public boolean go() {
										baseSetup1();
										try {
											Weapon.duoRarityMetrics();
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										}
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Save Test";
									}

									@Override
									public boolean go() {
										saveTest();
										return true;
									}});
								return mList;
							}
						});
						return true;
					}});
				return mList;
			}
			});
	}
	
	private static void saveTest() {
		baseSetup1();
		for (int j = 0;j < 10;j++) {
			extra.println("try: "+j);
			for (int i = 1;i<9;i++ ) {
				extra.println("slot: "+i);
				WorldGen.eoano();
				Person p = new Person(1);
				p.setPlayer();
				WorldGen.plane.setPlayer(new Player(p));
				WorldGen.save(i+"");
				WorldGen.load(1+"");
			}
			//WorldGen.conf.getClassRegistry().dragonDump();
		}
	}

	private static List<Mech> curMechs;

	
	private static List<Mech> mechsForSide(boolean side){
		extra.menuGoPaged(new MenuGeneratorPaged(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "load mechs (??? complexity)";
					}

					@Override
					public boolean go() {
						curMechs = SaveHandler.exportMechs();
						for (Mech m: curMechs) {
							m.playerControlled = side;
							m.swapPilot(new Pilot());
						}
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "debug mechs (70x2 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = debugMechs(side);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "three musketeers (80x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = threeMusketeers(side);
						return true;
					}});
				
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "pirate squad (80x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = pirateSquad(side);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "science squad (120x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = scienceSquad(side);
						return true;
					}});
				return mList;
			}});
		
		return curMechs;
	}
	
	private static List<Mech> debugMechs(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new DebugMech(side));
		mechs.add(new DebugMech(side));
		return mechs;
	}
	
	private static List<Mech> threeMusketeers(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Musketeer(side));
		mechs.add(new Musketeer(side));
		mechs.add(new Musketeer(side));
		return mechs;
	}
	
	private static List<Mech> pirateSquad(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Swashbuckler(side));
		mechs.add(new Packrat(side));
		mechs.add(new Pirate(side));
		return mechs;
	}
	
	private static List<Mech> scienceSquad(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Dynamo(side));
		mechs.add(new Pyro(side));
		mechs.add(new Hazmat(side));
		return mechs;
	}
	
	private static void modelMode() {
		baseSetup1();
		Person manOne;
		manOne = RaceFactory.makeOld(2);//new Person(starting_level,false,Race.RaceType.HUMANOID,null);
			 new Player(manOne);
			 Player.bag.swapWeapon(new Weapon(1,"shovel"));
			 //Player.bag.getHand().forceEnchantHit(0);
			 /*
			 player.bag.swapArmorSlot(new Armor(1,0,MaterialFactory.getMat("emerald")),0);
			 player.bag.swapArmorSlot(new Armor(1,1,MaterialFactory.getMat("emerald")),1);
			 player.bag.swapArmorSlot(new Armor(1,2,MaterialFactory.getMat("emerald")),2);
			 player.bag.swapArmorSlot(new Armor(1,3,MaterialFactory.getMat("emerald")),3);
			 player.bag.swapArmorSlot(new Armor(1,4,MaterialFactory.getMat("emerald")),4);*/
			 manOne.setPlayer();
			 Networking.sendStrong("Discord|desc|Character Select|");
			 Networking.charUpdate();
		
	}


	private void links() {
		while (true) {
			extra.println("1 Discord");
			extra.println("2 Github");
			extra.println("3 back");
			switch(extra.inInt(3)) {
			case 1: openWebpage("https://discord.gg/jsyqu7X");break;
			case 2: openWebpage("https://github.com/realDragon11/Trawel-Text");
			case 3: return;
			}
		}
	}
	
	/**
	 * Main method. Calls the main game.
	 * @param args (Strings)
	 */
	public static void main(String[] args) {
		new WorldGen();
		try {
			logStream = new PrintStream("log.txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		extra.println("Dragon's Trawel "+VERSION_STRING);
		extra.println(
				" ___________  ___  _    _ _____ _     \r\n" + 
				"|_   _| ___ \\/ _ \\| |  | |  ___| |    \r\n" + 
				"  | | | |_/ / /_\\ \\ |  | | |__ | |    \r\n" + 
				"  | | |    /|  _  | |/\\| |  __|| |    \r\n" + 
				"  | | | |\\ \\| | | \\  /\\  / |___| |____\r\n" + 
				"  \\_/ \\_| \\_\\_| |_/\\/  \\/\\____/\\_____/");
		for (String a: args) {
			if (a.toLowerCase().equals("autoconnect")){
				autoConnect = true;
				//extra.println("Please wait for the graphical to load...");
				//Networking.autoConnect();
			}
			if (a.toLowerCase().equals("noguiinput")){
				GUIInput = false;
			}
			if (a.toLowerCase().equals("nowarnings")) {
				logStreamIsErr = true;
				System.setErr(logStream);
			}
			if (a.toLowerCase().equals("ineclipse")) {
				inEclipse = true;
			}
			if (a.toLowerCase().equals("nodisconnect")) {
				noDisconnect = true;
			}
			if (a.toLowerCase().equals("nothreads")) {
				permaNoThreads = true;
			}
			
		}
		try {
		
		new Networking();
		if (autoConnect) {
			extra.println("Please wait for the graphical to load...");
			Networking.autoConnect();
		}
		
		mainMenu();
		}//catch (Exception )
		catch(Exception e) {
			System.setErr(System.out);
			e.printStackTrace();
			if (logStreamIsErr) {
				System.setErr(logStream);
			}
			extra.println("[jitter]Trawel has encountered an exception. Please report to realDragon. More details can be found on the command prompt.");
			System.out.println("ERROR: "+e.getMessage() != null ? (e.getMessage()) :"null" + e.getStackTrace());
			if (e.getMessage() == null) {
				main(args);
			} 
			if ((!e.getMessage().equals("invalid input stream error") && !e.getMessage().equals("No line found"))) {
			main(args);}
		}
		//
		
		logStream.close();
	}
	
	
	//instance methods
	
	
		/**
		 * The advanced combat, incorporating weapons and armors.
		 * @param first_man (Person)
		 * @param second_man (Person)
		 * @return winner (Person)
		 */
		public static Person CombatTwo(Person first_man,Person second_man, World w) {
				Person holdPerson;
				extra.println("Our first fighter is " + first_man.getName()  + "."); //+extra.choose("They hail from the","They come from the","They are from the","The place they call home is the") + " " + first_man.whereFrom() + ".");
				extra.println("Our second fighter is " + second_man.getName()  + "."); //+extra.choose("They hail from the","They come from the","They are from the","The place they call home is the") + " " + second_man.whereFrom() + ".");
				extra.println();
				if (first_man.isPlayer()) {
					first_man.getBag().graphicalDisplay(-1,first_man);
					second_man.getBag().graphicalDisplay(1,second_man);
					Networking.setBattle(Networking.BattleType.NORMAL);
				}
				new Combat(first_man,second_man, w);//////
				if (first_man.getHp() <= 0) {
					holdPerson = second_man;
					second_man = first_man;
					first_man = holdPerson;
				}
				//if (first_man.isPlayer()) {//now this combat only works with the player
				//extra.println("");
				
				if (!second_man.isPlayer() && first_man.getBag().getRace().racialType == Race.RaceType.HUMANOID && second_man.getBag().getRace().racialType == Race.RaceType.HUMANOID) {
					extra.println(first_man.getName() +" goes to loot " + second_man.getName() +".");
				AIClass.loot(second_man.getBag(),first_man.getBag(),first_man.getIntellect(),true);}else {
					if (first_man.isPlayer()) {
						for (DrawBane db: second_man.getBag().getDrawBanes()) {
							first_man.getBag().addNewDrawBane(db);
						}
					}
				}
				
				first_man.addXp(second_man.getLevel());
				
				if (second_man.isPlayer()) {
					first_man.addPlayerKill();
					Player.addXp(extra.zeroOut((int) (first_man.getLevel()*Math.floor((first_man.getMaxHp()-first_man.getHp())/(first_man.getMaxHp())))));
					die();
				}
				

					if (first_man.isPlayer()) {
					Player.player.wins++;
					if (Player.player.wins == 10) {
						Player.player.addTitle("duelist");
					}
					if (Player.player.wins == 50) {
						Player.player.addTitle("fighter");
					}
					if (Player.player.wins == 100) {
						Player.player.addTitle("warrior");
					}
					if (Player.player.wins == 1000) {
						Player.player.addTitle("master duelist");
					}
					second_man.addDeath();
					if (extra.chanceIn(second_man.getPlayerKills(), 5*(second_man.getDeaths()))) {
						w.addDeathCheater(second_man);//dupes don't happen since in this case the dupe is instantly removed in the wander code
						second_man.hTask = HostileTask.REVENGE;
					}
					}
					if (first_man.isPlayer() || second_man.isPlayer()) {
						Networking.setBattle(Networking.BattleType.NONE);
						Networking.clearSide(1);
					}
					first_man.clearBattleEffects();
					second_man.clearBattleEffects();
					
				return first_man;
		}
		
		/**
		 * if the fight doesn't involve the player, use the full method, with the World parameter
		 * @param first_man
		 * @param second_man
		 * @return
		 */
		public static Person CombatTwo(Person first_man,Person second_man) {
			return CombatTwo( first_man, second_man,Player.world);
		}
		
		public static ArrayList<Person> HugeBattle(ArrayList<Person>...people){
			return HugeBattle(Player.world,people);
		}
		
		public static ArrayList<Person> HugeBattle(World w, ArrayList<Person>... people){
			Combat battle = new Combat(w, people);
			Comparator<Person> levelSorter = new Comparator<Person>(){//sort in descending order
				@Override
				public int compare(Person arg0, Person arg1) {
					if (arg0.getLevel() == arg1.getLevel()) {
					return 0;}
					if (arg0.getLevel() < arg1.getLevel()) {
						return -1;
					}
					return 1;
				}
				
			};
			battle.killed.sort(levelSorter);
			battle.survivors.sort(levelSorter);
			
			for (Person surv: battle.survivors){
				surv.clearBattleEffects();
				surv.addXp(Math.min(surv.getLevel(),battle.killed.get(0).getLevel()));
				for (Person kill: battle.killed) {
					if (kill.isPlayer() || surv.getBag().getRace().racialType != Race.RaceType.HUMANOID || kill.getBag().getRace().racialType != Race.RaceType.HUMANOID) {
						if (surv.isPlayer()) {
							for (DrawBane db: kill.getBag().getDrawBanes()) {
								surv.getBag().addNewDrawBane(db);
							}
						}
						continue;}else {
					AIClass.loot(kill.getBag(),surv.getBag(),surv.getIntellect(),false);}
				}
			}
			
			int gold = 0;
			for (Person kill: battle.killed) {
				kill.clearBattleEffects();
				if (kill.isPlayer()) {
					Networking.setBattle(Networking.BattleType.NONE);
					Networking.clearSide(1);
					die();
					continue;}
				gold += kill.getBag().getGold();
				for (int i = 0;i<5;i++) {
					gold +=kill.getBag().getArmorSlot(i).getCost();
				}
				gold+=kill.getBag().getHand().getCost();
				
			}
			
			for (Person surv: battle.survivors){
				surv.getBag().addGold(gold/battle.survivors.size());
				if (surv.isPlayer()) {
					Networking.setBattle(Networking.BattleType.NONE);
					Networking.clearSide(1);
				}
			}
			
			return battle.survivors;
		}
		
		public static void adventureBody() {
			lastAutoSave = new Date();
			while(Player.player.isAlive()) {
				if (doAutoSave && (new Date().getTime()-lastAutoSave.getTime() > 1000*60*2)) {
					extra.println("Autosaving...");
					WorldGen.plane.prepareSave();;
					WorldGen.save("auto");
					lastAutoSave = new Date();
				}
				Player.player.getLocation().atTown();
				globalPassTime();
			}
			extra.println("You do not wake up.");
		}
		
		/**
		 * note that some events, like the player generating gold, ignore normal restrictions
		 */
		public static void globalPassTime() {
			double time = Player.popTime();
			if (time > 0) {
				WorldGen.plane.advanceTime(time);
			}
		}
		
		/*
		private static Player randPerson(boolean printIt, boolean choice) {
			Person manOne, manTwo;
			Player player;
			while (true) {
				 manOne = new Person(starting_level,false,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true);
				 manTwo = new Person(starting_level,false,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true);
				 extra.changePrint(!printIt);
				 manOne = CombatTwo(manOne,manTwo);
				 extra.changePrint(false);
				 manOne.displayStats();
				 player = new Player(manOne);
				 manOne.setPlayer();
				 Networking.sendStrong("Discord|desc|Character Select|");
				 //Networking.send("Visual|Race|" + manOne.getBag().getRace().name+  "|");
				 Networking.charUpdate();
				 if (!choice) {
					 break;
				 }
				 extra.println("Play as " + manOne.getName() + "?");
				
				 if (extra.yesNo()) {
					 break;
				 }
			}
			return player;
		}*/
		
		
		public static void adventure1(boolean cheaty, boolean displayFight, boolean rerolls, boolean advancedDisplay){
			baseSetup1();
			if (!finalSetup1) {
				randomLists.init();
				finalSetup1 = true;
			}
			Networking.sendStrong("Discord|desc|Character Select|");
			extra.println("Generating world...");
			World world = WorldGen.eoano();
			story = new StoryDeathWalker();
			Person manOne = null, manTwo;
			Player player;
			//
			while (manOne == null) {
				manOne = new Person(starting_level,false,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true);
				manOne.hTask = HostileTask.DUEL;
				Person manThree = manOne;
				manTwo = new Person(starting_level,false,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true);
				manTwo.hTask = HostileTask.DUEL;
				if (!displayFight) {
					extra.changePrint(true);
				}
				manOne = CombatTwo(manOne,manTwo);
				if (manOne == manThree) {
					StoryDeathWalker.killed = manTwo;
				}else {
					StoryDeathWalker.killed = manThree;
				}
				if (!displayFight) {
					extra.changePrint(false);
				}
				if (rerolls) {
					manOne.getBag().graphicalDisplay(1, manOne);
					if (advancedDisplay) {
						manOne.displayStats(false);
					}
					extra.println("Play as " + manOne.getName() +"?");
					if (extra.yesNo()) {
						break;
					}
				}
				//manOne.displayStats();
				Networking.clearSides();
			}
			Networking.clearSides();
			player = new Player(manOne);
			manOne.setPlayer();
			//Networking.send("Visual|Race|" + manOne.getBag().getRace().name+  "|");
			Networking.charUpdate();
			player.getPerson().setSkillPoints(0);
			Player.addSkill(Skill.BLOODTHIRSTY);
			Player.player.getPerson().addFighterLevel();
			story.storyStart();
			player.setLocation(world.getStartTown());
			WorldGen.plane.setPlayer(player);
			if (cheaty) {
				player.getPerson().addXp(9999);
			}
			//player.getPerson().playerLevelUp();
			adventureBody();
		}
		
		

		public static void openWebpage(String urlString) {
			try {
				Desktop.getDesktop().browse(new URL(urlString).toURI());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public static void die(String deathMessage) {
			Networking.sendStrong("StatUp|deaths|1|");
			story.onDeath();
			extra.println(deathMessage);
			story.onDeathPart2();
		}
		
		public static void die() {
			die(extra.choose("You rise from death...","You return to life.","You walk again!","You rise from the grave!","Death releases its hold on you."));
		}



}
