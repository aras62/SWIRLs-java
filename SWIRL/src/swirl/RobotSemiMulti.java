package swirl;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
/**
 * Robot class for Semi multi-Threaded implementation
 * @author Amir Rasouli
 *
 */
public class RobotSemiMulti implements Runnable {
	/**
	 * 
	 * @param dest destination
	 * @param speed speed
	 * @param loc location
	 * @param id ID 
	 * @param sList sensors list
	 * @param f graphic frame
	 * @param gc graphic components
	 *  * @param col robot's color
	 * @param requestList list of robots requests
	 * @param tList targets list
	 * @param rIsA list of robots arrived
	 */
	public RobotSemiMulti(int dest, int speed,Point loc, char id, 
			Map<Integer,Point> sList,JFrame f, ConcurrentMap<Character,Component> gc, Color col,
			BlockingQueue<Message> requestList,	Map<Character, Message> tList, BlockingQueue<Boolean> rIsA)
	{
		this.currentLoc = -1;
		this.destination = dest;
		this.displacement = speed; //(int)Math.sqrt(Math.pow(speed, 2)/2);
		this.distToDest = -1;
		this.distToNext = -1;
		this.firstDestination = true;
		this.frame = f;
		this.graphics = gc;
		this.myColor = col;
		this.myId = id;
		this.nextLoc = -1;
		this.orientation = 0;
		this.position = loc;
		this.robotIsArrived = rIsA;
		this.robotsRequestList = requestList;
		this.sensorsList = sList;
		this.startTime = 0;
		this.startTimeGlobal = 0;
		this.targetList = tList;
		this.totalTimeElapsed = 0;
		this.waitForQuery = false;
		if (SwirlController.SHOW_DEBUG_MESSAGES)
		{
			System.out.println("Robot "+ this.myId + "'s destination is " + this.destination);
		}
		if (SwirlController.SHOW_GRAPHICS)
		{
			String st = Character.toString(this.myId) + Integer.toString(destination);
			DrawRobot r = new DrawRobot(this.position.x,this.position.y,st,SwirlController.ROBOT_WIDTH,SwirlController.ROBOT_HEIGHT,orientation,col,Color.BLACK);
			graphics.put(id,frame.getContentPane().add(r));
			frame.revalidate();
			frame.repaint();
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
	 * Initialize destination
	 */
	private boolean firstDestination;
	/**
	 * Wait for query from corresponding sensor
	 */
	private boolean waitForQuery;
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
	 * List of sensors
	 */
	private Map<Integer,Point> sensorsList;
	/**
	 * Random number generate
	 */
	Random rand = new Random();
	/**
	 * List of robots arrived
	 */
	private final BlockingQueue<Boolean> robotIsArrived;
	/**
	 * List of graphic components
	 */
	private final ConcurrentMap<Character,Component> graphics;
	/**
	 * List of robots request
	 */
	private final BlockingQueue<Message> robotsRequestList;
	/**
	 * targets list
	 */
	private final Map<Character, Message> targetList;
	/**
	 * timer
	 * @param reset true to reset
	 * @return time elapsed
	 */
	private long timer(boolean reset)
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
	private double globalTimer(boolean reset)
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
	 * Find closest sensor to the robot
	 * @param sensorsList
	 * @return closest sensor's ID
	 */
	private int findClosestNeighbor(Map<Integer,Point> sensorsList)
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
	 * Calculates distance between two points
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
	 * Calculates orientation of the robot
	 * @param p
	 * @return orientation in radian
	 */
	private double getOrientation (Point p)
	{
		double angle = Math.atan2(p.getY()-this.position.getY(), p.getX()-this.position.getX());
		return angle;
	}
	/**
	 * Creates delay for graphic
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
	 * Handles Graphics Thread
	 * @return true if executed properly
	 */
	private boolean setupGraphics()
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
	 * Moves to the next location
	 * @return true if done
	 */
	private boolean move() 
	{
		Point nLoc = this.sensorsList.get(this.nextLoc);
		this.distToNext = getDistance(this.position,nLoc);	
		setupGraphics();
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
			this.position.setLocation(this.position.getX()+ 2 *Math.cos(this.orientation),this.position.getY()+ 2 *Math.sin(this.orientation));// 
			this.orientation = getOrientation(this.sensorsList.get(this.nextLoc));
			setupGraphics();
			delay(d);
			this.distToNext = getDistance(this.position,nLoc);
		}
		timer(SwirlController.STOP);
		return true;
	}
	/**
	 * Sets the new destination
	 * @param destin
	 */
	private void newDestination(int destin)
	{
		this.destination = destin;
		System.out.println("Robot "+ this.myId + "'s new destination is " + this.destination);
		globalTimer(SwirlController.START);
	}
	/**
	 * Selects the next target
	 */
	private void targetSelection(){

		while (!this.targetList.containsKey(myId)|| this.targetList.get(this.myId).targetId == -1)
		{
			if (!this.waitForQuery)
			{
				this.waitForQuery = true;
				if (SwirlController.SHOW_DEBUG_MESSAGES)
				{
					System.out.println("Robot " + myId + " is waiting for target message from " + this.nextLoc + " for " + this.destination);
				}
			}
		}
		this.waitForQuery = false;
		this.nextLoc = targetList.get(this.myId).targetId;
		targetList.remove(this.myId);
		if (SwirlController.SHOW_DEBUG_MESSAGES)
		{
			System.out.println("New Target for " + myId + " is " + this.nextLoc);
		}
	}
	/**
	 * Sends query message
	 */
	private void sendQuery()
	{
		this.distToDest = getDistance(this.position, this.sensorsList.get(this.destination));
		if (this.distToDest > SwirlController.DELTA )
		{
			if (SwirlController.SHOW_DEBUG_MESSAGES)
			{
				System.out.println("Query from " + this.currentLoc + " by "+ myId +" for destination " + this.destination);
			}
			this.robotsRequestList.offer (new Message(this.myId,Message.msgType.QUERY,	this.destination,this.currentLoc));
		}
	}
	/**
	 * Drives to the next location
	 */
	private void driveToNextLocation()
	{
		if (this.currentLoc == -1)
		{
			this.nextLoc = findClosestNeighbor(this.sensorsList);
			if (SwirlController.SHOW_DEBUG_MESSAGES)
			{
				System.out.println("Robot " + myId +" is moving toward closest sensor " + nextLoc);
			}
			move();
		}else
		{	
			targetSelection();
			move();

			this.robotsRequestList.offer(new Message(this.myId,Message.msgType.UPDATE,this.timeElapsed,	this.currentLoc,this.nextLoc, this.destination));
		}

		this.currentLoc = this.nextLoc;
		sendQuery();
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		globalTimer(SwirlController.START);

		if(!this.firstDestination)
		{
			sendQuery();
		}
		while (true)
		{
			this.distToDest = getDistance(this.position, this.sensorsList.get(this.destination));
			if (this.distToDest <= SwirlController.DELTA)
			{ 
				System.out.println("Robot " + myId + " has arrived at destination " + destination + " with total time of " + globalTimer(SwirlController.STOP) + "!");
				if (SwirlController.CHOOSE_NEW_DESTINATION)
				{
					int dest = rand.nextInt(sensorsList.size()-1);
					if (dest != this.destination)
					{
						this.destination = dest;
						this.newDestination(dest);
						setupGraphics();
						sendQuery();
					}else
					{
						continue;
					}
				}else
				{
					robotIsArrived.offer(true);
					if(!SwirlController.RESET_LOCATIONS)
					{
						this.firstDestination = false;
					}
					break;
				}
			}
			driveToNextLocation();
		}
	}
}
