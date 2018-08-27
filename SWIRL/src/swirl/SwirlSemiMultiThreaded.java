package swirl;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.awt.*;

import javax.swing.*;
/**
 * Class that controls Semi Multi-threaded implementation of SWIRL 
 *  
 * @author Amir Rasouli
 *
 */
/**
 * @author Amir Rasouli
 *
 */

public class SwirlSemiMultiThreaded {

	/**
	 * Graphics Frame
	 */
	public JFrame frame;

	/**
	 * Internal time for timer
	 */
	long startTime;
	/**
	 * Time elapsed for timer
	 */
	long timeElapsed;
	/**
	 * Global time
	 */
	long globalTime;
	/**
	 * Global Time elapsed
	 */
	long globalTimeElapsed;
	/**
	 * Indicator for completion of initialization phase
	 */
	boolean initialized;
	/**
	 * Sensors locations
	 */
	private Map<Integer,Point> sensorsLocations;
	/**
	 * Robots locations
	 */
	private Map<Character,Point> robotLocations;
	/**
	 * Robots destinations
	 */
	private Map<Character,Integer> robotDestinations;
	/**
	 * Robots color
	 */
	private Map<Character,Color> robotColor;
	/**
	 * List of sensors
	 */
	private final java.util.List<SensorSeq> sensorsList;
	/**
	 * List of robots
	 */
	private final java.util.List<RobotSemiMulti> robotsList;
	/**
	 * A list that keeps the record of whether robots are arrived at their destinations
	 */
	private final BlockingQueue<Boolean> robotIsArrived;
	/**
	 * Graphic components
	 */
	private final ConcurrentMap<Character,Component> graphicComps;
	/**
	 * Next targets for the robots
	 */
	private final ConcurrentMap<Character, Message> targetList;
	/**
	 * List of requests made by the robot
	 */
	private final BlockingQueue<Message> robotsRequestList;
	/**
	 * Graphic Container
	 */
	public Container content;
	/**
	 * Constructor
	 * 
	 * @param sensorLocs sensors locations
	 * @param robotLocs robots locations
	 * @param robotDes robots destinations
	 * @param robotCol robots colors
	 */
	public SwirlSemiMultiThreaded( Map<Integer,Point> sensorLocs,Map<Character,Point> robotLocs,Map<Character,Integer> robotDes,Map<Character,Color> robotCol)
	{
		this.initialized = false;
		this.graphicComps = new ConcurrentHashMap<Character,Component>(); 
		this.robotColor = robotCol;
		this.robotDestinations = robotDes;
		this.robotIsArrived = new ArrayBlockingQueue<Boolean>(SwirlController.NUMBER_OF_ROBOTS);
		this.robotsList = new ArrayList<RobotSemiMulti>();
		this.robotLocations = robotLocs;
		this.robotsRequestList = new ArrayBlockingQueue<Message>(SwirlController.NUMBER_OF_ROBOTS*2);
		this.startTime = System.currentTimeMillis();
		this.sensorsList = new ArrayList<SensorSeq>();
		this.sensorsLocations = sensorLocs;
		this.timeElapsed = 0;
	    this.targetList = new ConcurrentHashMap<Character,Message>();
		if (SwirlController.SAVE_DATA)
		{
			this.content = this.frame.getContentPane();
		}
		if (SwirlController.SHOW_GRAPHICS)
		{
			this.frame = new JFrame();
			this.frame.setBounds(1,1,SwirlController.FRAME_BOUNDX, SwirlController.FRAME_BOUNDY );
			this.frame.setTitle("SemiMultiThreaded Run");
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.frame.getContentPane().setBackground(Color.WHITE);
			this.frame.setVisible(true);
		}
		generateSensorsList();//Circle
		generateRobot();
	}
	// ** Sensor Control */
	/**
	 * Draws sensors
	 */
	public void drawSensorsComp()
	{
		for (int i = 0; i < sensorsList.size();i++)
		{
			DrawSensor s = new DrawSensor((int)this.sensorsLocations.get(i).getX(), 
					(int)sensorsLocations.get(i).getY(), SwirlController.SENSORS_DIAMETER, i, SwirlController.INIT_COLOR, SwirlController.INIT_FONT_COLOR);
			this.graphicComps.put((char)i,this.frame.getContentPane().add(s));
			this.frame.revalidate();
			this.frame.repaint();
		}
	}
	//Circle Constraint	
	/**
	 * Generates list of sensors
	 */
	public void generateSensorsList()
	{
		for ( int i = 0; i < this.sensorsLocations.size();i++)
		{
			SensorSeq s = new SensorSeq(i,this.sensorsLocations.get(i), SwirlController.SENSORS_COM_RANGE,SwirlController.ROBOT_SPEED,this.frame,this.graphicComps);
			this.sensorsList.add(s);
		}
		if (SwirlController.SHOW_GRAPHICS)
		{
			drawSensorsComp();
		}
	}
	//**robot control*/
	/**
	 * Draws robots
	 * 
	 * @param x x cord of the robot
	 * @param y y cord of the robots
	 * @param id robot's ID
	 * @param destination robot's destination
	 * @param orientation robot's orientation
	 * @param col robot's color
	 */
	public void drawRobotComp(int x, int y,char id, int destination, double orientation, Color col)
	{
		String st = Character.toString(id) + Integer.toString(destination);
		DrawRobot r = new DrawRobot(x,y,st,SwirlController.ROBOT_WIDTH,SwirlController.ROBOT_HEIGHT,orientation,col,Color.BLACK);
		this.graphicComps.put(id,this.frame.getContentPane().add(r));
		this.frame.revalidate();
		this.frame.repaint();
	}
	/**
	 * Generates list of robots
	 */
	public void generateRobot()
	{
		for (Map.Entry <Character,Point> entry: this.robotLocations.entrySet())
		{
			this.robotsList.add(new RobotSemiMulti(this.robotDestinations.get(entry.getKey()),SwirlController.ROBOT_SPEED,entry.getValue(),
					entry.getKey(),this.sensorsLocations,
					this.frame,this.graphicComps,this.robotColor.get(entry.getKey()),
					this.robotsRequestList,this.targetList, this.robotIsArrived));
		}
	}
	/**
	 * Initializes robots
	 * @return true if initialization is completed
	 */
	public boolean initialize()
	{
		Map<Integer, java.util.List<Message>> msgList = new HashMap<Integer,java.util.List<Message>>();
		boolean broadcastDone = false;
		while(true)
		{
			for (int i = 0; i < this.sensorsList.size(); i++)
			{
				if (msgList.containsKey(i) || !broadcastDone)
				{
					java.util.List<Message> responses = this.sensorsList.get(i).initialize(this.sensorsLocations, msgList.get(i));
					msgList.remove(i);
					for (int r = 0; r < responses.size(); r++)
					{

						if (responses.get(r) == null)
						{
							continue;
						}
						if (msgList.containsKey(responses.get(r).receiverId))
						{
							msgList.get(responses.get(r).receiverId).add(responses.get(r));
						}else
						{
							java.util.List<Message> init = new java.util.ArrayList<Message>();
							init.add(responses.get(r));
							msgList.put(responses.get(r).receiverId,init);
							delay();
						}

					}

				}
			}
			broadcastDone = true;
			if (msgList.isEmpty())
			{
				if (SwirlController.SHOW_DEBUG_MESSAGES)
				{
					System.out.println("Initialization Complete!");
				}
				break;
			}
		}
		return true;
	}
	/**
	 * Performs estimate transmissions
	 * @return true once done
	 */
	public boolean estimate()
	{
		Map<Integer, java.util.List<Message>> msgList = new HashMap<Integer,java.util.List<Message>>();
		boolean estimateDone = false;
		while(true)
		{
			for (int i = 0; i < this.sensorsList.size(); i++)
			{
				if (msgList.containsKey(i) || !estimateDone)
				{
					java.util.List<Message> responses = this.sensorsList.get(i).estimate(msgList.get(i), estimateDone );
					msgList.remove(i);
					if (responses == null)
					{
						continue;
					}	
					for (int r = 0; r < responses.size(); r++)
					{
						if (responses.get(r) == null)
						{
							continue;
						}
						if (msgList.containsKey(responses.get(r).receiverId))
						{
							msgList.get(responses.get(r).receiverId).add(responses.get(r));
						}else
						{
							java.util.List<Message> init = new java.util.ArrayList<Message>();
							init.add(responses.get(r));
							msgList.put(responses.get(r).receiverId,init);
						}
					}
				}
			}
			estimateDone = true;
			if (msgList.isEmpty())
			{
				if (SwirlController.SHOW_DEBUG_MESSAGES)
				{
					System.out.println("Estimation Complete!");
				}
				break;
			}	
		}
		return true;
	}
	/**
	 * Timer to count time
	 * @param reset true or false to reset time
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
		this.timeElapsed = endTime - this.startTime;
		return (this.timeElapsed/1000);
	}
	/**
	 * Creates delay for graphic interface
	 */
	public void delay()
	{
		long sTime = System.currentTimeMillis();
		double sec =  SwirlController.VISIBILITY_DELAY * 1000;
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
	 * Set destinations of robots
	 */
	public void setDestinations()
	{
		for (int i = 0; i < this.robotsList.size();i++)
		{
			this.robotsList.get(i).setDestination(this.robotDestinations.get(this.robotsList.get(i).myId));
		}
	}
	/**
	 * Creates and runs robots threads
	 */
	public void setThreads()
	{
		for (int i = 0; i < this.robotsList.size();i++)
		{
			new Thread( this.robotsList.get(i), Character.toString(this.robotsList.get(i).myId)).start();
		}	
	}
	/**
	 * Resets robots locations
	 */
	public void resetLocations()
	{
		for (int i = 0; i < this.robotsList.size();i++)
		{
			this.robotsList.get(i).setPosition(this.robotLocations.get(this.robotsList.get(i).myId));
			this.robotsList.get(i).setCurrentLoc(-1);
		}
	}
	/**
	 * Transmits and receives messages to sensors
	 */
	public void execute()
	{	
		this.globalTime = System.currentTimeMillis();
		if(SwirlController.SAVE_DATA)
		{
			SaveImage s = new SaveImage(this.content, SwirlController.PATH_SEMIMULTI);
			new Thread(s,"save").start();
		}
		if (!initialized)
		{
			initialize();
			initialized = true;
		}
		if(SwirlController.DEST_REGEN)
		{
			setDestinations();
		}
		if(SwirlController.RESET_LOCATIONS)
		{
			resetLocations();
		}
		setThreads();
		this.targetList.clear();
		//estimate();
		Message targetMsg = new Message();
		BlockingQueue<Message> rejectList = new ArrayBlockingQueue<Message>(SwirlController.NUMBER_OF_ROBOTS);


		while(true)
		{
			if (timer(SwirlController.STOP) >= SwirlController.ESTIMATE_TIME_INETRVAL)
			{
				if (SwirlController.SHOW_DEBUG_MESSAGES)
				{
					System.out.println("Estimation");
				}
				timer(SwirlController.START);
				estimate();
			}

			if (this.robotIsArrived.size() == SwirlController.NUMBER_OF_ROBOTS)
			{
				System.out.println("All Robots are arrived!");
				this.robotIsArrived.clear();
				break;
			}
			while (!robotsRequestList.isEmpty())
			{
				Message robotMsg = this.robotsRequestList.poll();

				if (robotMsg.type == Message.msgType.QUERY)
				{
					targetMsg= sensorsList.get(robotMsg.receiverId).receive(robotMsg);
					if (targetMsg.targetId == -1)
					{
						rejectList.offer(new Message(robotMsg.robotId,Message.msgType.QUERY,robotMsg.destination,robotMsg.receiverId));
					}
					this.targetList.put(targetMsg.robotId, targetMsg);

				}else if (robotMsg.type == Message.msgType.UPDATE)
				{
					this.sensorsList.get(robotMsg.receiverId).receive(robotMsg);

				}

			}

			while (!rejectList.isEmpty())
			{
				this.robotsRequestList.offer(rejectList.poll());
			}
		}
		this.globalTimeElapsed = System.currentTimeMillis() - globalTime;
		System.out.println("Total time of completion for SemiMultiThreaded run is " + (double)globalTimeElapsed/1000.);
	}

}
