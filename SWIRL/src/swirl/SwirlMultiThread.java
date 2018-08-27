package swirl;
import java.util.*;
import java.util.concurrent.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
/**
 * Class that controls Multi-threaded implementation of SWIRL 
 * 
 * @author Amir Rasouli
 *
 */
public class SwirlMultiThread {
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
	public SwirlMultiThread(Map<Integer,Point> sensorLocs,Map<Character,Point> robotLocs,Map<Character,Integer> robotDes,Map<Character,Color> robotCol)
	{
		
		this.estList = new HashMap<Integer,BlockingQueue<Message>>();
		this.graphicComps = new ConcurrentHashMap<Character,Component>();
		this.initList =  new HashMap<Integer,BlockingQueue<Message>>();
		this.queryList = new HashMap<Integer,BlockingQueue<Message>>();
		this.robotColor = robotCol;
		this.robotDestinations = robotDes;
		this.robotIsArrived = new ArrayBlockingQueue<Boolean>(SwirlController.NUMBER_OF_ROBOTS);
		this.robotsList = new ArrayList<RobotMulti>();
		this.robotLocations = robotLocs;
		this.sensorsList = new ArrayList<SensorMulti>();
		this.sensorsLocations = sensorLocs;
		this.targetList = new ConcurrentHashMap<Character,Message>();
		this.updateList = new HashMap<Integer,BlockingQueue<Message>>();
		if (SwirlController.SHOW_GRAPHICS)
		{
			this.frame = new JFrame();
			this.frame.setBounds(1,1,SwirlController.FRAME_BOUNDX, SwirlController.FRAME_BOUNDY);
			this.frame.setTitle("Multi-Threaded Run");
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
	private final java.util.List<SensorMulti> sensorsList;
	/**
	 * List of robots
	 */
	private final java.util.List<RobotMulti> robotsList;
	/**
	 * A list that keeps the record of whether robots are arrived at their destinations
	 */
	private final BlockingQueue<Boolean> robotIsArrived;
	/**
	 * Graphic components
	 */
	private final ConcurrentMap<Character,Component> graphicComps; 
	/**
	 * Initialize messages
	 */
	private final Map<Integer,BlockingQueue<Message>> initList;
	/**
	 * Estimate messages
	 */
	private final Map<Integer,BlockingQueue<Message>>  estList;
	/**
	 * Queries made by robots
	 */
	private final Map<Integer,BlockingQueue<Message>>  queryList;
	/**
	 * Updates sent by robots
	 */
	private final Map<Integer,BlockingQueue<Message>>  updateList;
	/**
	 * Target list for robots
	 */
	private final ConcurrentMap<Character, Message> targetList;
	/**
	 * Global timer
	 */
	private long globalTime;
	/**
	 * Global time elapsed
	 */
	private long globalTimeElapsed;
	/**
	 * Generates list of sensors based on sensors locations received
	 */
	public void generateSensorsList()
	{
		int sensorsQueueSize =(int)((SwirlController.SENSORS_COM_RANGE*SwirlController.SENSORS_COM_RANGE)/((SwirlController.SENSORS_DIAMETER/2)
				*(SwirlController.SENSORS_DIAMETER/2)*SwirlController.DISTANCE_BETWEEN_SENSORS));

		for (Map.Entry <Integer,Point> entry: this.sensorsLocations.entrySet())
		{
			SensorMulti s = new SensorMulti(entry.getKey(),entry.getValue(), SwirlController.SENSORS_COM_RANGE,sensorsQueueSize,
					frame,graphicComps,this.initList,this.estList,
					this.queryList,this.updateList ,this.targetList, this.sensorsLocations, this.robotIsArrived);
			this.sensorsList.add(s);
			this.estList.put(s.selfId,new ArrayBlockingQueue<Message>(sensorsQueueSize));
			this.initList.put(s.selfId,new ArrayBlockingQueue<Message>(sensorsQueueSize));
			this.queryList.put(s.selfId,new ArrayBlockingQueue<Message>(SwirlController.NUMBER_OF_ROBOTS*4));
			this.updateList.put(s.selfId,new ArrayBlockingQueue<Message>(SwirlController.NUMBER_OF_ROBOTS*4));
		}
	}
	/**
	 * Generates list of robots based on robots locations received
	 */
	public void generateRobot()
	{

		for (Map.Entry <Character,Point> entry: this.robotLocations.entrySet())
		{
			this.robotsList.add(new RobotMulti(this.robotDestinations.get(entry.getKey()),SwirlController.ROBOT_SPEED,entry.getValue(),
					entry.getKey(),this.sensorsLocations, 
					this.frame,this.graphicComps,robotColor.get(entry.getKey()),
					this.queryList,this.updateList ,this.targetList, this.robotIsArrived));

			//this.targetList.put(entry.getKey(),new Message());
		}

	}
	/**
	 * Creates a blank list of targets
	 */
	public void setTargetList()
	{
		for (Map.Entry <Character,Point> entry: this.robotLocations.entrySet())
		{
			this.targetList.put(entry.getKey(),new Message());
		}
	}
	/**
	 * Initializes and runs threads
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
			this.robotsList.get(i).setPosition(this.robotLocations.get(this.robotsList.get(i).myId)); 
			this.robotsList.get(i).setCurrentLoc(-1);

		}
	}
	/**
	 * Initializes and runs the system
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
					ImageIO.write(image,"png", new File(SwirlController.PATH_MULTI+idx+".png"));
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
		System.out.println("Total time of completion for Multi-Threaded run is " + (double)this.globalTimeElapsed/1000.);
	}	

}
