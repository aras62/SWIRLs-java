package swirl;
import java.util.*;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
/**
 * Robot Class for sequential implementation
 * @author Amir Rasouli
 *
 */
public class RobotSeq {
	/**
	 * 
	 * @param dest destination
	 * @param speed speed
	 * @param loc location
	 * @param id ID 
	 * @param sList sensors list
	 * @param f graphic frame
	 * @param gc graphic components
	 * @param col robot's color
	 * @param aList list of robots arrived
	 */
	public RobotSeq(int dest, int speed,Point loc, char id, 
			Map<Integer,Point> sList, JFrame f, 
			Map<Character,Component> gc, Color col, java.util.List<Character> aList)
	{
		this.arrivedList = aList;
		this.currentLoc = -1;
		this.destination = dest;
		this.displacement = speed; 
		this.distToDest = -1;
		this.distToNext = -1;
		this.frame = f;
		this.graphics = gc;
		this.myColor = col;
		this.myId = id;
		this.nextLoc = -1;
		this.orientation = 0;
		this.position = loc;
		this.sensorsList = sList;
		this.rand = new Random();
		this.totalTimeElapsed = 0;
		this.waitForQuery = false;
		if (SwirlController.SHOW_DEBUG_MESSAGES)
		{
			System.out.println("Robot "+ this.myId + "'s destination is " + this.destination);
		}
	}
	/**
	 * robot's destination
	 */
	private int destination;
	/**
	 * Gets for destination
	 * @return destination
	 */
	public int getDestination() {
		return destination;
	}
	/**
	 * Sets the robot's destination
	 * @param destination
	 */
	public void setDestination(int destination) {
		this.destination = destination;
	}
	/**
	 * Gets current location of the robot
	 * @return current location
	 */
	public int getCurrentLoc() {
		return currentLoc;
	}
	/**
	 * Set current location of the robot
	 * @param currentLoc
	 */
	public void setCurrentLoc(int currentLoc) {
		this.currentLoc = currentLoc;
	}
	/**
	 * Gets robot's position
	 * @return
	 */
	public Point getPosition() {
		return position;
	}
	/**
	 * Sets robot position
	 * @param position
	 */
	public void setPosition(Point position) {
		this.position = position;
	}
	/**
	 * Current location of the robot
	 */
	private int currentLoc;
	/**
	 * Next location to move
	 */
	private int nextLoc;
	/**
	 * Robot's speed in pixels
	 */
	private int displacement;
	/**
	 * Time elapsed
	 */
	private long timeElapsed;
	/**
	 * Start time
	 */
	private long startTime;
	/**
	 * Start global timer
	 */
	private long startTimeGlobal;
	/**
	 * Total time elapsed
	 */
	private double totalTimeElapsed;
	/**
	 * Distance to destination
	 */
	private double distToDest;
	/**
	 * Distance to next location
	 */
	private double distToNext;
	/**
	 * Robot's orientation
	 */
	private double orientation;
	/**
	 * Robot's position
	 */
	private Point position;
    /**
	 * List of robots arrived
	 */
	private final java.util.List<Character> arrivedList;
	/**
	 * Robot's ID
	 */
	public char myId;
	/**
	 * Robot's color
	 */
	public Color myColor;
	/**
	 * Graphic frame
	 */
	public JFrame frame;
	/**
	 * List of graphic components
	 */
	private Map<Character,Component> graphics;
	/**
	 * Wait for query from corresponding sensor
	 */
	public boolean waitForQuery;
	/**
	 * List of sensors
	 */
	private Map<Integer,Point> sensorsList;
	/**
	 * Random number generator
	 */
	public Random rand;
	/**
	 * Timer
	 * @param reset true to reset
	 * @return time elapsed
	 */
	public long timer(boolean reset)
	{
		if (reset)
		{
			startTime = System.currentTimeMillis();
			return -1;
		}
		long endTime = System.currentTimeMillis();
		timeElapsed = endTime - startTime;
		return timeElapsed;
	}
	/**
	 * Global timer
	 * @param reset true to reset
	 * @return global time elapsed
	 */
	public double globalTimer(boolean reset)
	{
		if (reset)
		{
			startTimeGlobal = System.currentTimeMillis();
			return -1;
		}
		long endTime = System.currentTimeMillis();
		totalTimeElapsed = (double)(endTime - startTimeGlobal);
		return totalTimeElapsed/1000.;
	}
	/**
	 * Finds the closest sensor to the robot
	 * @param sensorsList
	 * @return closest sensor ID
	 */
	public int findClosestNeighbor(Map<Integer,Point> sensorsList)
	{
		double minDist = -1;
		int closestId = -1;
		for (Map.Entry<Integer,Point> entry : sensorsList.entrySet())		
		{
			double dist = Math.sqrt( 
					Math.pow((position.getX()- entry.getValue().getX()),2)+
					Math.pow((position.getY()- entry.getValue().getY()),2)
					);	
			if (minDist < 0 || minDist > dist)
			{
				minDist = dist;
				closestId = entry.getKey();
			}
		}
		return closestId;
	}
	/**
	 * Calculates the distance between two points
	 * @param p1
	 * @param p2
	 * @return distance in pixels
	 */
	private double getDistance(Point p1, Point p2)
	{
		return	Math.sqrt( 
				Math.pow((p1.getX()- p2.getX()),2)+
				Math.pow((p1.getY()- p2.getY()),2)
				);	
	}
	/**
	 * Calculates the orientation of the robot
	 * @param p
	 * @return orientation in radian
	 */
	private double getOrientation (Point p)
	{
		double angle = Math.atan2(p.getY()-this.position.getY(), p.getX()-this.position.getX());
		return angle;
	}
	/**
	 * Creates delay for the graphic
	 * @param sec
	 */
	private void delay(double sec)
	{
		long sTime = System.currentTimeMillis();
		sec *= 1000;
		while(true)
		{
			long eTime = System.currentTimeMillis();
			if((eTime - sTime) >= (long)sec)
			{
				break;
			}
		}
	}
	/**
	 * Handles graphics thread
	 * @param nLoc next location to move
	 * @return true if executed properly
	 */
	private boolean setupGraphics(Point nLoc)
	{
		if (!SwirlController.SHOW_GRAPHICS)
		{
			return false;
		}

		SwingWorker<Boolean, DrawRobot> worker = new SwingWorker<Boolean, DrawRobot>()
				{
			protected Boolean doInBackground() throws Exception 
			{

				int x =position.x;
				int y = position.y;

				String st = Character.toString(myId)+ Integer.toString(destination);
				DrawRobot r = new DrawRobot(x,y,st,SwirlController.ROBOT_WIDTH,SwirlController.ROBOT_HEIGHT,orientation,myColor,Color.BLACK);
				publish(r);
				return true;
			}
			protected void process(java.util.List<DrawRobot> chunks) {
				// Here we receive the values that we publish().
				// They may come grouped in chunks.
				frame.getContentPane().remove(graphics.get(myId));
				frame.revalidate();
				frame.repaint();
				graphics.put(myId,frame.getContentPane().add(chunks.get(chunks.size()-1)));
				frame.revalidate();
				frame.repaint();
			}
				};

				worker.execute();
				return true;
	}
	/**
	 * moves to the next location
	 * @return true if done
	 */
	private boolean move() 
	{
		Point nLoc = this.sensorsList.get(this.nextLoc);
		this.distToNext = getDistance(this.position,nLoc);	
		setupGraphics(nLoc);
		timer(SwirlController.START);
		int moveSpeed = displacement;
		double d =2./moveSpeed ;
		while (this.distToNext > SwirlController.DELTA)
		{
			if (this.distToNext <= displacement)
			{
				moveSpeed = (int)(this.distToNext - SwirlController.DELTA + 1);
				d= 2./moveSpeed;
			}
			this.position.setLocation(this.position.getX()+ 2 *Math.cos(this.orientation),this.position.getY()+ 2 *Math.sin(this.orientation));
			this.orientation = getOrientation(this.sensorsList.get(this.nextLoc));
			setupGraphics(nLoc);
			delay(d);
			this.distToNext = getDistance(this.position,nLoc);
		}
		timer(SwirlController.STOP);
		return true;
	}
	/**
	 * sets the new destination
	 * @param destin
	 */
	private void newDestination(int destin)
	{
		this.destination = destin;
		System.out.println("new destination for " + this.myId + " is " + this.destination);
		setupGraphics(sensorsList.get(destin));
		globalTimer(SwirlController.START);
	}
	/**
	 * Selects the next target
	 * @param m message received
	 * @return true if set correctly
	 */
	private boolean targetSelection(Message m){

		if (m.targetId == -1)
		{
			if (!this.waitForQuery)
			{
				this.waitForQuery = true;
				if (SwirlController.SHOW_DEBUG_MESSAGES)
				{
					System.out.println("Sensor " + m.senderId+ " does not know destination " + this.destination);
					System.out.println("Inquire Again");
				}
			}
			return false;
		}
		else 
		{
			waitForQuery = false;
			if (SwirlController.SHOW_DEBUG_MESSAGES)
			{
				System.out.println("New Target for " + myId + " is " + m.targetId);
			}
			return true;
		}
	}
	/**
	 * checks to see if the robot is arrived
	 * @return lsit of messages
	 */
	private java.util.List<Message> amIArrived()
	{
		if (!this.arrivedList.contains(this.myId))
		{
			System.out.println("Robot " + this.myId + " has arrived at destination " + this.destination
					+ " with total time of "+ globalTimer(SwirlController.STOP)+ "!");
			this.arrivedList.add(this.myId);
		}
		if  (SwirlController.CHOOSE_NEW_DESTINATION)
		{
			int dest = rand.nextInt(sensorsList.size());
			if (dest != this.destination)
			{
				newDestination(dest);
				java.util.List<Message> response = new ArrayList<Message>();
				response.add(new Message( this.myId,Message.msgType.QUERY, this.destination, this.currentLoc));
				return response;
			}
		}
		return null;
	}
	/**
	 * Runs the robot
	 * @param msg messages sent by sensors
	 * @return message from the robot to sensors
	 */
	public java.util.List<Message> operate(Message msg) 
	{
		if(this.arrivedList.contains(this.myId))
		{
			return null;
		}
		java.util.List<Message> response = new ArrayList<Message>();
		if (this.currentLoc == -1 || msg == null)
		{
			this.nextLoc = findClosestNeighbor(this.sensorsList);
			if (SwirlController.SHOW_DEBUG_MESSAGES)
			{
				System.out.println("Robot " + this.myId +" is moving toward closest sensor " + this.nextLoc);
			}
			move();
		}else
		{	
			if (!targetSelection(msg))
			{
				response.add(new Message( this.myId,Message.msgType.QUERY, this.destination, this.currentLoc));
				return response;
			}
			nextLoc = msg.targetId;	
			move();
			response.add(new Message(this.myId,Message.msgType.UPDATE,this.timeElapsed, msg.senderId,msg.targetId, this.destination));
		}
		this.currentLoc = this.nextLoc;
		this.distToDest = getDistance(this.position, this.sensorsList.get(this.destination));
		if (this.distToDest > SwirlController.DELTA )
		{
			if (!waitForQuery && SwirlController.SHOW_DEBUG_MESSAGES)
			{
				System.out.println("Query from " + this.currentLoc + " by "+ this.myId +" for destination " + this.destination);
			}
			response.add(new Message( this.myId,Message.msgType.QUERY, this.destination, this.currentLoc));
		}else
		{
			return amIArrived();
		}

		return response;

	}

}
