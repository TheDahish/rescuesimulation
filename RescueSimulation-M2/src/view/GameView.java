package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.xml.transform.SourceLocator;

import model.disasters.Collapse;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Injury;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.Evacuator;
import model.units.Unit;
import sun.audio.*;

public class GameView extends JFrame {
	 private final int BUFFER_SIZE = 128000;
	    private File soundFile;
	    private AudioInputStream audioStream;
	    private AudioFormat audioFormat;
	    private SourceDataLine sourceLine;
	private JPanel Mainpanel;
	private JPanel test;
	private PaintTest Rescuepanel;
	private JPanel Infopanel;
	private JPanel Unitspanel;
	public JPanel Availableunits;
	public JPanel Respondingunits;
	public JPanel Treatingunits;
	private JPanel functions;
	private JPanel centrepanel;
	public JTextArea rescuableinfo;
	private JTextArea messages;
	public JButton nextcycle;
	public ArrayList<JButton> mapbuttons = new ArrayList<>();
	private JScrollPane info;
	private JScrollPane log;
	private JScrollPane buttons;
	private JLabel currentcycle;
	private JLabel causualties;
	public JRadioButton optionyes;
	public JRadioButton optionno;
	public ArrayList<JButton> availableu = new ArrayList<>();
	public ArrayList<JButton> respondingu = new ArrayList<>();
	public ArrayList<JButton> treatingu = new ArrayList<>();
	public String causinfo = "Citizens and Buildings information: ";
	public String disastersinfo = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n History of Disasters ";
	public String activedisinfo = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n Active Disasters";

