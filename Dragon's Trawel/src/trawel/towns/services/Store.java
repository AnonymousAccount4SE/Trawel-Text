package trawel.towns.services;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.personal.RaceFactory;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.Skill;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class Store extends Feature implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int type;
	private List<Item> items;
	private List<DrawBane> dbs;
	private double time;
	private int tier;
	private int buys;
	private float markup;
	
	public static int INVENTORY_SIZE = 5;

	private Store() {
		time = 0;
		tutorialText = "This is a store. You can buy stuff here.";
		color = Color.BLUE;
		markup = 1.5f;
	}
	
	public Store(Town t, int tier, int type) {
		this();
		town = t;
		this.generate(tier, type);
	}
	
	public Store(int tier) {
		this();
		type = extra.getRand().nextInt(7);//6 = general
		this.generate(tier, type);
	}
	public Store(int tier, int type) {
		this();
		this.generate(tier, type);
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void generate(int tier,int newType) {
		this.tier = tier;
		type = newType;
		switch (type) {
		case 0: name = extra.choose("hat","headwear","heads and hair");break;
		case 1: name = extra.choose("gloves","handwear","hand protection","mitten");break;
		case 2: name = extra.choose("chestpiece","bodywear","chest protector");break;
		case 3: name = extra.choose("pants","legwear","leg protector","trouser","pantaloon");break;
		case 4: name = extra.choose("boot","footwear","cobbler","feet protection");break;
		case 5: name = extra.choose("weapon","arms","armament","war");break;
		case 6: name = extra.choose("general","flea","convenience","trading","super");break;
		case 7: name = extra.choose("race","species");break;
		case 8: name = extra.choose("drawbane","lure");break;
		case 9: name = extra.choose("witch","potion");break;
		}
		name += " " + extra.choose("store","market","shop","post","boutique","emporium","outlet","center","mart","stand");
		if (type < 8) {
			items = new ArrayList<Item>();
			if (type < 5) {
				for (int j = 0;j < 5;j++) {
					items.add(new Armor(Math.max(tier+extra.getRand().nextInt(6)-2,1),type));
				}
			}
			if (type == 5) {
				for (int j = 0;j < 5;j++) {
					items.add(Weapon.genMidWeapon(Math.max(tier+extra.getRand().nextInt(6)-2,1)));
				}
			}
			if (type == 6) {
				for (int j = 0;j < 5;j++) {
					Random rand = extra.getRand();
					if (rand.nextFloat() > .5f) {
						items.add(Weapon.genMidWeapon(Math.max(tier+rand.nextInt(6)-2,1)));
					}else {
						items.add(new Armor(Math.max(tier+rand.nextInt(6)-2,1),rand.nextInt(5)));
					}
				}
			}
			if (type == 7) {
				for (int j = 0;j < 5;j++) {
					items.add(RaceFactory.randRace(Race.RaceType.HUMANOID));
				}
			}
		}
		
		if (type == 8) {
			dbs = new ArrayList<DrawBane>();
			for (int j = 0;j < 5;j++) {
				dbs.add(randomDB());
			}
		}
		if (type == 9) {
			dbs = new ArrayList<DrawBane>();
			for (int j = 0;j < 5;j++) {
				dbs.add(randomPI());
			}
		}
	}
	
	//TODO put these in tables elsewhere TODO
	
	public static DrawBane randomDB() {
		return extra.choose(DrawBane.MEAT,DrawBane.GARLIC,DrawBane.BLOOD,DrawBane.REPEL,DrawBane.CLEANER,extra.choose(DrawBane.PROTECTIVE_WARD,DrawBane.SILVER,extra.choose(DrawBane.SILVER,DrawBane.GOLD,DrawBane.VIRGIN)));
	}
	
	public static DrawBane randomPI() {
		return extra.choose(DrawBane.MEAT,DrawBane.BAT_WING,DrawBane.APPLE,DrawBane.CEON_STONE,DrawBane.MIMIC_GUTS,DrawBane.BLOOD);
	}
	
	private void serviceItem(int index) {
		Inventory bag = Player.player.getPerson().getBag();
		if (type == 8 || type == 9) {
			if (index == -1) {
				DrawBane sellItem = bag.discardDrawBanes(true);
				if (sellItem != null) {
					bag.addGold(sellItem.getValue());
					//dbs.remove(index);
				}
				
				return;
			}
			DrawBane db = dbs.get(index);
			int buyGold = db.getValue() * tier;
			if (bag.getGold() >= buyGold) {
				extra.println("Buy the "+ db.getName() + "? (" + buyGold + " gold)");
				if (extra.yesNo()) {
					DrawBane sellItem = bag.addNewDrawBane(db);
					if (sellItem != null) {
						bag.addGold(sellItem.getValue()-buyGold);
					}
					dbs.remove(index);
				}
			}else {
				extra.println("You cannot afford this item.");
			}
			return;
		}
		Item buyItem = items.get(index);
		if (!canSee(buyItem)) {
			return;
		}
		String itemType = buyItem.getType();
		Item sellItem = null;
		int slot = -1;
		if (itemType.contains("armor")) {
			slot = Integer.parseInt(itemType.replaceAll("armor",""));
			sellItem = bag.getArmorSlot(slot);
		}
		if (itemType.contains("weapon")) {
			sellItem = bag.getHand();
		}
		if (itemType.contains("race")) {
			sellItem = bag.getRace();
		}
		int sellGold = extra.zeroOut(sellItem.getCost());//the gold the item you are exchanging it for is worth
		int buyGold = (int)(buyItem.getCost()*markup);
		if (buyGold > (bag.getGold()+sellGold)) {
			extra.println("You can't afford this item!");
			return;
		}
		if (!AIClass.compareItem(sellItem,buyItem,-2,false)) {
			extra.println("You decide not to buy the item.");
			return;
		}
		this.addBuy();
		if (itemType.contains("armor")) {
			arraySwap(bag.swapArmorSlot((Armor)buyItem, slot),buyItem);
		}
		if (itemType.contains("weapon")) {
			arraySwap(bag.swapWeapon((Weapon)buyItem),buyItem);
		}
		if (itemType.contains("race")) {
			arraySwap(bag.swapRace((Race)buyItem),buyItem);
		}
		bag.setGold(bag.getGold()+sellGold-buyGold);
		extra.println("You complete the trade. "+ (sellGold-buyGold) + " gold.");
	}
	
	private void arraySwap(Item i,Item i2) {
		items.remove(i2);
		items.add(i);
	}
	
	public static boolean canSee(Item i) {
		if (Player.player.merchantLevel >= i.getLevel() || Player.player.getPerson().getLevel() > i.getLevel()) {
			return true;}else {
				return false;
			}
	}
	
	public void storeFront() {
		Networking.charUpdate();
		extra.println("You have " + Player.bag.getGold() + " gold.");
		int j = 1;
		extra.println(j + " examine all");j++;
		if (type == 8 || type == 9) {
			for (DrawBane i: dbs) {
				extra.println(j + " " + i.getName() + " - " + i.getFlavor() + " cost: " + (i.getValue()*tier));
				j++;
			}
		}else {
		for (Item i: items) {
			extra.print(j + " ");
			if (canSee(i)) {
			i.display(1,markup);}else {
				extra.println("They refuse to show you this item.");
			}
			j++;
		}}
		if (Player.hasSkill(Skill.RESTOCK)) {
		extra.println(j + " restock (" + tier*100 +" gold)" );
		j++;
		}
		if (type == 8) {
			extra.println(j+ " sell drawbane");
			j++;
		}
		extra.println(j + " Exit");
		int i = extra.inInt(j);
		j = 1;
		if (i == j) {//examine all
			if (type == 8 || type == 9) {
				for (int k = 0;k < dbs.size();k++) {
					serviceItem(k);
				}
			}else {
			for (int k = 0;k < items.size();k++) {
				serviceItem(k);
			}}
			storeFront();
			return;
		}
		
		j++;
		if (type == 8 || type == 9) {
			for (DrawBane it: dbs) {
				if (i == j) {
					serviceItem(i-2);
					storeFront();//bad way of staying in it, but easy to code
					return;
				}
				j++;
			}
				
					
		}else {
		for (Item it: items) {
			if (i == j) {
				serviceItem(i-2);
				storeFront();//bad way of staying in it, but easy to code
				return;
			}
			
			j++;
		}}
		if (Player.hasSkill(Skill.RESTOCK)) {
		if (i ==j) {
			this.restock();
		}j++;}
		if (type == 8) {
			if (i ==j) {
				serviceItem(-1);
			}j++;}
		if (i ==j) {
			return;
		}j++;
		storeFront();
	}
	

	private void restock() {
		if (this.type == 8 || type == 9) {
			for (int i = items.size()-1;i >= 0;i--) {
				dbs.remove(i);
			}
			for (int i = INVENTORY_SIZE;i > 0;i-- ) {
				addAnItem();
			}
			return;
		}
		for (int i = items.size()-1;i >= 0;i--) {
			items.remove(i);
		}
		for (int i = INVENTORY_SIZE;i > 0;i-- ) {
			addAnItem();
		}
		
	}
	
	@Override
	public void go() {
		Networking.setArea("shop");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|store|Store|");
		this.storeFront();
	}
	
	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		time += addtime;
		if (time > 12+(extra.getRand().nextInt(30))) {
			if (type != 7) {
			extra.offPrintStack();
			goShopping();
			extra.popPrintStack();}
			addAnItem();
			time = 0;
		}
		return null;//TODO: should probably make these events or something instead? idk, shouldn't have much issues
	}
	
	@Override
	public String getName() {
		return name;
	}
	public int getBuys() {
		return buys;
	}
	
	private void addBuy() {
		this.buys +=1;
		if (buys == 3) {
			Player.player.addTitle(this.getName() + " shopper");
		}
		if (buys == 10) {
			Player.player.addTitle(this.getName() + " frequent shopper");
		}
	}
	
	public void addAnItem() {
		if (type == 8) {
			if (dbs.size() >= INVENTORY_SIZE) {
				dbs.remove(extra.randList(dbs));}
			dbs.add(randomDB());
			return;
		}
		if (type == 9) {
			if (dbs.size() >= INVENTORY_SIZE) {
				dbs.remove(extra.randList(dbs));}
			dbs.add(randomPI());
			return;
		}
		if (items.size() >= INVENTORY_SIZE) {
			items.remove(extra.randList(items));}
			if (type < 5) {
					items.add(new Armor(Math.max(tier+extra.getRand().nextInt(6)-2,1),type));
			}
			if (type == 5) {
					items.add(Weapon.genMidWeapon(Math.max(tier+extra.getRand().nextInt(6)-2,1)));
			}
			if (type == 6) {
				Random rand = extra.getRand();
				if (rand.nextFloat() > .5f) {
					items.add(Weapon.genMidWeapon(Math.max(tier+rand.nextInt(6)-2,1)));
				}else {
					items.add(new Armor(Math.max(tier+rand.nextInt(6)-2,1),rand.nextInt(5)));
				}
			}
	}
	private void goShopping() {
		if (type == 8 || type == 9) {
			return;
		}
		for (SuperPerson peep: town.getOccupants()) {
			Agent a = (Agent)peep;
			Inventory bag = a.getPerson().getBag();
			ArrayList<Item> add = new ArrayList<Item>();
			ArrayList<Item> remove = new ArrayList<Item>();
			for (Item i: items) {
				if (AIClass.compareItem(bag,i,a.getPerson().getIntellect(),false)) {
					int goldDiff = i.getCost()-bag.itemCounterpart(i).getCost();
					if (goldDiff <= bag.getGold()){
					bag.addGold(-goldDiff);
					remove.add(i);
					add.add(bag.swapItem(i));
					}
				}
			}
			for (Item i: add) {
				items.add(i);
			}
			for (Item i: remove) {
				items.remove(i);
			}
		}
	}
}
