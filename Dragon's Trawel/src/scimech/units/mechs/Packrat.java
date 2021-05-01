package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.ArtemisCannon;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.fixtures.Mortar;
import scimech.units.mounts.Blunderbuss;
import scimech.units.mounts.Broadside;
import scimech.units.mounts.Foil;
import scimech.units.systems.AblativeArmor;
import scimech.units.systems.CoolantRod;
import scimech.units.systems.FusionReactor;
import scimech.units.systems.MiniReactor;
import scimech.units.systems.Ramjet;
import trawel.randomLists;

public class Packrat extends Mech {

	public Packrat(boolean side) {
		playerControlled = side;
		complexityCap = 80;
		weightCap = 100;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
		
		Mount m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new Mortar());
		
		m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new APCannon());
		
		m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		
		for (int i = 0; i < 10;i++) {
			this.addSystem(new MiniReactor());
		}
		
		this.addSystem(new AblativeArmor());
		
		hp = this.getMaxHP();
	}
	
	public Packrat() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int baseHP() {
		return 180;
	}

	@Override
	public int baseSpeed() {
		return 9;
	}

	@Override
	public int baseComplexity() {
		return 4;
	}

	@Override
	public String getName() {
		return "Packrat";
	}

	@Override
	public int baseDodge() {
		return 5;
	}

	@Override
	public int baseHeatCap() {
		return 17;
	}

	@Override
	public ResistMap internalResistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		map.put(DamageMods.AP, .7f, 1.3f);
		map.put(DamageMods.NORMAL,1f,1f);
		map.put(DamageMods.HOLLOW,1.5f, 1f);
		return map;
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mech.internalDeserial(s,new Packrat());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
