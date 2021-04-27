package scimech.combat;

public class Dummy implements Target {
	
	public int dodgeValue;
	public TargetType targetType;
	
	public TakeDamage takeDamage;
	
	public int hp = 0;

	@Override
	public boolean checkFire() {
		return false;
	}

	@Override
	public int dodgeValue() {
		return dodgeValue;
	}

	@Override
	public TargetType targetType() {
		return targetType;
	}

	@Override
	public TakeDamage takeDamage() {
		return takeDamage;
	}

	@Override
	public boolean isDummy() {
		return true;
	}

	@Override
	public Dummy constructDummy() {
		return this;
	}

	@Override
	public void takeHPDamage(int i) {
		hp-=i;
	}

}
