package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import controller.CommandCenter;

public class into extends JFrame{
	private Paint panel;
	private JPanel buttons;
	private JPanel inpt;
	public String l;
	public CommandCenter game;
	public Clip c;
	public into ()
	{
		try {
			c=BGPlaySound("theme.wav");
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		setTitle("Welcome");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(-10, 0, 2000, 1000);
		panel=new Paint();
		panel.setPreferredSize(new Dimension(1000,1000));
		inpt= new JPanel(new GridLayout(2,2)); 
		buttons = new JPanel(new GridLayout(0, 2));
		JTextArea username = new JTextArea();
		
		JButton exit = new JButton("Exit");
		TitledBorder currentdborder = BorderFactory.createTitledBorder(BorderFactory
		        .createLineBorder(Color.black), "Enter Your Name:");
		currentdborder.setTitleJustification(TitledBorder.CENTER);
		currentdborder.setTitlePosition(TitledBorder.TOP);
		currentdborder.setTitleColor(Color.BLACK);
		currentdborder.setTitleFont(new Font("Courier New", Font.BOLD, 12));
		username.setBorder(currentdborder);
		username.setFont(new Font("Courier New", Font.BOLD, 19));
		username.setPreferredSize(new Dimension(100, 100));
		username.setBackground(Color.BLACK);
		JButton b=  new JButton("Enter Game");
		b.setContentAreaFilled(false);
		b.setPreferredSize(new Dimension(10, 10));
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);
		buttons.add(exit);
		buttons.add(b);
		buttons.setPreferredSize(new Dimension(100, 100));
		//buttons.setconte
		
		inpt.add(username);
		inpt.add(buttons);
		inpt.setPreferredSize(new Dimension(100, 100));
		panel.setLayout(null);
		panel.add(username);
		panel.add(b);
		b.setBorderPainted(false);
		b.setBounds(100, 100, 100, 50);
		username.setOpaque(false);
		username.setBounds(100, 70, 200, 35);
		exit.setBounds(200, 100, 55, 50);
		panel.add(exit);
		//panel.add(Box.createHorizontalGlue());
		add(panel);
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				setVisible(false);
				dispose();
				
			}
		});
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//String line ="";
	            for(  String line: username.getText().split("\\n"))
	                l=line;
	            try {
					game= new CommandCenter();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            setVisible(false);
	           // c.stop();
	            dispose();
			}
		});
		
	 
		
		
	}
	public Clip BGPlaySound(String dir) throws UnsupportedAudioFileException, IOException, LineUnavailableException { 
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(dir).getAbsoluteFile());
	Clip clip = AudioSystem.getClip();
	clip.open(audioInputStream);
	clip.start();
	clip.loop(clip.LOOP_CONTINUOUSLY);
	return clip;

	}
	public static void main(String[] args) {
		into n =  new into();
		n.setVisible(true);
	}

}
