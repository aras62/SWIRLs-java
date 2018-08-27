package swirl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.*;

import javax.swing.JComponent;
/**
 * Class that performs drawing of robots
 * @author Amir Rasouli
 *
 */
public class DrawRobot extends JComponent{
	private static final long serialVersionUID = 1L;
	/**
	 * x cord of the robot
	 */
	private int x;
	/**
	 * y cord of the robot
	 */
	private int y;
	/**
	 * Robot's ID
	 */
	private String id;
	/**
	 * Robots Color
	 */
	private Color robotColor;
	/**
	 * Robots ID color
	 */
	private Color  idColor;
	/**
	 * Robots width
	 */
	private int robotWidth;
	/**
	 * Robot's height
	 */
	private int  robotHeight;
	/**
	 * Robot's orientation
	 */
	private double orientation; 
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.rotate(this.orientation, (double)this.x,(double)this.y);

		// Wheels
		g2.setColor(Color.BLACK);
		g2.fillRect(this.x, this.y-(int)robotHeight/4, (int)2*robotWidth/5, (int)robotHeight/4);//W1
		g2.fillRect(this.x + robotWidth - (int)2*robotWidth/5, this.y-(int)robotHeight/4, (int)2*robotWidth/5, (int)robotHeight/4);//W1
		g2.fillRect(this.x, this.y + robotHeight, (int)2*robotWidth/5, (int)robotHeight/4);//W1
		g2.fillRect(this.x + robotWidth -(int)2*robotWidth/5 , this.y + robotHeight, (int)2*robotWidth/5, (int)robotHeight/4);//W1
		//body
		g2.setColor(robotColor);
		g2.fillRect(this.x, this.y, robotWidth, robotHeight);
		//Camera
		g2.setColor(Color.YELLOW);
		g2.fillRect(this.x+(int)robotWidth/5, this.y + (int)robotHeight/4,(int) robotWidth/2, (int)robotHeight/2);
		g2.setFont(new Font("TimesRoman", Font.PLAIN, (int)robotWidth*robotHeight/100));
		g2.setColor(idColor);
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(id, g2);
		g2.drawString(id, (int) (this.x+(int)2*robotWidth/5- rect.getWidth()/2),
				(int) (this.y + (int)2*robotHeight/5 + rect.getHeight()/2));
		g2.rotate(-orientation);
	}
	/**
	 * Constructor
	 * 
	 * @param xIn x cord 
	 * @param yIn y cord
	 * @param idIn ID
	 * @param rWidth width
	 * @param rHeight height
	 * @param orient orientation
	 * @param robotColorIn robot's color
	 * @param idColorIn robot's ID color
	 */
	public DrawRobot(int xIn, int yIn,  String idIn, int rWidth, int rHeight, double orient, Color robotColorIn, Color idColorIn)
	{
		this.x = xIn ;
		this.y = yIn;
		this.id = idIn;
		this.robotColor = robotColorIn;
		this.idColor = idColorIn;
		this.robotWidth =rWidth;
		this.robotHeight = rHeight;
		this.orientation = orient;
	}
}