	public GameView() {

		setTitle("Rescuer Man");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(-10, 0, 2000, 1000);
		Mainpanel = new JPanel(new BorderLayout());
		Rescuepanel = new PaintTest();
		// Rescuepanel.setBackground(Color.BLACK);
		Rescuepanel.setLayout(new GridLayout(10, 10));
		Rescuepanel.setPreferredSize(new Dimension(40, 40));
		TitledBorder rescueborder = new TitledBorder("City Map");
		rescueborder.setTitleJustification(TitledBorder.CENTER);
		rescueborder.setTitlePosition(TitledBorder.TOP);
		Rescuepanel.setBorder(rescueborder);
		Rescuepanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		Infopanel = new JPanel(new GridLayout(2, 0));
		Infopanel.setPreferredSize(new Dimension(300, 400));
		// Infopanel.setEditable(false);
		TitledBorder infoborder = new TitledBorder("Information");
		infoborder.setTitleJustification(TitledBorder.CENTER);
		infoborder.setTitlePosition(TitledBorder.TOP);
		infoborder.setTitleColor(Color.WHITE);
		infoborder.setTitleFont(new Font("Courier New", Font.BOLD, 12));
		Infopanel.setBorder(infoborder);
		Unitspanel = new JPanel(new BorderLayout());
		Unitspanel.setPreferredSize(new Dimension(300, 300));
		JPanel checkrespond = new JPanel(new FlowLayout());
		JLabel r = new JLabel("Respond: ");
		r.setFont(new Font("Courier New", Font.BOLD, 16));
		r.setForeground(Color.WHITE);
		optionyes = new JRadioButton("Yes");
		optionyes.setBackground(Color.BLACK);
		optionyes.setForeground(Color.WHITE);
		optionno = new JRadioButton("No", true);
		optionno.setBackground(Color.BLACK);
		optionno.setForeground(Color.WHITE);
		ButtonGroup respondornot = new ButtonGroup();
		respondornot.add(optionyes);
		respondornot.add(optionno);
		checkrespond.add(r);
		checkrespond.add(optionyes);
		checkrespond.add(optionno);
		checkrespond.setPreferredSize(new Dimension(100, 30));
		Unitspanel.add(checkrespond);
		Availableunits = new JPanel(new GridLayout(0, 3));
		Availableunits.setPreferredSize(new Dimension(100, 400));
		TitledBorder border = new TitledBorder("Available Units");
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitlePosition(TitledBorder.TOP);
		border.setTitleColor(Color.WHITE);
		border.setTitleFont(new Font("Courier New", Font.BOLD, 12));
		Availableunits.setBorder(border);
		Respondingunits = new JPanel(new GridLayout(0, 3));
		Respondingunits.setPreferredSize(new Dimension(100, 200));
		TitledBorder respondingborder = new TitledBorder("Responding Units");
		respondingborder.setTitleJustification(TitledBorder.CENTER);
		respondingborder.setTitlePosition(TitledBorder.TOP);
		respondingborder.setTitleColor(Color.WHITE);
		respondingborder.setTitleFont(new Font("Courier New", Font.BOLD, 12));
		Respondingunits.setBorder(respondingborder);
		Treatingunits = new JPanel(new GridLayout(0, 3));
		Treatingunits.setPreferredSize(new Dimension(100, 200));
		TitledBorder treatingborder = new TitledBorder("Treating Units");
		treatingborder.setTitleJustification(TitledBorder.CENTER);
		treatingborder.setTitlePosition(TitledBorder.TOP);
		treatingborder.setTitleColor(Color.WHITE);
		treatingborder.setTitleFont(new Font("Courier New", Font.BOLD, 12));
		Treatingunits.setBorder(treatingborder);
		add(Mainpanel);

		Mainpanel.add(Infopanel, BorderLayout.WEST);
		UIManager.put("ScrollBar.trackHighlightForeground", (new Color(57,57,57))); 
	    UIManager.put("scrollbar", (new Color(57,57,57))); 
	    UIManager.put("ScrollBar.thumb", new ColorUIResource(new Color(57,57,57))); 
	    UIManager.put("ScrollBar.thumbHeight", 2); 
	    UIManager.put("ScrollBar.background", (new Color(57,57,57)));
	    UIManager.put("ScrollBar.thumbDarkShadow", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar.thumbShadow", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar.thumbHighlight", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar.trackForeground", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar.trackHighlight", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar.foreground", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar.shadow", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar.highlight", new ColorUIResource(new Color(57,57,57)));
	    UIManager.put("ScrollBar._key_", new ColorUIResource(Color.BLACK));
		buttons = new JScrollPane(test);
		buttons.getVerticalScrollBar().setForeground(Color.black);
		

		// test = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		// test.add(Availableunits);
		Availableunits.setBackground(Color.BLACK);
		buttons = new JScrollPane(Availableunits);
		buttons.setPreferredSize(new Dimension(100, 300));
		buttons.setBackground(Color.black);
		buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		Respondingunits.setBackground(Color.BLACK);
		Treatingunits.setBackground(Color.black);
		Unitspanel.add(buttons, BorderLayout.NORTH);
		Unitspanel.add(Respondingunits, BorderLayout.CENTER);
		Unitspanel.add(Treatingunits, BorderLayout.SOUTH);
		Unitspanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// Mainpanel.add(Unitspanel, BorderLayout.EAST);
		JPanel leftpanel = new JPanel(new BorderLayout());
		leftpanel.setBackground(Color.BLACK);
		checkrespond.setBackground(Color.BLACK);
		Unitspanel.setBackground(Color.BLACK);
		leftpanel.add(checkrespond, BorderLayout.NORTH);
		leftpanel.add(Unitspanel, BorderLayout.CENTER);
		Mainpanel.add(leftpanel, BorderLayout.EAST);
		// Unitspanel.add(new JButton("de"));

		// Availableunits.add(new JButton("test"));
		// Rescuepanel.add(new JButton("tes"));
		ImageIcon map = new ImageIcon("black.png");
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				JButton b = new JButton(i + "," + j,map);
				b.setName("Map");
				// b.setText();
				b.setHorizontalTextPosition(JButton.CENTER);
				b.setVerticalTextPosition(JButton.TOP);
				b.setBorderPainted(false);
				b.setBackground(Color.WHITE);
				b.setOpaque(false);
				b.setContentAreaFilled(false);
				b.setBorderPainted(false);
				b.setForeground(Color.WHITE);
				Rescuepanel.add(b);
				mapbuttons.add(b);
			}
		}
		mapbuttons.get(0).setIcon(new ImageIcon("arctic-station.png"));
		mapbuttons.get(0).setText("BASE");
		// Respondingunits.add(new JButton("sd"));

