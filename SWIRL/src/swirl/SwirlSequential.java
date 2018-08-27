package swirl;
import java.util.*;
import java.awt.*;

import javax.swing.*;
public class SwirlSequential {
	/**
	 * Graphics Frame
	 */
	private JFrame frame;
	/**
	 * Internal time for timer
	 */
	private long startTime;
	/**
	 * Time elapsed for timer
	 */
	private long timeElapsed;
	/**
	 * Global time
	 */
	private long globalTime;
	/**
	 * Global Time elapsed
	 */
	private long globalTimeElapsed;
	/**
	 * Indicator for completion of initialization phase
	 */
	private boolean initialized;

	/**
	 * List of robots arrive at their destinations
	 */
	private java.util.List<Character> arrivedList;
	/**
	 * Sensors locations
	 */
	private final Map<Integer,Point> sensorsLocations;
	/**
	 * Robots locations
	 */
	private final Map<Character,Point> robotLocations;
	/**
	 * Robots destinations
	 */
	private final Map<Character,Integer> robotDestinations;
	/**
	 * Robots color
	 */
	private final Map<Character,Color> robotColor;
	/**
	 * List of sensors
	 */
	private final java.util.List<SensorSeq> sensorsList;
	/**
	 * List of robots
	 */
	private final java.util.List<RobotSeq> robotsList;

	/**
	 * Graphic components
	 */
	private final Map<Character,Component> graphicComps; 
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
	 * @param robotCol robots color
	 */
	public SwirlSequential(Map<Integer,Point> sensorLocs,Map<Character,Point> robotLocs,Map<Character,Integer> robotDes,Map<Character,Color> robotCol)
	{
		this.arrivedList = new ArrayList<Character>();
		this.initialized = false;
		this.graphicComps = new HashMap<Character,Component>(); 
		this.robotColor = robotCol;
		this.robotDestinations = robotDes;
		this.robotLocations = robotLocs;
		this.robotsList = new ArrayList<RobotSeq>();
		this.startTime = System.currentTimeMillis();
		this.sensorsList = new ArrayList<SensorSeq>();
		this.sensorsLocations = sensorLocs;
		this.timeElapsed = 0;
		if (SwirlController.SAVE_DATA)
		{
			this.content =this.frame.getContentPane();
		}
		if (SwirlController.SHOW_GRAPHICS)
		{
			this.frame = new JFrame();
			this.frame.setBounds(1,1,SwirlController.FRAME_BOUNDX, SwirlController.FRAME_BOUNDY);
			this.frame.setTitle("Sequential Run");
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
		for (int i = 0; i < this.sensorsList.size();i++)
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
	 * Generates sensors list
	 */
	public void generateSensorsList()
	{
		for ( int i = 0; i < this.sensorsLocations.size();i++)
		{
			SensorSeq s = new SensorSeq(i,this.sensorsLocations.get(i), SwirlController.SENSORS_COM_RANGE,SwirlController.ROBOT_SPEED,frame,graphicComps);
			this.sensorsList.add(s);
		}
		if (SwirlController.SHOW_GRAPHICS)
		{
			drawSensorsComp();
		}
	}

	//**robot control*/
	/**
	 * Draw robots
	 * 
	 * @param x x cord of the robot
	 * @param y y cord of the robot
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
	 * Generates robots
	 */
	public void generateRobot()
	{
		for (Map.Entry <Character,Point> entry: this.robotLocations.entrySet())
		{
			this.robotsList.add(new RobotSeq(this.robotDestinations.get(entry.getKey()),SwirlController.ROBOT_SPEED,entry.getValue(),
					entry.getKey(),this.sensorsLocations,
					this.frame,this.graphicComps,robotColor.get(entry.getKey()), this.arrivedList));
			if (SwirlController.SHOW_GRAPHICS)
			{
				drawRobotComp(entry.getValue().x, entry.getValue().y,entry.getKey(),this.robotDestinations.get(entry.getKey()), 0.,robotColor.get(entry.getKey()));
			}
		}
	}
	/**
	 * Initializes Sensors
	 * @return true once done
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
					java.util.List<Message> responses = sensorsList.get(i).initialize(sensorsLocations, msgList.get(i));
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
	 * Performs estimation
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
	 * Counts time
	 * @param reset  reset timer
	 * @return time elapsed
	 */
	public long timer(boolean reset)
	{
		if (reset)
		{
			this.startTime = System.currentTimeMillis();
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
	 * Sets robot's internal timer
	 */
	public void setRobotClocks()
	{
		for (int i = 0; i < robotsList.size(); i++)
		{
			this.robotsList.get(i).globalTimer(SwirlController.START);
		}
	}
	/**
	 * Sets robots destinations
	 */
	public void setDestinations()
	{
		for (int i = 0; i < this.robotsList.size();i++)
		{
			this.robotsList.get(i).setDestination(this.robotDestinations.get(this.robotsList.get(i).myId));
		}
	}
	/**
	 * Resets robots locations
	 */
	public void resetLocations()
	{
		for (int i = 0; i < this.robotsList.size();i++)
		{
			this.robotsList.get(i).setPosition( this.robotLocations.get(this.robotsList.get(i).myId));
			this.robotsList.get(i).setCurrentLoc(-1);
		}
	}
	/**
	 * Transmits messages between components
	 */
	public void execute()
	{	
		this.globalTime = System.currentTimeMillis();
		if(SwirlController.SAVE_DATA)
		{
			SaveImage s = new SaveImage(content, SwirlController.PATH_SEQ);
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
		setRobotClocks();
		//estimate();
		java.util.List<Message> robotsRequestList = new ArrayList<Message>();
		java.util.List<Message> requestList = new ArrayList<Message>();
		Map<Character, Message> targetList = new HashMap<Character,Message>(); 
		Message targetMsg = new Message();


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

			for (int rIdx = 0; rIdx < this.robotsList.size(); rIdx++)
			{
				requestList = this.robotsList.get(rIdx).operate(targetList.get(this.robotsList.get(rIdx).myId));

				if (requestList!= null)
				{
					robotsRequestList.addAll(requestList);
				}
			}
			if (this.arrivedList.size() == this.robotsList.size())
			{
				System.out.println("All Robots arrived!");
				this.arrivedList.clear();
				break;
			}
			targetList.clear();
			if (robotsRequestList == null|| robotsRequestList.isEmpty())
			{
				continue;
			}

			for (int m = 0 ; m < robotsRequestList.size(); m++)
			{
				if (robotsRequestList.get(m).type == Message.msgType.QUERY)
				{
					targetMsg= sensorsList.get(robotsRequestList.get(m).receiverId).receive(robotsRequestList.get(m));
					targetList.put(targetMsg.robotId, targetMsg);

				}else if (robotsRequestList.get(m).type == Message.msgType.UPDATE)
				{
					this.sensorsList.get(robotsRequestList.get(m).receiverId).receive(robotsRequestList.get(m));

				}
			}
			robotsRequestList.clear();

		}
		this.globalTimeElapsed = System.currentTimeMillis() - this.globalTime;
		System.out.println("Total time of completion for Sequential run is " + (double)this.globalTimeElapsed/1000.);

	}

}





