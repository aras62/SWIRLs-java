package swirl;
import java.util.*;
import java.util.concurrent.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
/**
 * This class implements Muti-threaded implemenation of SWIRL using a single channel of communication
 * 
 * @author Amir Rasouli
 *
 */
public class SwirlMultiThreadSingleChannel {
	/**
	 * Graphics Frame
	 */
	public JFrame frame;
	/**
	 * Constructor
	 * 
	 * @param sensorLocs location of sensors
	 * @param robotLocs location of robots
	 * @param robotDes robots destinations
	 * @param robotCol robots color
	 */
	public SwirlMultiThreadSingleChannel(Map<Integer,Point> sensorLocs,Map<Character,Point> robotLocs,Map<Character,Integer> robotDes,Map<Character,Color> robotCol)
	{
		this.graphicComps = new ConcurrentHashMap<Character,Component>(); 
		this.msgList =  new HashMap<Integer,BlockingQueue<Message>>();
		this.robotColor = robotCol;
		this.robotDestinations = robotDes;
		this.robotIsArrived = new ArrayBlockingQueue<Boolean>(SwirlController.NUMBER_OF_ROBOTS);
		this.robotsList = new ArrayList<RobotMultiSingleChannel>();
		this.robotLocations = robotLocs;
		this.sensorsList = new ArrayList<SensorMultiSingleChannel>();
		this.sensorsLocations = sensorLocs;
		this.targetList = new ConcurrentHashMap<Character,Message>();
		
		if (SwirlController.SHOW_GRAPHICS)
		{
			this.frame = new JFrame();
			this.frame.setBounds(1,1,SwirlController.FRAME_BOUNDX, SwirlController.FRAME_BOUNDY);
			this.frame.setTitle("Multi-Threaded Single Channel Run");
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.frame.getContentPane().setBackground(Color.WHITE);
			this.frame.setVisible(true);
		}
		generateSensorsList();
		generateRobot();
	}
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
	 * Sensors List
	 */
	private final java.util.List<SensorMultiSingleChannel> sensorsList;
	/**
	 * Robots list
	 */
	private final java.util.List<RobotMultiSingleChannel> robotsList;
	/**
	 * List of robots that are arrived at their destinations
	 */
	private final BlockingQueue<Boolean> robotIsArrived;
	/**
	 * List of graphic components
	 */
	private final ConcurrentMap<Character,Component> graphicComps; 
	/**
	 * All messages transmitted between sensors, and sensors and robots
	 */
	private final Map<Integer,BlockingQueue<Message>> msgList;
	/**
	 * Robots next target
	 */
	private final ConcurrentMap<Character, Message> targetList;
	/**
	 * Global timer
	 */
	long globalTime;
	/**
	 * Global time elapsed
	 */
	long globalTimeElapsed;

	/**
	 * Generates sensors
	 */
	public void generateSensorsList()
	{
		int sensorsQueueSize =(int)((SwirlController.SENSORS_COM_RANGE*SwirlController.SENSORS_COM_RANGE)/((SwirlController.SENSORS_DIAMETER/2)
				*(SwirlController.SENSORS_DIAMETER/2)*SwirlController.DISTANCE_BETWEEN_SENSORS)) + SwirlController.NUMBER_OF_ROBOTS;

		for (Map.Entry <Integer,Point> entry: this.sensorsLocations.entrySet())
		{
			SensorMultiSingleChannel s = new SensorMultiSingleChannel(entry.getKey(),entry.getValue(), SwirlController.SENSORS_COM_RANGE,sensorsQueueSize,
					frame,graphicComps,this.msgList,this.targetList, this.sensorsLocations, this.robotIsArrived);
			this.sensorsList.add(s);
			this.msgList.put(s.selfId,new ArrayBlockingQueue<Message>(sensorsQueueSize));
		}
	}
	/**
	 * Generates robots
	 */
	public void generateRobot()
	{

		for (Map.Entry <Character,Point> entry: this.robotLocations.entrySet())
		{
			this.robotsList.add(new RobotMultiSingleChannel(this.robotDestinations.get(entry.getKey()),SwirlController.ROBOT_SPEED,entry.getValue(),
					entry.getKey(),this.sensorsLocations, 
					this.frame,this.graphicComps,robotColor.get(entry.getKey()),
					this.msgList ,this.targetList, this.robotIsArrived));
		}

	}
	/**
	 * Resets target list
	 */
	public void setTargetList()
	{
		for (Map.Entry <Character,Point> entry: this.robotLocations.entrySet())
		{
			this.targetList.put(entry.getKey(),new Message());
		}
	}
	/**
	 * Creates and runs threads
	 */
	public void setThreads()
	{
		for (int i = 0; i < this.sensorsList.size();i++)
		{
			new Thread( this.sensorsList.get(i), Integer.toString(i)).start();
		}

		for (int i = 0; i < this.robotsList.size();i++)
		{
			new Thread( this.robotsList.get(i), Character.toString(this.robotsList.get(i).myId)).start();
		}
		if (SwirlController.SHOW_DEBUG_MESSAGES)
		{
			System.out.println("Number of active threads " + java.lang.Thread.activeCount());
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
	 * Runs the system
	 */
	public void execute()
	{	
		this.globalTime = System.currentTimeMillis();
		if(SwirlController.DEST_REGEN)
		{
			setDestinations();
		}
		if(SwirlController.RESET_LOCATIONS)
		{
			resetLocations();
		}
		setTargetList();
		setThreads();
		if (SwirlController.SAVE_DATA)
		{

			BufferedImage image = new BufferedImage(SwirlController.FRAME_BOUNDX, SwirlController.FRAME_BOUNDY, BufferedImage.TYPE_INT_RGB);
			Container content = this.frame.getContentPane();
			Graphics2D g2D = image.createGraphics();
			int idx = 0;
			while(true)
			{
				idx++;
				content.printAll(g2D);
				try
				{
					ImageIO.write(image,"png", new File(SwirlController.PATH_MULTISINGLE+idx+".png"));
				}
				catch(Exception exception)
				{
				}
				if (this.robotIsArrived.size() == SwirlController.NUMBER_OF_ROBOTS)
				{
					System.out.println("All Robots arrived!");
					break;
				}
			}
		}
		else
		{
			while(true)
			{
				if (this.robotIsArrived.size() == SwirlController.NUMBER_OF_ROBOTS)
				{
					System.out.println("All Robots arrived!");
					break;
				}
			}
		}
		this.robotIsArrived.clear();
		this.globalTimeElapsed = System.currentTimeMillis() - this.globalTime;
		System.out.println("Total time of completion for Multi-Threaded Single Channel run is " + (double)this.globalTimeElapsed/1000.);
	}	

}
