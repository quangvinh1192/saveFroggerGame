/*************************************************************************
 *	Vinh Tran. CIS 35A
* FroggerComponent is the primary class. It stores most of the data and 
* handles the central logic of the game.
 *************************************************************************/
// FroggerComponent.java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.awt.geom.*;
/*
 The main class for Frogger. Stores the state of the world,
 draws it, handles the tick() and key() logic.
*/
public class FroggerComponent extends JComponent  {
	// Size of the game grid
	public static final int WIDTH = 20;
	public static final int HEIGHT = 7;
	
	// Initial pixel size for each grid square
	public static final int PIXELS = 50;
	

	// Image filenames for car, lily, and frog
	public static final String[] IMAGES = new String[] { "car.png", "lily.png" , "frog.png", "frog_right.png", "frog_back.png", "frog_left.png" };
	
	// Colors for ROAD, WATER, and DIRT
	public static final Color[] COLORS = new Color[] { Color.BLACK, Color.BLUE, Color.GRAY } ;
	
	// Codes to store what is in each square in the grid
	public static final int EMPTY = 0;
	public static final int CAR = 1;
	public static final int LILY = 2;

	private Row []rowData;
	private Image carImage;
	private Image [] frogImage;
	private Image lilyImage;
	private int myX;
	private int myY;
	public static boolean dead;
	private static int XY_PIXELS;
	private int frogDirection;
	
	private int [][] controlMoving;
	public static int round;
	public static boolean newLevel;
	
