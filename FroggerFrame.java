/*************************************************************************
 *	Vinh Tran. CIS 35A
 *  Compilation:  javac FroggerComponent.java FroggerFrame.java Row.java
 *  Execution:    java FroggerFrame
 *  Dependencies: Row.java FroggerComponent.java 
 *
 *  Play the game: Save The Frogs
 *
 *  
 *
 *************************************************************************/
// FroggerFrame.java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JOptionPane;
/*
 Implements the Frogger window.
*/
class FroggerFrame extends JFrame implements KeyListener, ActionListener {
	public static final int DELAY = 400;  // milliseconds
	private static FroggerComponent frogger;
	// our drawing component
	private JButton newGameButton;
	private JButton harderButton;
	private static JLabel gameLevel;  
	private static javax.swing.Timer timer;
	private static int level;
	private static int delay;
	private static boolean showGameOver;
	
	public FroggerFrame( ){
		setTitle("Frogger");

		Container container = getContentPane(); 
		container.setLayout(new BorderLayout( ));

		frogger = new FroggerComponent("world.txt"); 
		container.add(frogger, BorderLayout.CENTER); 
		frogger.addKeyListener(this); // adding the KeyListener 
		frogger.setFocusable(true); // setting focusable to true
		JPanel panel = new JPanel( ); 
		container.add(panel, BorderLayout.SOUTH);

		newGameButton = new JButton("New Game"); 
		newGameButton.addActionListener(this); 
		newGameButton.setFocusable(false); // focusable should be 
		panel.add(newGameButton); // false for everything else

		harderButton = new JButton("Harder Game");
		harderButton.addActionListener(this); 
		harderButton.setFocusable(false); // focusable should be 
		panel.add(harderButton); // false for everything else
		
		level=1;
		gameLevel = new JLabel("Level "+level);  
		gameLevel.setFocusable(false); // focusable should be 
		panel.add(gameLevel); // false for everything else


		timer = new javax.swing.Timer(DELAY, this); 
		timer.start();
		
		delay=DELAY;
		showGameOver=true;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frogger.requestFocusInWindow( );
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource( ).equals(newGameButton)) {
			frogger.reset("world.txt");
			level=1;
			gameLevel.setText("Level "+ level);
			delay=DELAY;
			timer.setDelay(delay);
			showGameOver=true;
		}
		else if (e.getSource( ).equals(timer)) {
			frogger.tick();
		}
		else if (e.getSource( ).equals(harderButton)) {
			frogger.resetHarderGame("roadrunner.txt");//resetHarderGame
			level=1;
			gameLevel.setText("Level "+ level);
			delay=DELAY;
			timer.setDelay(delay);
			showGameOver=true;
		} 
	}	

	public void keyTyped(KeyEvent e)
		{	}
	public void keyReleased(KeyEvent e) 
		{	}
	public void keyPressed(KeyEvent e)
	{		
		frogger.keyPress(e.getKeyCode( )); 
	}
	private static void checkLevel(){
		if(FroggerComponent.newLevel){
			level++;
			gameLevel.setText("Level "+ level);
			frogger.resetLevel();
			delay = (int)(delay * 0.85);
		    timer.setDelay(delay);
		}
	}
	
	public static void main(String[] args) {
		FroggerFrame frame = new FroggerFrame();
		while(true){
			checkLevel();
			if(FroggerComponent.dead &&showGameOver){
				showGameOver=false;
				JOptionPane.showMessageDialog(frame, "Game Over");
			}
		}
	}
}


	
