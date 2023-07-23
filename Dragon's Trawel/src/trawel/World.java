package trawel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import trawel.time.ContextLevel;
import trawel.time.ContextType;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class World extends TContextOwner{

	private static final long serialVersionUID = 1L;
	private ArrayList<Island> islands;
	private int xSize;
	private int ySize;
	private Town startTown;
	private String name;
	
	private transient Map<String,PrintEvent> printerMap;
	private List<String> printerLabels;
	private List<PrintEvent> printers;

	//private ArrayList<BardSong> bardSongs;
	private ArrayList<Person> deathCheaters;
	private Calender calender = new Calender();
	private float minLata, maxLata, minLonga, maxLonga;
	
	private transient ReentrantLock debtLock;
	
	public World(int x, int y, String _name,float minLata, float minLonga) {
		xSize = x;
		ySize = y;
		islands = new ArrayList<Island>();
		//bardSongs = new ArrayList<BardSong>();
		deathCheaters = new ArrayList<Person>();
		name = _name;
		this.minLata = minLata;
		this.maxLata = minLata+y/WorldGen.unitsInLata;
		this.minLonga = minLonga;
		this.maxLonga = minLonga+x/WorldGen.unitsInLonga;
		
		printerLabels = new ArrayList<String>();
		printers = new ArrayList<PrintEvent>();
	}
	
	public void addIsland(Island t) {
		islands.add(t);
	}

	public ArrayList<Island> getislands() {
		return islands;
	}
	
	public void generate() {
		int i = 0;
		while (i < 5) {
			//Island island = new Island();
			//island.generate();
			//islands.add(island);
			i++;
		}
		
		//look for connections, should be careful to not double up on connections, different types is fine though
	}

	public int getXSize() {
		return xSize;
	}

	public void setXSize(int xSize) {
		this.xSize = xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public void setYSize(int ySize) {
		this.ySize = ySize;
	}

	public Town getStartTown() {
		return startTown;
	}

	public void setStartTown(Town startTown) {
		this.startTown = startTown;
	}
	
	private void displayMap1() {
		String[][] map = new String[xSize][ySize];
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				map[i][j] = " ";
			}
		}
		for(Island i: islands) {
			for (Town t: i.getTowns()) {
				if (Player.player.getLocation() != t) {
				map[t.getLocationY()-1][t.getLocationX()-1] = Integer.toString(t.getTier()%10);		
				}else {
					map[t.getLocationY()-1][t.getLocationX()-1] = "x";
				}
			}
		}
		
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				extra.print(map[i][j]);
			}
			extra.println();
		}
	}

	public Town getRandom(int level) {
		ArrayList<Town> list = new ArrayList<Town>();
		for(Island i: islands) {
			for (Town t: i.getTowns()) {
				if (t.getTier() == level) {
					list.add(t);
				}
				
				}
			}
		if (list.size() > 0) {
		return list.get((int)(list.size()*Math.random()));}else {
			if (level > 0) {
			return getRandom(level-1);}else {return startTown;}
		}
	}
	
	public void addDeathCheater(Person p) {
		deathCheaters.add(p);
	}
	
	public void removeDeathCheater(Person p) {
		deathCheaters.remove(p);
	}
	
	public Person getDeathCheater(int level) {
		ArrayList<Person> list = new ArrayList<Person>();
		deathCheaters.stream().filter(p -> p.getLevel() == level).forEach(list::add);
		if (list.size() == 0) {
			return null;
		}
		return extra.randList(list);
		
		
	}

	public Calender getCalender() {
		return calender;
	}

	public float getMinLata() {
		return minLata;
	}

	public float getMaxLata() {
		return maxLata;
	}

	public float getMinLonga() {
		return minLonga;
	}

	public float getMaxLonga() {
		return maxLonga;
	}

	//TODO: worlds can't interact with each other right now, at all, because planes would need to handle it, but
	//we might be handling it from a thread and not know how to pass it around
	//NOTE: that means that the calling context for our own context call could be either the plane or null
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		calender.passTime(time,calling);
		for (Island t: islands) {
			timeScope.localEvents(t.contextTime(time, calling));
		}
		return null;
	}
	
	@Override
	public void reload() {
		debtLock = new ReentrantLock();
		timeScope = new TimeContext(ContextType.GLOBAL,this,true);//world is lazy
		timeSetup();
		for(Island i: islands) {
			i.reload();
		}
	}
	
	@Override
	public void prepareSave() {
		super.prepareSave();
		for(Island i: islands) {
			i.prepareSave();
		}
	}
	
	
	public double assumeDebt(double limit) {
		//try {
		assert debtLock != null;
		try {
			if (!debtLock.tryLock(50, TimeUnit.MILLISECONDS)) {
				return -1;
			}
		} catch (InterruptedException e) {
			return -1;
		}
		if (timeScope.getDebt() > .2) {//different threshold from hasDebt- lower
			double taken = Math.min(limit, timeScope.getDebt());
			assert debtLock.isHeldByCurrentThread();
			timeScope.assumeDebt(taken);
			return taken;
		}
		return 0;
		//}finally {
		//	if (debtLock.isHeldByCurrentThread()) {
		//		debtLock.unlock();
		//	}
		//}
	}
	
	/**
	 * needs to be called after assuming debt with a return of > 0
	 */
	public void freeLock() {
		debtLock.unlock();
	}

	public boolean hasDebt() {
		return timeScope.getDebt() > 1;//needs at least an hour of debt to bother
	}
	
	@Override
	public void consumeEvents(List<TimeEvent> list) {
		// TODO Auto-generated method stub
	}
	@Override
	public ContextLevel contextLevel() {
		return ContextLevel.WORLD;
	}

	public void addPrintEvent(String string, PrintEvent e) {
		printerMap.put(string, e);
		printerLabels.add(string);
		printers.add(e);
	}

	public boolean getAndPrint(String string) {
		PrintEvent printer = printerMap.getOrDefault(string, null);
		if (printer !=null) {
			printer.print();
			return true;
		}
		return false;
	}
	

}
