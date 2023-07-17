package trawel;
import java.awt.Color;
import java.util.List;

import trawel.time.ContextType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class Grove extends Feature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Town town;
	private int size;
	private GroveNode start;
	private transient TimeContext timeScope;
	public Grove(String name,Town t) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Explore groves to progress in level.";
		generate();
		color = Color.RED;
		background_area = "forest";
		background_variant = 1;
		timeScope = new TimeContext(ContextType.UNBOUNDED,this);
	}
	@Override
	public void go() {
		Networking.setArea("forest");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|grove|Grove|");
		start.go();
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		start.passTime(time);
		start.timeFinish();
	}
	
	public void generate() {
		start = new GroveNode(size,town.getTier(),this);
	}
	public Shape getShape() {
		return Shape.STANDARD;
	}
	
	public enum Shape{
		STANDARD;
	}

}
