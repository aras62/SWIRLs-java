package swirl;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
/**
 * Draws sensor component
 * @author Amir Rasouli
 *
 */
public class DrawSensor extends JComponent{
	private static final long serialVersionUID = 1L;
	/**
	 * x cord
	 */
	private int x;
	/**
	 * y cord
	 */
	private int y;
	/**
	 * Radius of the sensor
	 */
	private int radius;
	/**
	 * Sensor's ID
	 */
	private int id;
	/**
	 * Sensor's color
	 */
	private Color sensorColor;
	/**
	 * Sensor's ID color
	 */
	private Color idColor;
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(sensorColor);
		g2.fillOval(x, y, radius, radius);
		g2.setColor(Color.BLUE);
		g2.drawOval(x, y, radius, radius);
		g2.setFont(new Font("TimesRoman", Font.PLAIN, (int)2*radius/3));
		g2.setColor(idColor);
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(Integer.toString(id), g2);
		g2.drawString(Integer.toString(id), (int) (x+1 + radius/2 - rect.getWidth()/2),
				(int) (y-5 + radius/2 + rect.getHeight()/2));


	}
	/**
	 * Constructor
	 * 
	 * @param xIn x cord
	 * @param yIn y cord
	 * @param radiusIn sensor's radius
	 * @param idIn sensor's id
	 * @param sensorColorIn sensor's color
	 * @param idColorIn sensor's ID color
	 */
	public DrawSensor(int xIn, int yIn, int radiusIn, int idIn, Color sensorColorIn, Color idColorIn)
	{
		this.x = xIn ;
		this.y = yIn;
		this.radius = radiusIn;
		this.id = idIn;
		this.sensorColor = sensorColorIn;
		this.idColor = idColorIn;
	}
}