		// Treatingunits.add(new JButton("sds"));
		functions = new JPanel(new GridLayout(0, 3));
		nextcycle = new JButton("NEXT CYCLE");
		nextcycle.setName("next cycle");
		nextcycle.setPreferredSize(new Dimension(50, 50));
		nextcycle.setBackground(Color.BLACK);
		nextcycle.setForeground(Color.WHITE);
		nextcycle.setBorderPainted(false);
		nextcycle.setFont(new Font("Courier New", Font.BOLD, 16));
		nextcycle.setIcon(new ImageIcon("arrow.png"));
		nextcycle.setHorizontalTextPosition(SwingConstants.LEFT);
		Border border2 = BorderFactory.createLineBorder(Color.GRAY, 1);
		currentcycle = new JLabel("The current cycle is");
		currentcycle.setFont(new Font("Courier New", Font.BOLD, 16));
		currentcycle.setForeground(Color.white);
		currentcycle.setBackground(Color.BLACK);
		currentcycle.setHorizontalAlignment(JLabel.CENTER);
		//currentcycle.setBorder(border2);
		functions.setBackground(Color.BLACK);
		currentcycle.setPreferredSize(new Dimension(50,50));
		functions.add(currentcycle);
		functions.add(nextcycle);
		causualties = new JLabel("The causualities so far is");
		causualties.setHorizontalAlignment(JLabel.CENTER);
		//causualties.setBorder(border2);
		causualties.setBackground(Color.WHITE);
		causualties.setPreferredSize(new Dimension(50,50));
		functions.add(causualties);
		functions.setPreferredSize(new Dimension(50, 50));
		centrepanel = new JPanel(new BorderLayout());
		centrepanel.add(Rescuepanel, BorderLayout.CENTER);
		centrepanel.add(functions, BorderLayout.NORTH);
		Mainpanel.add(centrepanel, BorderLayout.CENTER);
		JLabel back = new JLabel(new ImageIcon("backround.png"));
		

