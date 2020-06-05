package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Injury;
import model.events.SOSListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;
import model.units.UnitState;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulator;
import view.GameView;
import view.into;

public class CommandCenter extends JFrame implements SOSListener, ActionListener {

	public ArrayList<Citizen> deadcit = new ArrayList<>();
	public ArrayList<Citizen> addedcit = new ArrayList<>();
	public ArrayList<Citizen> rescued = new ArrayList<>();
	public ArrayList<ResidentialBuilding> rescuedb = new ArrayList<>();
	public ArrayList<ResidentialBuilding> fallenb = new ArrayList<>();
	public ArrayList<ResidentialBuilding> alreadystruck = new ArrayList<>();
	public ArrayList<ResidentialBuilding> occ = new ArrayList<>();
	public ArrayList<Disaster> activedisasters = new ArrayList<>();
	public ArrayList<Disaster> alldisasters = new ArrayList<>();
	private Simulator engine;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private GameView game;
	private ImageIcon ambulance = new ImageIcon("ambulanceinverted.png");
	private ImageIcon firetruck = new ImageIcon("fire.png");
	private ImageIcon disease = new ImageIcon("diseas.png");
	private ImageIcon evacuator = new ImageIcon("evac.png");
	private ImageIcon gas = new ImageIcon("gasinverted.png");
	private ArrayList<JButton> unitbuttons = new ArrayList<>();
	private ArrayList<JButton> buildingbuttons = new ArrayList<>();
	private ArrayList<JButton> citizenbuttons = new ArrayList<>();
	@SuppressWarnings("unused")
	private ArrayList<Unit> emergencyUnits;
	private Clip c;
	boolean flag = false;

	public CommandCenter() throws Exception {
		//c =BGPlaySound("background.wav");
		engine = new Simulator(this);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();
		game = new GameView();
		//PlaySound("background.wav");

		for (int i = 0; i < emergencyUnits.size(); i++) {
			JButton unit = null;
			if (emergencyUnits.get(i) instanceof Ambulance)
				{
					
					unit = new JButton("Ambulance ID " + emergencyUnits.get(i).getUnitID(), ambulance);
					unit.setBackground(Color.BLACK);
				}
			if (emergencyUnits.get(i) instanceof FireTruck)
				unit = new JButton("Firetruck ID " + emergencyUnits.get(i).getUnitID(), firetruck);
			if (emergencyUnits.get(i) instanceof DiseaseControlUnit)
				unit = new JButton("Disease ID " + emergencyUnits.get(i).getUnitID(), disease);
			if (emergencyUnits.get(i) instanceof Evacuator)
				unit = new JButton("Evacuator ID " + emergencyUnits.get(i).getUnitID(), evacuator);
			if (emergencyUnits.get(i) instanceof GasControlUnit)
				unit = new JButton("Gas Unit ID " + emergencyUnits.get(i).getUnitID(), gas);
			// unit.setText(emergencyUnits.get(i).getUnitID());
			unit.setVerticalTextPosition(JButton.TOP);
			unit.setHorizontalTextPosition(JButton.CENTER);
			// unit.setBorderPainted(false);
			unit.setMargin(new Insets(0, 0, 0, 0));
			unit.setBackground(Color.BLACK);
			unit.addActionListener(this);
			unit.setName("unit");
			unit.setForeground(Color.WHITE);
			unitbuttons.add(unit);
			game.addAvailableUnits(unit);
			for (int j = 0; j < game.mapbuttons.size(); j++)
				game.mapbuttons.get(j).addActionListener(this);
		}
		game.nextcycle.addActionListener(this);
		game.setVisible(true);

	}

