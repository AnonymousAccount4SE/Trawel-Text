package trawel.quests;

import trawel.MenuSelect;
import trawel.Player;
import trawel.extra;

public class QBMenuItem extends MenuSelect {

	public Quest q;
	public QuestBoardLocation i;
	
	public QBMenuItem(Quest q, QuestBoardLocation i) {
		this.q = q;
		this.i = i;
	}
	
	
	@Override
	public String title() {
		return "Accept Quest: " + q.name();
	}

	@Override
	public boolean go() {
		if (Player.player.sideQuests.size() >= 4) {
			extra.println("You have too many side quests already!");
		}else {
			Player.player.sideQuests.add(q);
			q.take();
			i.removeSideQuest(q);
		}
		return false;
	}

}