		rescuableinfo = new JTextArea();
		TitledBorder rescuableborder = BorderFactory.createTitledBorder(BorderFactory
		        .createLineBorder(Color.black), "Rescuable Information");
		rescuableborder.setTitleJustification(TitledBorder.CENTER);
		rescuableborder.setTitlePosition(TitledBorder.TOP);
		rescuableborder.setTitleColor(Color.WHITE);
		rescuableborder.setTitleFont(new Font("Courier New", Font.BOLD, 15));
		rescuableinfo.setBorder(rescuableborder);
		rescuableinfo.setForeground(Color.WHITE);
		rescuableinfo.setFont(new Font("Courier New", Font.BOLD, 15));
		messages = new JTextArea();
		messages.setForeground(Color.WHITE);
		messages.setBackground(Color.BLACK);
		messages.setFont(new Font("Courier New", Font.BOLD, 12));
		TitledBorder currentdborder = BorderFactory.createTitledBorder(BorderFactory
		        .createLineBorder(Color.black), "Game Messages");
		currentdborder.setTitleJustification(TitledBorder.CENTER);
		currentdborder.setTitlePosition(TitledBorder.TOP);
		currentdborder.setTitleColor(Color.WHITE);
		currentdborder.setTitleFont(new Font("Courier New", Font.BOLD, 12));
		messages.setBorder(currentdborder);
		JTextArea activedisasters = new JTextArea();
		TitledBorder active = new TitledBorder("Active Disasters");
		active.setTitleJustification(TitledBorder.CENTER);
		active.setTitlePosition(TitledBorder.TOP);
		active.setTitleColor(Color.WHITE);
		active.setTitleFont(new Font("Courier New", Font.BOLD, 12));
		activedisasters.setBorder(active);
		info = new JScrollPane(rescuableinfo);
		info.setPreferredSize(new Dimension(200, 400));
		// info.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// info.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		rescuableinfo.setEditable(false);
		rescuableinfo.setBackground(Color.BLACK);
		messages.setEditable(false);
		// rescuableinfo.setBackground(Color.BLACK);
		Infopanel.add(info);
		Infopanel.setBackground(Color.BLACK);
		// rescuableinfo.setPreferredSize(new Dimension(Infopanel.getWidth(), 700));
		// info.setPreferredSize(new Dimension(Infopanel.getWidth(), 7000));
		// Infopanel.add(rescuableinfo);
		messages.setEditable(false);
		messages.setText(causinfo + disastersinfo + activedisinfo);
		// messages.setPreferredSize(new Dimension(200, 300));
		log = new JScrollPane(messages);
		// log.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// log.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		log.setPreferredSize(new Dimension(200, 395));
		// log.setHorizontalScrollBarPolicy(JScrollPane.);
		messages.setLineWrap(true);
		messages.setWrapStyleWord(true);
		rescuableinfo.setWrapStyleWord(true);
		rescuableinfo.setLineWrap(true);
		Infopanel.add(log);
		Rescuepanel.setBackground(Color.WHITE);
		// Infopanel.add(activedisasters);
		//setVisible(true);
		//playSound("background.wav");
		causualties.setFont(new Font("Courier New", Font.BOLD, 16));
		causualties.setForeground(Color.white);

	}
	
	public boolean shouldrespond() {
		return (optionyes.isSelected());
	}

	public void onnextcycle(int cycle, int caus) {
		currentcycle.setText("The current cycle is " + cycle);
		causualties.setText("The causualities so far is " + caus);
	}

	public void background() {
		
	}

	public void buildingclicked(ResidentialBuilding b) {
		String s = "";
		String occ = "";
		String disaster = "";
		if (b.getDisaster() instanceof Fire)
			disaster = "Fire";
		if (b.getDisaster() instanceof GasLeak)
			disaster = "Gas Leak";
		if (b.getDisaster() instanceof Collapse)
			disaster = "Collapse";
		if (b.getOccupants().size() == 0)
			occ = "\nThis building has no occupants";
		else {
			occ = "\n The building's Occupant's info is";
			for (int i = 0; i < b.getOccupants().size(); i++) {
				Citizen c = b.getOccupants().get(i);
				occ += "\n Citizen's name: " + c.getName() + "\n Citizen's age: " + c.getAge() + "\n Citizen's ID: "
						+ c.getNationalID() + "\n Citizen located at cell (" + c.getLocation().getX() + ","
						+ c.getLocation().getY() + ")" + "\n Citizens Condition: " + "\n              HP " + c.getHp()
						+ "\n              Bloodloss " + c.getBloodLoss() + "\n              Toxicity "
						+ c.getToxicity() + "\n              State " + c.getState();
			}
		}
		s += "The building is located at (" + b.getLocation().getX() + "," + b.getLocation().getY() + ")"
				+ "\n The building's Structrual Integrity is " + b.getStructuralIntegrity()
				+ "\n The building's Fire Damage is " + b.getFireDamage() + "\n The building's Gas Level is "
				+ b.getGasLevel() + occ + "\n This building is affected by a " + disaster + " Disaster";
		rescuableinfo.setText(s);
	}

	public Clip PlaySound(String dir) throws UnsupportedAudioFileException, IOException, LineUnavailableException { 
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(dir).getAbsoluteFile());
Clip clip = AudioSystem.getClip();
clip.open(audioInputStream);
clip.start();
return clip;}
	public void citizenclick(Citizen c) {
		String dis = "";
		if (c.getDisaster() instanceof Injury) {
			dis = "Injury";
		} else {
			dis = "Infection";
		}
		String s = "";
		s += "Citizen's name: " + c.getName() + "\n Citizen's age: " + c.getAge() + "\n Citizen's ID: "
				+ c.getNationalID() + "\n Citizen located at cell (" + c.getLocation().getX() + ","
				+ c.getLocation().getY() + ")" + "\n Citizens Condition: " + "\n              HP " + c.getHp()
				+ "\n              Bloodloss " + c.getBloodLoss() + "\n              Toxicity " + c.getToxicity()
				+ "\n              State " + c.getState() + "\n An " + dis + " Disaster is affecting " + c.getName();
		rescuableinfo.setText(s);
	}

	public void base(String s) {
		rescuableinfo.setText(s);
	}

	public void setfalse() {
		optionyes.setSelected(false);
		optionno.setSelected(true);
	}

	public JButton getmapbutton(int x, int y) {
		// System.out.println(x+""+y);
		// String s = k+","+j ;
		int j = 0;
		for (int i = 0; i < 10; i++)
			for (int k = 0; k < 10; k++) {
				j++;
				if (i == x && k == y)
					return mapbuttons.get(j - 1);
			}
		return null;
	}
	
	public void addAvailableUnits(JButton unit) {
		availableu.add(unit);
		Availableunits.add(unit);
	}

	public void addRespondingUnits(JButton unit) {
		respondingu.add(unit);
		Respondingunits.add(unit);
	}

	public void addTreatingUnits(JButton unit) {
		availableu.add(unit);
		Treatingunits.add(unit);
	}

	public void popup(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE, new ImageIcon("bahayem.jpg"));
	}

	public void unitclicked(Unit u) {
		String info = "";
		info += "Unit ID: " + u.getUnitID();
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
		rescuableinfo.setText(info);
	}

	public void citlog(String s) {
		causinfo += s;
		messages.setText(causinfo + disastersinfo + activedisinfo);

		revalidate();
	}

	public void disastersinfo(String s) {
		disastersinfo += "\n" + s;
		messages.setText(causinfo + disastersinfo + activedisinfo);
		revalidate();
	}

	public void activedisasters(String s) {
		activedisinfo = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n Active Disasters" + s;
		messages.setText(causinfo + disastersinfo + activedisinfo);
		revalidate();
	}
	public void playSound(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }

	public static void main(String[] args) {
		GameView n = new GameView();
		// n.setVisible(true);
	}

}
