import java.awt.Color;

public class Mine extends Feature {

	private Town town;
	private int size;
	private MineNode start;
	private int veinsLeft = 0;
	public Mine(String name,Town t, SuperPerson owner) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Mines have minerals for you to make profit off of.";
		generate();
		color = Color.RED;
		this.owner = owner;
	}
	@Override
	public void go() {
		Networking.setArea("mine");
		Networking.sendStrong("Discord|imagesmall|icon|Mine|");
		start.go();
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
	}
	
	public void generate() {
		start = new MineNode(size,town.getTier(),this);
	}
	
	public void addVein() {
		veinsLeft++;
	}
	public void removeVein() {
		veinsLeft--;
		if (veinsLeft == 0) {
			Networking.sendStrong("Achievement|miner1|");
		}
	}
	


}
