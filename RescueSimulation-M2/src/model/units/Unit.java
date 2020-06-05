package model.units;

import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.events.SOSResponder;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Unit implements Simulatable, SOSResponder {
	private String unitID;
	private UnitState state;
	private Address location;
	private Rescuable target;
	private int distanceToTarget;
	private int stepsPerCycle;
	private WorldListener worldListener;

	public Unit(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		this.unitID = unitID;
		this.location = location;
		this.stepsPerCycle = stepsPerCycle;
		this.state = UnitState.IDLE;
		this.worldListener = worldListener;
	}

	public void setWorldListener(WorldListener listener) {
		this.worldListener = listener;
	}

	public WorldListener getWorldListener() {
		return worldListener;
	}

	public UnitState getState() {
		return state;
	}

	public void setState(UnitState state) {
		this.state = state;
	}

	public Address getLocation() {
		return location;
	}

	public void setLocation(Address location) {
		this.location = location;
	}

	public String getUnitID() {
		return unitID;
	}

	public Rescuable getTarget() {
		return target;
	}

	public int getStepsPerCycle() {
		return stepsPerCycle;
	}

	public void setDistanceToTarget(int distanceToTarget) {
		this.distanceToTarget = distanceToTarget;
	}

	@Override
	public void respond(Rescuable r) throws CannotTreatException, IncompatibleTargetException{
		//boolean incompat = false;
		if(this instanceof Ambulance || this instanceof DiseaseControlUnit)
			if(r instanceof ResidentialBuilding)
				throw new IncompatibleTargetException(this, r, "Cannot handle a building's disaster using this unit");
		if(this instanceof FireTruck || this instanceof GasControlUnit || this instanceof Evacuator)
		{
			if(r instanceof Citizen)
				throw new IncompatibleTargetException(this, r, "Cannot handle a citizen's disaster using this unit");
		}
		if(!canTreat(r))
			throw new CannotTreatException( this , r , "This target cannot be treated as it's already safe!");
		else
			{if (target != null && state == UnitState.TREATING)
				reactivateDisaster();
			finishRespond(r);
			}

	}

	public void reactivateDisaster() {
		Disaster curr = target.getDisaster();
		curr.setActive(true);
	}

	public void finishRespond(Rescuable r) {
		target = r;
		state = UnitState.RESPONDING;
		Address t = r.getLocation();
		distanceToTarget = Math.abs(t.getX() - location.getX())
				+ Math.abs(t.getY() - location.getY());

	}

	public abstract void treat();

	public void cycleStep() {
		if (state == UnitState.IDLE)
			return;
		if (distanceToTarget > 0) {
			distanceToTarget = distanceToTarget - stepsPerCycle;
			if (distanceToTarget <= 0) {
				distanceToTarget = 0;
				Address t = target.getLocation();
				worldListener.assignAddress(this, t.getX(), t.getY());
			}
		} else {
			state = UnitState.TREATING;
			treat();
		}
	}

	public void jobsDone() {
		if(this instanceof FireTruck || this instanceof GasControlUnit)
			((ResidentialBuilding)target).rescued=true;
		target = null;
		state = UnitState.IDLE;

	}
	public boolean canTreat(Rescuable r)
	{
		if(r instanceof Citizen)
		{
			Citizen c = (Citizen)r;
			if(this instanceof Ambulance)
				if(c.getBloodLoss()!=0)
					return true;
			if(this instanceof DiseaseControlUnit)
				if(c.getToxicity()!=0)
					return true;
		}
		else
		{
			ResidentialBuilding b = (ResidentialBuilding)r;
			if(this instanceof Evacuator)
				if(b.getDisaster()!=null && b.getDisaster() instanceof Collapse &&b.getDisaster().isActive())
					return true;
			if(this instanceof FireTruck)
				if(b.getFireDamage()!=0)
					return true;
			if(this instanceof GasControlUnit)
				if(b.getGasLevel()!=0)
					return true;
//			if(this instanceof Evacuator)
//				if(b.getOccupants().size()!=0)
//					return true;
		}
		return false;
	}
}