	public FroggerComponent(String world){
		setPreferredSize(new Dimension(1000, 350));
		carImage= readImage(IMAGES[0]);
		lilyImage= readImage(IMAGES[1]);
		frogImage = new Image[4];
		for(int i=0; i<4; i++){
			frogImage[i]=readImage(IMAGES[2+i]);
		}
		controlMoving = new int[HEIGHT][WIDTH];
		loadFile(world); //read file
		round =1;
		myY=6;
		dead=false;
		XY_PIXELS=PIXELS;
	}
	//read data from input file
	private void loadFile(String filename) {
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line ;
			rowData = new Row[HEIGHT];
			for (int y = 1; y < HEIGHT-1; y++){
				line = in.readLine( );
				rowData[y] = new Row(line);
			}
			rowData[0] = new Row("dirt");
			rowData[HEIGHT-1] = new Row("dirt");
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		} 
	}

	public void paintComponent(Graphics g) {
		int squareWidth = getWidth() / WIDTH;
		int squareHeight = getHeight() / (HEIGHT);
		Graphics2D g2 = (Graphics2D) g;
		for (int i=0; i<HEIGHT;i++ ) {//draw color
			if (rowData[i].getType()==1) {
				g2.setColor(COLORS[0]);
				g2.fill(new Rectangle2D.Double(0,squareHeight*i,getWidth(),squareHeight));
			}
			else if(rowData[i].getType()==2){
				g2.setColor(COLORS[1]);
				g2.fill(new Rectangle2D.Double(0,squareHeight*i,getWidth(),squareHeight));
			}
			else{
				g2.setColor(COLORS[2]);
				g2.fill(new Rectangle2D.Double(0,squareHeight*i,getWidth(),squareHeight));
			}
		}
		
		
		for(int i=0; i<HEIGHT; i++){//draw cars and lily
			for(int j=0; j<WIDTH; j++){
				if(controlMoving[i][j]==CAR){			
					g.drawImage(carImage,j*squareWidth,squareHeight*i, squareWidth,squareHeight,null);
				}
				else if(controlMoving[i][j]==LILY){
					g.drawImage(lilyImage,j*squareWidth,squareHeight*i, squareWidth,squareHeight,null);
				}
			}
		}
		if(dead==true){
			((Graphics2D) g).setStroke(new BasicStroke(5,BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
			g.setColor(Color.RED);
			g.drawLine(myX*squareWidth, myY*squareHeight, myX*squareWidth+squareWidth, myY*squareHeight+squareHeight);
			g.drawLine(myX*squareWidth, myY*squareHeight+squareHeight, myX*squareWidth+squareWidth, myY*squareHeight);
		}
		g.drawImage(frogImage[frogDirection], myX*squareWidth, myY*squareHeight, squareWidth, squareHeight, null);
	}

	public void reset(String filename)//reset a new game
	{
		loadFile(filename); //read file
		for(int i=0; i<HEIGHT; i++){
			for(int j=0; j<WIDTH; j++){
				controlMoving[i][j]=0;//reset all the data to 0
			}
		}
		dead=false;//reset all variables
		round =1;
		myX=0;
		myY=6;
		newLevel=false;
	}

	public void resetLevel(){//reset the value of myY when users hit the other dirt
		myY=6;
		newLevel=false;
	}
	public void keyPress(int code) {
		if(dead!=true){
			if (code == KeyEvent.VK_UP) {
				frogDirection=0;//control the image of this frog
				if(myY==0)
					newLevel= true;
				if(myY>0)
					myY += -1;
			} 
			else if (code == KeyEvent.VK_DOWN) {
				frogDirection=2;
				if(myY!=6)
					myY += 1;
				} 
			else if (code == KeyEvent.VK_LEFT) {
				frogDirection=3;
				if(myX>0)
					myX += -1;
				else if(myX==0 && (rowData[myY].getType()==3 ||rowData[myY].getType()==1))
					;//do nothing
				else//x<0
					dead=true;
			} 
			else if (code == KeyEvent.VK_RIGHT) {
				frogDirection=1;
				if(myX<19)
					myX += 1;
				else if(myX==19 && (rowData[myY].getType()==3 ||rowData[myY].getType()==1))
					;//do nothing
				else//x>19
					dead=true;
			}
		}
		
		checkCollisions( );
		repaint();
	}
	//read images from input files
	private Image readImage(String filename) {
		Image image = null;
		try {
			image = ImageIO.read(new File(filename));
		}
		catch (IOException e) {
			System.out.println("Failed to load image '" + filename + "'");
			e.printStackTrace();
		}
		return(image);
	}
	//control everything, add a new element, move it
	public void tick( ) {
		if(dead)//if the frog is dead, stop everything
			return;

		for(int i=0; i<HEIGHT; i++){
			if(controlMoving[i][19]!=EMPTY){//change the last one to 0
				controlMoving[i][19]=EMPTY;				
				}
			if(i==myY&& rowData[myY].getType()==LILY){//the lily
				if(rowData[i].isTurn(round)){
					for(int j=19; j>0; j--){
						controlMoving[i][j]= controlMoving[i][j-1];							
						controlMoving[i][j-1]=EMPTY;
					}
					if(myX+1==20)
						dead=true;//frog will die b/c of out of the range
					if(myX<19)
						myX += 1;//frog moves with the lily
				}
			}				
			else if(i==myY&& rowData[myY].getType()==CAR)
			{//frog in the high way
				for(int j=19; j>0; j--){
					controlMoving[i][j]= controlMoving[i][j-1];
					controlMoving[i][j-1]=0;						
					if(j==myX && controlMoving[i][j]==1)//hits the car
						dead=true;
				}
			}
			else{//normal road
				if(rowData[i].isTurn(round)){						
					for(int j=19; j>0; j--){
						controlMoving[i][j]= controlMoving[i][j-1];
						controlMoving[i][j-1]=EMPTY;
					}
				}
			}				
			if(rowData[i].isAdd())// checking isAdd
			{
				controlMoving[i][0]=rowData[i].getType();
				if(controlMoving[myY][0]==1 && myX==0){
					dead=true;					
				}
			}
		}
			
			
			round++;	
			repaint( ); 
		}
	
	private void checkCollisions( ){
		
		if(rowData[myY].getType()==1){
			if(controlMoving[myY][myX]==1){
				dead=true;
			}
		}
		else if(rowData[myY].getType()==2){
			if(controlMoving[myY][myX]==0){
				dead=true;
			}
		}
	}
	
	public void resetHarderGame(String filename){
		loadFile(filename); //read file
		for(int i=0; i<HEIGHT; i++){
			for(int j=0; j<WIDTH; j++){
				controlMoving[i][j]=0;//reset all the data to 0
			}
		}
		dead=false;//reset all variables
		round =1;
		myX=0;
		myY=6;
		newLevel=false;
	}
}


