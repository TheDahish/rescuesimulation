package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Disaster implements Simulatable{
	private int startCycle;
	private Rescuable target;
	private boolean active;
	public Disaster(int startCycle, Rescuable target) {
		this.startCycle = startCycle;
		this.target = target;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getStartCycle() {
		return startCycle;
	}
	public Rescuable getTarget() {
		return target;
	}
	public void strike() throws CitizenAlreadyDeadException, BuildingAlreadyCollapsedException 
	{
		if(target instanceof Citizen)
			if(((Citizen)target).getState()==CitizenState.DECEASED)
			{
				throw new CitizenAlreadyDeadException(this,"No disaster can strike this citizen as it's already dead");
			}
			else
			{
				target.struckBy(this);
				active=true;
			}
		else
		{
			if(target instanceof ResidentialBuilding)
				if(((ResidentialBuilding)target).getStructuralIntegrity() ==0)
					throw new BuildingAlreadyCollapsedException(this,"No disaster can strike this building as it has already collapsed");
				else
			{target.struckBy(this);
			active=true;}
		}
	}
	@Override
	public String toString() {
		String s ="";
		if(this instanceof Fire)
			s="A Fire disaster";
		if(this instanceof Collapse)
			s="A Collapse disaster";
		if(this instanceof GasLeak)
			s="A Gas Leak disaster";
		if(this instanceof Injury)
			s="A Injury disaster";
		if(this instanceof Infection)
			s="A Infection disaster";
		return s;
	}
}