	public Unit previousunit;
	public JButton prevbut;

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		// button.addActionListener(this);
		if (button.getText().equals("BASE")) {
			String info = "The base currently contains:";
			Address a = new Address(0, 0);
			for (int i = 0; i < emergencyUnits.size(); i++) {
				if (emergencyUnits.get(i).getLocation().getX() == 0
						&& emergencyUnits.get(i).getLocation().getY() == 0) {
					info += "\n Unit ID: " + emergencyUnits.get(i).getUnitID();
					info += "\n Steps per cycle: " + emergencyUnits.get(i).getStepsPerCycle();
					info += "\n Unit's State is " + emergencyUnits.get(i).getState();
					info += "\n Units Address " + emergencyUnits.get(i).getLocation().getX() + ","
							+ emergencyUnits.get(i).getLocation().getY();
				}
			}
			for (int i = 0; i < engine.citizens.size(); i++) {
				if (engine.citizens.get(i).getLocation().getX() == 0
						&& engine.citizens.get(i).getLocation().getX() == 0) {
					Citizen c = engine.citizens.get(i);
					info += "\n Citizen's name: " + c.getName() + "\n Citizen's age: " + c.getAge()
							+ "\n Citizen's ID: " + c.getNationalID() + "\n Citizen located at cell ("
							+ c.getLocation().getX() + "," + c.getLocation().getY() + ")" + "\n Citizens Condition: "
							+ "\n              HP " + c.getHp() + "\n              Bloodloss " + c.getBloodLoss()
							+ "\n              Toxicity " + c.getToxicity() + "\n              State " + c.getState();

				}
			}
			game.base(info);
			

		}
		boolean shouldrespond = game.shouldrespond();
		if (button.getName().equals("next cycle")) {
			if (engine.checkGameOver()) {
				UIManager UI=new UIManager();
				 UI.put("OptionPane.background", Color.BLACK);
				 UI.put("Panel.background", Color.BLACK);
				JLabel j = new JLabel("The game is over, the number of casualties are " + engine.calculateCasualties());
				j.setForeground(Color.WHITE);
				try {
				
					PlaySound("gameover.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null,j,
						"GAME OVER",JOptionPane.INFORMATION_MESSAGE, new ImageIcon("gameover.jpg"));
				game.setVisible(false);
				game.dispose();
			} else {
				// System.out.println("fr");
				try {
					engine.nextCycle();
				} catch (CitizenAlreadyDeadException | BuildingAlreadyCollapsedException e1) {
					// TODO Auto-generated catch block
					game.popup(e1.getMessage(), "");
				}
				game.onnextcycle(engine.currentCycle, engine.calculateCasualties());
				buildingbuttons.clear();
				for (int i = 0; i < visibleBuildings.size(); i++) {
					JButton b = game.getmapbutton(visibleBuildings.get(i).getLocation().getX(),
							visibleBuildings.get(i).getLocation().getY());
					// System.out.println(b.getText());
					// System.out.println(""+visibleBuildings.get(i).getLocation().getX()+visibleBuildings.get(i).getLocation().getY());
					if (visibleBuildings.get(i).getStructuralIntegrity() == 0)
						b.setIcon(new ImageIcon("collapse2.png"));
					else
						if(visibleBuildings.get(i).getDisaster() instanceof Fire)
							{if(!alreadystruck.contains(visibleBuildings.get(i)))
							{
								alreadystruck.add(visibleBuildings.get(i));
								try {
									PlaySound("collapse1.wav");
								} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						b.setIcon(new ImageIcon("building.gif"));}
						else
							if(visibleBuildings.get(i).getDisaster() instanceof GasLeak)
								{if(!alreadystruck.contains(visibleBuildings.get(i)))
								{
									alreadystruck.add(visibleBuildings.get(i));
									try {
										PlaySound("collapse1.wav");
									} catch (UnsupportedAudioFileException | IOException
											| LineUnavailableException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
									b.setIcon(new ImageIcon("gasbuilding.gif"));
								}
							else 
							
								{
//									try {
//										//PlaySound("collapse.wav");
//									} catch (UnsupportedAudioFileException | IOException
//											| LineUnavailableException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									}
									b.setIcon(new ImageIcon("collapse.png"));
								}
					
						//b.setIcon(new ImageIcon("buildingcitizenpng.png"));
					if (visibleBuildings.get(i).rescued && !visibleBuildings.get(i).occarrived)
						b.setIcon(new ImageIcon("savedbuilding.png"));
					if (visibleBuildings.get(i).occarrived && !occ.contains(visibleBuildings.get(i))) {
						occ.add(visibleBuildings.get(i));
						JOptionPane.showMessageDialog(null,"The occupants of building " + visibleBuildings.get(i).getLocation().getX() + ","
								+ visibleBuildings.get(i).getLocation().getY() + " have arrived to the base.",
								"",JOptionPane.INFORMATION_MESSAGE);
						
						// b.setIcon(new ImageIcon("building.png"));
					}
					b.setName("building");
					buildingbuttons.add(b);
				}
				citizenbuttons.clear();
				for (int i = 0; i < visibleCitizens.size(); i++) {
					JButton c = game.getmapbutton(visibleCitizens.get(i).getLocation().getX(),
							visibleCitizens.get(i).getLocation().getY());
					if(!addedcit.contains(visibleCitizens.get(i))) {
					if((c.getName().equals("citizen")||c.getText().equals("Citizens")))
					{
						c.setText("Citizens");
						c.setIcon(new ImageIcon("teamwork.png"));
						c.setName(""+(visibleCitizens.get(i).getLocation().getX()*10+visibleCitizens.get(i).getLocation().getY()));
						try {
							PlaySound("scream.wav");
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					else {
					if (visibleCitizens.get(i).getState() != CitizenState.DECEASED)
					{
						if(visibleCitizens.get(i).getDisaster() instanceof Injury)
							c.setIcon(new ImageIcon("inj.gif"));
						else
							
						c.setIcon(new ImageIcon("sick.gif"));
						try {
							PlaySound("scream.wav");
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
					}
					
					c.setText(visibleCitizens.get(i).getName());
					c.setName("citizen");
					}
					
					addedcit.add(visibleCitizens.get(i));
					}
					if (visibleCitizens.get(i).getState() == CitizenState.RESCUED) {
						if(alone(visibleCitizens.get(i)))
						c.setIcon(new ImageIcon("savedperson.png"));
						if (!rescued.contains(visibleCitizens.get(i))) {
							game.citlog(visibleCitizens.get(i).getName() + " is rescued");
							rescued.add(visibleCitizens.get(i));
						}
					}
					citizenbuttons.add(c);
					if(visibleCitizens.get(i).getState()==CitizenState.DECEASED&&!deadcit.contains(visibleCitizens.get(i)))
					{
						if(alone(visibleCitizens.get(i)))
						c.setIcon(new ImageIcon("death.png"));
						try {
							PlaySound("killed.wav");
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
				game.Availableunits.removeAll();
				game.Respondingunits.removeAll();
				game.Treatingunits.removeAll();
				game.revalidate();
				game.repaint();
				JButton unit = null;
				unitbuttons.clear();
				for (int j = 0; j < emergencyUnits.size(); j++) {
					if (emergencyUnits.get(j).getState() == UnitState.IDLE)

					{

						if (emergencyUnits.get(j) instanceof Ambulance)
							unit = new JButton("Ambulance ID " + emergencyUnits.get(j).getUnitID(), ambulance);
						if (emergencyUnits.get(j) instanceof FireTruck)
							unit = new JButton("Firetruck ID " + emergencyUnits.get(j).getUnitID(), firetruck);
						if (emergencyUnits.get(j) instanceof DiseaseControlUnit)
							unit = new JButton("Disease ID " + emergencyUnits.get(j).getUnitID(), disease);
						if (emergencyUnits.get(j) instanceof Evacuator)
							unit = new JButton("Evacuator ID " + emergencyUnits.get(j).getUnitID(), evacuator);
						if (emergencyUnits.get(j) instanceof GasControlUnit)
							unit = new JButton("Gas Unit ID " + emergencyUnits.get(j).getUnitID(), gas);
						// unit.setText(emergencyUnits.get(i).getUnitID());
						unit.setVerticalTextPosition(JButton.TOP);
						unit.setHorizontalTextPosition(JButton.CENTER);
						// unit.setBorderPainted(false);
						unit.setMargin(new Insets(0, 0, 0, 0));
						unit.setBackground(Color.black);
						unit.setForeground(Color.WHITE);
						unit.addActionListener(this);
						unit.setName("unit");
						unitbuttons.add(unit);
						game.addAvailableUnits(unit);
					}
					if (emergencyUnits.get(j).getState() == UnitState.RESPONDING) {
						if (emergencyUnits.get(j) instanceof Ambulance)
							unit = new JButton("Ambulance ID " + emergencyUnits.get(j).getUnitID(), ambulance);
						if (emergencyUnits.get(j) instanceof FireTruck)
							unit = new JButton("Firetruck ID " + emergencyUnits.get(j).getUnitID(), firetruck);
						if (emergencyUnits.get(j) instanceof DiseaseControlUnit)
							unit = new JButton("Disease ID " + emergencyUnits.get(j).getUnitID(), disease);
						if (emergencyUnits.get(j) instanceof Evacuator)
							unit = new JButton("Evacuator ID " + emergencyUnits.get(j).getUnitID(), evacuator);
						if (emergencyUnits.get(j) instanceof GasControlUnit)
							unit = new JButton("Gas Unit ID " + emergencyUnits.get(j).getUnitID(), gas);
						// unit.setText(emergencyUnits.get(i).getUnitID());
						unit.setVerticalTextPosition(JButton.TOP);
						unit.setHorizontalTextPosition(JButton.CENTER);
						// unit.setBorderPainted(false);
						unit.setMargin(new Insets(0, 0, 0, 0));
						unit.setBackground(Color.black);
						unit.setForeground(Color.WHITE);
						unit.addActionListener(this);
						unit.setName("unit");
						unitbuttons.add(unit);
						game.addRespondingUnits(unit);

					}
					if (emergencyUnits.get(j).getState() == UnitState.TREATING) {
						if (emergencyUnits.get(j) instanceof Ambulance)
							unit = new JButton("Ambulance ID " + emergencyUnits.get(j).getUnitID(), ambulance);
						if (emergencyUnits.get(j) instanceof FireTruck)
							unit = new JButton("Firetruck ID " + emergencyUnits.get(j).getUnitID(), firetruck);
						if (emergencyUnits.get(j) instanceof DiseaseControlUnit)
							unit = new JButton("Disease ID " + emergencyUnits.get(j).getUnitID(), disease);
						if (emergencyUnits.get(j) instanceof Evacuator)
							unit = new JButton("Evacuator ID " + emergencyUnits.get(j).getUnitID(), evacuator);
						if (emergencyUnits.get(j) instanceof GasControlUnit)
							unit = new JButton("Gas Unit ID " + emergencyUnits.get(j).getUnitID(), gas);
						// unit.setText(emergencyUnits.get(i).getUnitID());
						unit.setVerticalTextPosition(JButton.TOP);
						unit.setHorizontalTextPosition(JButton.CENTER);
						// unit.setBorderPainted(false);
						unit.setMargin(new Insets(0, 0, 0, 0));
						unit.setBackground(Color.black);
						unit.setForeground(Color.WHITE);
						unit.addActionListener(this);
						unit.setName("unit");
						unitbuttons.add(unit);
						game.addTreatingUnits(unit);

					}
					unit.setEnabled(true);

				}
				for (int i = 0; i < visibleCitizens.size(); i++) {
					if (visibleCitizens.get(i).getState() == CitizenState.DECEASED) {
						if (!deadcit.contains(visibleCitizens.get(i))) {
							deadcit.add(visibleCitizens.get(i));
							game.citlog("\n -" + visibleCitizens.get(i).getName() + " is dead!");
						}
					}
					if (visibleCitizens.get(i).getDisaster().getStartCycle() == engine.currentCycle) {
						game.disastersinfo("-"+visibleCitizens.get(i).getDisaster().toString() + " struck "
								+ visibleCitizens.get(i).getName() + " on Cycle "
								+ visibleCitizens.get(i).getDisaster().getStartCycle());
					}

				}
				for (int i = 0; i < visibleBuildings.size(); i++) {
					if (visibleBuildings.get(i).getStructuralIntegrity() == 0) {
						if (!fallenb.contains(visibleBuildings.get(i))) {
							fallenb.add(visibleBuildings.get(i));
							try {
								PlaySound("collapse.wav");
							} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							game.citlog("\n -The Building located at " + visibleBuildings.get(i).getLocation().getX()
									+ "," + visibleBuildings.get(i).getLocation().getY() + " has fallen!");
						}
					}
					if (visibleBuildings.get(i).getDisaster().getStartCycle() == engine.currentCycle) {
						game.disastersinfo("-"+visibleBuildings.get(i).getDisaster().toString()
								+ " struck a building located at " + visibleBuildings.get(i).getLocation().getX() + ","
								+ visibleBuildings.get(i).getLocation().getY() + " on Cycle "
								+ visibleBuildings.get(i).getDisaster().getStartCycle());
					}
				}
				String s = "";
				for (int i = 0; i < engine.executedDisasters.size(); i++) {

					if (engine.executedDisasters.get(i).isActive())
						if (engine.executedDisasters.get(i) instanceof Collapse
								|| engine.executedDisasters.get(i) instanceof Fire
								|| engine.executedDisasters.get(i) instanceof GasLeak) {
							if (((ResidentialBuilding) engine.executedDisasters.get(i).getTarget())
									.getStructuralIntegrity() != 0)
								s += "\n -" + engine.executedDisasters.get(i).toString()
										+ " is active on a building located at "
										+ ((ResidentialBuilding) engine.executedDisasters.get(i).getTarget())
												.getLocation().getX()
										+ "," + ((ResidentialBuilding) engine.executedDisasters.get(i).getTarget())
												.getLocation().getY();
						} else if (((Citizen) engine.executedDisasters.get(i).getTarget())
								.getState() != CitizenState.DECEASED)
							s += "\n -" + engine.executedDisasters.get(i).toString() + " is active on "
									+ ((Citizen) engine.executedDisasters.get(i).getTarget()).getName();

				}
				game.activedisasters(s);

			}

		}
		int count = 0;
		if (button.getName().equals("citizen")) {
			Citizen c = visibleCitizens.get(citizenbuttons.indexOf(button));
			game.citizenclick(c);
			// if(!flag) {
			if (shouldrespond) {
				try {
					// flag=true;
					// count++;
					previousunit.respond(c);
					game.optionyes.setSelected(false);
					PlaySound("amb.wav");
				} catch (CannotTreatException | IncompatibleTargetException e1) {
					// TODO Auto-generated catch block
					// count++;
					game.popup(e1.getMessage(), "");
					// }

					game.setfalse();
				} catch (UnsupportedAudioFileException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				game.setfalse();
				// flag =false;
			}
			// flag =false;
		}
		if (button.getName().equals("building")) {
			ResidentialBuilding b = visibleBuildings.get(buildingbuttons.indexOf(button));
			game.buildingclicked(b);
			if (shouldrespond) {
				try {
					if (previousunit == null)
						game.popup("Select a unit!", "");
					else {
						previousunit.respond(b);
						game.optionyes.setSelected(false);
					try {
						this.PlaySound("emergency.wav");
					} catch (UnsupportedAudioFileException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (LineUnavailableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}}
				} catch (CannotTreatException | IncompatibleTargetException e1) {
					// TODO Auto-generated catch block
					game.popup(e1.getMessage(), "");
				}
				game.setfalse();
			}
			for(int ik = 0 ; ik<emergencyUnits.size();ik++)
			{
				if(emergencyUnits.get(ik).getLocation().getX()==b.getLocation().getX()&&emergencyUnits.get(ik).getLocation().getY()==b.getLocation().getY())
				{
					Unit u = emergencyUnits.get(ik);
						String info = "";
						info += "\nUnit ID: " + u.getUnitID();
						info += "\n Steps per cycle: " + u.getStepsPerCycle();
						info += "\n Unit's State is " + u.getState();
						info += "\n Units Address " + u.getLocation().getX() + "," + u.getLocation().getY();
						if (u.getTarget() != null)
							if (u.getTarget() instanceof ResidentialBuilding)
								info += "\n This Unit's target is the building at location " + u.getTarget().getLocation().getX() + ","
										+ u.getTarget().getLocation().getY();
							else
								info += "\n This unit's target is " + ((Citizen) u.getTarget()).getName() + ". Located at "
										+ u.getTarget().getLocation().getX() + "," + u.getTarget().getLocation().getY();
						if (u instanceof Evacuator) {
							info += "\n Number of citizens on board: " + ((Evacuator) u).getPassengers().size();
							for (int i = 0; i < ((Evacuator) u).getPassengers().size(); i++) {
								Citizen c = ((Evacuator) u).getPassengers().get(i);
								info += "\n Citizens: \nCitizen's name: " + c.getName() + "\n Citizen's age: " + c.getAge()
										+ "\n Citizen's ID: " + c.getNationalID() + "\n Citizen located at cell ("
										+ c.getLocation().getX() + "," + c.getLocation().getY() + ")" + "\n Citizens Condition: "
										+ "\n              HP " + c.getHp() + "\n              Bloodloss " + c.getBloodLoss()
										+ "\n              Toxicity " + c.getToxicity() + "\n              State " + c.getState();
							}
						}
						game.rescuableinfo.setText(game.rescuableinfo.getText()+info);
				}
			}
		}
		if (button.getName().equals("unit")) {
			Unit u = emergencyUnits.get(unitbuttons.indexOf(button));
			game.unitclicked(u);
			previousunit = u;
		}
		if(button.getText().equals("Citizens"))
		{
			int l = Integer.parseInt(button.getName());
			int x = (int) (l / 10);
			int y = l%10;
			String s ="";
			String dis="";
			for(int i = 0; i< visibleCitizens.size();i++)
			{
				Citizen c = visibleCitizens.get(i);
				if(c.getLocation().getX()==x&&c.getLocation().getY()==y)
				{
					if (c.getDisaster() instanceof Injury) {
						dis = "Injury";
					} else {
						dis = "Infection";
					}
					
					s += "\n Citizen's name: " + c.getName() + "\n Citizen's age: " + c.getAge() + "\n Citizen's ID: "
							+ c.getNationalID() + "\n Citizen located at cell (" + c.getLocation().getX() + ","
							+ c.getLocation().getY() + ")" + "\n Citizens Condition: " + "\n              HP " + c.getHp()
							+ "\n              Bloodloss " + c.getBloodLoss() + "\n              Toxicity " + c.getToxicity()
							+ "\n              State " + c.getState() + "\n An " + dis + " Disaster is affecting " + c.getName();
				}
			}
			game.rescuableinfo.setText(s);
			if(shouldrespond)
			{
				
				JPanel p = new JPanel(new GridLayout(0,2));
				p.setPreferredSize(new Dimension(100 ,100));
				p.setBackground(Color.BLACK);
				for(int i = 0; i< visibleCitizens.size();i++)
				{
					Citizen c = visibleCitizens.get(i);
					if(c.getLocation().getX()==x&&c.getLocation().getY()==y) {
						JButton b = new JButton(c.getName());
						//b.setIcon( new ImageIcon("citizen.png"));
						b.setName("citizen");
						
						if(visibleCitizens.get(i).getDisaster() instanceof Injury)
							b.setIcon(new ImageIcon("inj.gif"));
						else
							
						b.setIcon(new ImageIcon("sick.gif"));
						b.setText(visibleCitizens.get(i).getName());
						b.setPreferredSize(new Dimension(64, 64));
						p.add(b);
						p.setBackground(Color.BLACK);
						b.setHorizontalTextPosition(JButton.CENTER);
						b.setVerticalTextPosition(JButton.TOP);
						b.setBorderPainted(true);
						b.setBackground(Color.black);
						b.setFont(new Font("Courier New", Font.BOLD, 10));
						b.setForeground(Color.WHITE);
						b.addActionListener(new ActionListener() { 
							  public void actionPerformed(ActionEvent e) { 
							    try {
									manycit(c);
								} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							  } 
							} );
					}
				}
				UIManager.put("OptionPane.minimumSize",new Dimension(300,300));
				UIManager.put("OptionPane.background", Color.BLACK);
				JOptionPane.showMessageDialog(null,p, "Citizens",JOptionPane.INFORMATION_MESSAGE);
				
			}
		}

	}
	public boolean alone(Citizen c)
	{
		for(int i = 0;i<visibleCitizens.size();i++)
		{
			if(c.getLocation().equals(visibleCitizens.get(i).getLocation())&&!c.getNationalID().equals(visibleCitizens.get(i).getNationalID()))
				return false;
		}
		return true;
	}
	
	@Override
	public void receiveSOSCall(Rescuable r) {

		if (r instanceof ResidentialBuilding) {

			if (!visibleBuildings.contains(r))
				visibleBuildings.add((ResidentialBuilding) r);

		} else {

			if (!visibleCitizens.contains(r))
				visibleCitizens.add((Citizen) r);
		}

	}
public void manycit(Citizen c) throws UnsupportedAudioFileException, IOException, LineUnavailableException
{
	
		try {
			if (previousunit == null)
				game.popup("Select a unit!", "");
			else {
				PlaySound("amb.wav");
				previousunit.respond(c);
				game.optionyes.setSelected(false);
			} // game.playSound("emergency.wav");
		} catch (CannotTreatException | IncompatibleTargetException e1) {
			// TODO Auto-generated catch block
			game.popup(e1.getMessage(), "");
		}
		game.setfalse();
	
}
public Clip BGPlaySound(String dir) throws UnsupportedAudioFileException, IOException, LineUnavailableException { 
	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(dir).getAbsoluteFile());
Clip clip = AudioSystem.getClip();
clip.open(audioInputStream);
clip.start();
clip.loop(clip.LOOP_CONTINUOUSLY);
return clip;

}
public void PlaySound(String dir) throws UnsupportedAudioFileException, IOException, LineUnavailableException { 
	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(dir).getAbsoluteFile());
Clip clip = AudioSystem.getClip();
clip.open(audioInputStream);
clip.start();
//clip.loop(clip.LOOP_CONTINUOUSLY);
}

	public static void main(String[] args) throws Exception {
		into n = new into();
		n.setVisible(true);
		//c.game.setVisible(true);
		
	}

}
