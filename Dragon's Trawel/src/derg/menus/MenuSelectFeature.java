package derg.menus;

import java.awt.Color;

import trawel.extra;
import trawel.towns.Feature;
import trawel.towns.Lot;

public class MenuSelectFeature implements MenuItem {
	
	public Feature feature;

	@Override
	public String title() {
		if (feature.getName() == null) {
			return "nullname";
		}
		String append = "";
		if (feature instanceof Lot) {
			if (((Lot)feature).getConstructTime() != -1) {
				append += " ("+extra.F_WHOLE.format(((Lot)feature).getConstructTime())+" hours)";
			}
		}
		return extra.inlineColor(extra.colorMix(feature.getColor(),Color.WHITE,.5f))+extra.capFirst(feature.getName()) + append;
	}
	
	public MenuSelectFeature(Feature f) {
		feature = f;
	}
	/**
	 * 
	 * @return whether to go back or not
	 */
	@Override
	public boolean go() {
		feature.go();
		return true;
	}
	
	@Override
	public boolean canClick() {
		return true;
	}

	@Override
	public boolean forceLast() {
		return false;
	}

}
