package swirl;

/**
 * Top Level Controller for testing SWIRL implementations
 * 
 * @author Amir Rasouli
 */


import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class SwirlController {

	/**
	 * Default constructor
	 */
	SwirlController()
	{
		this.rand  = new Random();
		this.sensorsLocations = new HashMap<Integer,Point>();
		this.robotLocationsSeq = new HashMap<Character,Point>();
		this.robotLocationsMulti = new HashMap<Character,Point>();
		this.robotLocationsSemiMulti = new HashMap<Character,Point>();
		this.robotLocationsMultiSingle = new HashMap<Character,Point>();
		this.originalRobotLocations = new HashMap<Character,Point>();
		this.robotDestinationsSeq = new HashMap<Character,Integer>();
		this.robotDestinationsMulti = new HashMap<Character,Integer>();
		this.robotDestinationsSemiMulti = new HashMap<Character,Integer>();
		this.robotDestinationsMultiSingle = new HashMap<Character,Integer>();
		this.robotColor = new HashMap<Character,Color>();
	}
	/**
	 * Constructor
	 * 
	 * @param NOR number of robots
	 * @param NOS number of sensors
	 * @param EX x dimension of environment
	 * @param EY y dimension of environment
	 * @param ETI estimate time intervals
	 * @param CL communication latency
	 */
	SwirlController(int NOR, int NOS, int EX, int EY, long ETI, double CL)
	{
		COM_LATENCY = CL;
		ENVX = EX;
		ENVY = EY;
		ESTIMATE_TIME_INETRVAL = ETI;
		NUMBER_OF_ROBOTS = NOR;
		NUMBER_OF_SENSORS = NOS;
		this.rand  = new Random();
		this.sensorsLocations = new HashMap<Integer,Point>();
		this.robotLocationsSeq = new HashMap<Character,Point>();
		this.robotLocationsMulti = new HashMap<Character,Point>();
		this.robotLocationsSemiMulti = new HashMap<Character,Point>();
		this.robotLocationsMultiSingle = new HashMap<Character,Point>();
		this.originalRobotLocations = new HashMap<Character,Point>();
		this.robotDestinationsSeq = new HashMap<Character,Integer>();
		this.robotDestinationsMulti = new HashMap<Character,Integer>();
		this.robotDestinationsSemiMulti = new HashMap<Character,Integer>();
		this.robotDestinationsMultiSingle = new HashMap<Character,Integer>();
		this.robotColor = new HashMap<Character,Color>();
		
	}
	/**
	 * Constructor
	 * 
	 * @param NOR number of robots
	 * @param NOS number of sensors
	 */
	SwirlController(int NOR, int NOS)
	{
		NUMBER_OF_ROBOTS = NOR;
		NUMBER_OF_SENSORS = NOS;
		this.rand  = new Random();
		this.sensorsLocations = new HashMap<Integer,Point>();
		this.robotLocationsSeq = new HashMap<Character,Point>();
		this.robotLocationsMulti = new HashMap<Character,Point>();
		this.robotLocationsSemiMulti = new HashMap<Character,Point>();
		this.robotLocationsMultiSingle = new HashMap<Character,Point>();
		this.originalRobotLocations = new HashMap<Character,Point>();
		this.robotDestinationsSeq = new HashMap<Character,Integer>();
		this.robotDestinationsMulti = new HashMap<Character,Integer>();
		this.robotDestinationsSemiMulti = new HashMap<Character,Integer>();
		this.robotDestinationsMultiSingle = new HashMap<Character,Integer>();
		this.robotColor = new HashMap<Character,Color>();
	}
	/**
	 * Constructor
	 * 
	 * @param NOR number of robots
	 * @param NOS number of sensors
	 * @param ETI estimate time interval
	 */
	SwirlController( int NOR, int NOS, long ETI)
	{
		ESTIMATE_TIME_INETRVAL = ETI;
		NUMBER_OF_ROBOTS = NOR;
		NUMBER_OF_SENSORS = NOS;
		this.rand  = new Random();
		this.sensorsLocations = new HashMap<Integer,Point>();
		this.robotLocationsSeq = new HashMap<Character,Point>();
		this.robotLocationsMulti = new HashMap<Character,Point>();
		this.robotLocationsSemiMulti = new HashMap<Character,Point>();
		this.robotLocationsMultiSingle = new HashMap<Character,Point>();
		this.originalRobotLocations = new HashMap<Character,Point>();
		this.robotDestinationsSeq = new HashMap<Character,Integer>();
		this.robotDestinationsMulti = new HashMap<Character,Integer>();
		this.robotDestinationsSemiMulti = new HashMap<Character,Integer>();
		this.robotDestinationsMultiSingle = new HashMap<Character,Integer>();
		this.robotColor = new HashMap<Character,Color>();
	}
	/**
	 * Constructor
	 * 
	 * @param NOR number of robots
	 * @param NOS number of sensors
	 * @param speed robots maximum speed
	 */
	SwirlController( int NOR, int NOS, int speed)
	{
		NUMBER_OF_ROBOTS = NOR;
		NUMBER_OF_SENSORS = NOS;
		ROBOT_SPEED = speed;
		this.rand  = new Random();
		this.sensorsLocations = new HashMap<Integer,Point>();
		this.robotLocationsSeq = new HashMap<Character,Point>();
		this.robotLocationsMulti = new HashMap<Character,Point>();
		this.robotLocationsSemiMulti = new HashMap<Character,Point>();
		this.robotLocationsMultiSingle = new HashMap<Character,Point>();
		this.originalRobotLocations = new HashMap<Character,Point>();
		this.robotDestinationsSeq = new HashMap<Character,Integer>();
		this.robotDestinationsMulti = new HashMap<Character,Integer>();
		this.robotDestinationsSemiMulti = new HashMap<Character,Integer>();
		this.robotDestinationsMultiSingle = new HashMap<Character,Integer>();
		this.robotColor = new HashMap<Character,Color>();
	}

	public final static boolean START = true;
	public final static boolean STOP = false;
	/**
	 * Minimum distance to arrive to a sensor
	 */
	public final static double DELTA = 10.;
	/**
	 * Minimum range of communication of sensors
	 */
	public final static int SENSORS_COM_RANGE = 150;
	/**
	 * Size of sensors
	 */
	public final static int SENSORS_DIAMETER = 20;
	/**
	 * Learning factor of SWIRLs
	 */
	public final static double ALPHA = 0.7;
	// ****************** Graphics *************************
	/**
	 * Robots width
	 */
	public final static int ROBOT_WIDTH = 40;
	/**
	 * Robots height
	 */
	public final static int ROBOT_HEIGHT = 30;
	/**
	 * Initial color of sensors
	 */
	public final static Color INIT_COLOR = Color.GRAY;
	/**
	 * Initial color of sensors ID
	 */
	public final static Color INIT_FONT_COLOR = Color.WHITE;
	/**
	 * Default color of sensors
	 */
	public final static Color DEFAULT_COLOR = Color.ORANGE;
	/**
	 * Default color of sensors ID
	 */
	public final static Color DEFAULT_FONT_COLOR = Color.BLACK;
	/**
	 * Color of sensors in estimate phase
	 */
	public final static Color EST_COLOR = Color.GREEN;
	/**
	 * Color of sensors ID in estimate phase
	 */
	public final static Color EST_FONT_COLOR = Color.YELLOW;
	/**
	 * Robots choose new destination after arrival
	 */
	public final static boolean CHOOSE_NEW_DESTINATION= false;
	/**
	 * Minimum distance between sensors in simulation
	 */
	public final static int DISTANCE_BETWEEN_SENSORS = 3;
	/**
	 * Delay to make graphics visible
	 */
	public final static double VISIBILITY_DELAY = 0.;// 0.1; 
	//********************** Setups ***************************
	/**
	 * Number of robots
	 */
	public static int NUMBER_OF_ROBOTS = 5; //52
	/**
	 * Number of sensors
	 */
	public static int NUMBER_OF_SENSORS = 60; 
	/**
	 * Environment x dimension
	 */
	public static int ENVX =  924;//5000;//
	/**
	 * Environment y dimension
	 */
	public static int ENVY = 740;// 5000;//
	/**
	 *  * Graphics bounding box x dimension
	 */
	public final static int FRAME_BOUNDX = ENVX + 100;
	/**
	 *  Graphics bounding box y dimension
	 */
	public final static int FRAME_BOUNDY = ENVY + 100;
	/**
	 * Estimate time intervals
	 */
	public static long ESTIMATE_TIME_INETRVAL = 5;
	/**
	 * Communication latency
	 */
	public static double COM_LATENCY = 0.; // Not used in tests
	/**
	 * Regenerate destination for all robots
	 */
	public static boolean DEST_REGEN = true;
	/**
	 * Reset location of robots to the initial location
	 */
	public static boolean RESET_LOCATIONS = true;
	/**
	 * Robots maximum speed
	 */
	public static int ROBOT_SPEED = 60;
	//****************************   Paths  *************************
	/**
	 * Path to save multi-threaded implementations
	 */
	public final static String PATH_MULTI = "dataMulti/frame";
	/**
	 * Path to save multi-threaded single channel implementations
	 */
	public final static String PATH_MULTISINGLE = "dataMultiSingle/frame";
	/**
	 * Path to save sequential implementations
	 */
	public final static String PATH_SEQ = "dataSeq/frame";
	/**
	 * Path to save semi multi-threaded implementations
	 */
	public final static String PATH_SEMIMULTI = "dataSemiMulti/frame";
	//****************** Control Variables *****************************
	/**
	 * Save data
	 */
	public final static boolean SAVE_DATA = false;
	/**
	 * Show debug messages
	 */
	public final static boolean SHOW_DEBUG_MESSAGES = false;
	/**
	 * Show graphics
	 */
	public final static boolean SHOW_GRAPHICS =  true;
	/**
	 * Log results
	 */
	public static boolean LOG_RESULTS = false;
	/**
	 * Random number generated
	 */
	public Random rand  = new Random();
	/**
	 * Sensors List
	 */
	public final Map<Integer,Point> sensorsLocations;
	/** 
	 * Robot locations for sequential implementation
	 */
	public final Map<Character,Point> robotLocationsSeq;
	/** 
	 * Robot locations for multi-threaded implementation
	 */
	public final Map<Character,Point> robotLocationsMulti;
	/** 
	 * Robot locations for semi-mmulti-threaded implementation
	 */
	public final Map<Character,Point> robotLocationsSemiMulti;
	/** 
	 * Robot locations for multi-threaded single channel implementation
	 */
	public final Map<Character,Point> robotLocationsMultiSingle;
	/** 
	 *  Robots locations
	 */
	public final Map<Character,Point> originalRobotLocations;
	/**
	 * Robots destinations sequential implementation
	 */
	public final Map<Character,Integer> robotDestinationsSeq;
	/**
	 * Robots destinations multi-threded implementation
	 */
	public final Map<Character,Integer> robotDestinationsMulti;
	/**
	 * Robots destinations semi-multi-threaded implementation
	 */
	public final Map<Character,Integer> robotDestinationsSemiMulti;
	/**
	 * Robots destinations multi-threaded single channel implementation
	 */
	public final Map<Character,Integer> robotDestinationsMultiSingle;
	/**
	 * List of robots colors
	 */
	public final Map<Character,Color> robotColor;
	/**
	 * Generates list of sensors using radius constraint
	 */
	public void generateSensorsListCircle()
	{
		Point p = new Point(rand.nextInt(ENVX),rand.nextInt(ENVY));
		sensorsLocations.put(0,p);
		for (int i = 1; i < NUMBER_OF_SENSORS; i++)
		{

			do{
				double angle = Math.random()*Math.PI*2;
				double radius = rand.nextInt(SENSORS_COM_RANGE - (SENSORS_DIAMETER)) + (SENSORS_DIAMETER ) ;
				int x = (int)(Math.cos(angle)*radius);//rand.nextInt(600);//
				int y = (int)(Math.sin(angle)*radius);//rand.nextInt(400);//
				int index = rand.nextInt(sensorsLocations.size());
				x += (int)sensorsLocations.get(index).getX();
				y += (int)sensorsLocations.get(index).getY();

				if (x - SENSORS_DIAMETER  < 0 || x + SENSORS_DIAMETER  > ENVX || y - SENSORS_DIAMETER < 0 || y + SENSORS_DIAMETER > ENVY)
				{
					continue;
				}
				p = new Point(x,y);
			}
			while(sensorsLocations.containsValue(p));
			sensorsLocations.put(i,p);
		}
	}
	/**
	 * Generate list of sensors randomly
	 */
	public void generateSensorsListRandom()
	{
		Point p = new Point(rand.nextInt( ENVX ),rand.nextInt( ENVY));
		sensorsLocations.put(0,p);
		boolean distCor = false;
		for (int i = 1; i < NUMBER_OF_SENSORS; i++)
		{
			while(true)
			{
				p = new Point(rand.nextInt( ENVX ),rand.nextInt( ENVY));

				for (int j = 0 ; j < sensorsLocations.size(); j++)
				{
					double dist = Math.sqrt( 
							Math.pow((p.getX()-sensorsLocations.get(j).getX()),2)+
							Math.pow((p.getY()-sensorsLocations.get(j).getY()),2)
							);
					if (dist < SENSORS_COM_RANGE && dist > SENSORS_DIAMETER*DISTANCE_BETWEEN_SENSORS )
					{
						distCor = true;
						break;
					}
				}
				if (distCor)
				{
					sensorsLocations.put(i,p);
					distCor = false;
					break;
				}
			}

		}
	}
	/**
	 * Generate list of sensors randomly with constraints
	 */
	public void generateSensorsList()
	{

		Point p = new Point(rand.nextInt( ENVX - SENSORS_DIAMETER ) + SENSORS_DIAMETER ,rand.nextInt( ENVY - SENSORS_DIAMETER ) + SENSORS_DIAMETER);
		sensorsLocations.put(0,p);
		boolean distCor = false;
		for (int i = 1; i < NUMBER_OF_SENSORS; i++)
		{

			while(true)
			{
				p = new Point(rand.nextInt( ENVX - SENSORS_DIAMETER )+SENSORS_DIAMETER ,rand.nextInt( ENVY- SENSORS_DIAMETER )+SENSORS_DIAMETER);

				for (int j = 0 ; j < sensorsLocations.size(); j++)
				{
					double dist = Math.sqrt( 
							Math.pow((p.getX()-sensorsLocations.get(j).getX()),2)+
							Math.pow((p.getY()-sensorsLocations.get(j).getY()),2)
							);
					if (dist < SENSORS_COM_RANGE && dist > SENSORS_DIAMETER*DISTANCE_BETWEEN_SENSORS )
					{
						distCor = true;
						break;
					}
				}
				for (int j = 0 ; j < sensorsLocations.size(); j++)
				{
					double dist = Math.sqrt( 
							Math.pow((p.getX()-sensorsLocations.get(j).getX()),2)+
							Math.pow((p.getY()-sensorsLocations.get(j).getY()),2)
							);
					if (dist < SENSORS_DIAMETER*DISTANCE_BETWEEN_SENSORS)
					{
						distCor = false;
						break;
					}
				}

				if (distCor)
				{
					sensorsLocations.put(i,p);
					distCor = false;
					break;
				}
			}

		}
	}
	/**
	 * Generates list of robots
	 */
	public void generateRobot()
	{
		char[] charList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
		Color[] robotColors = {Color.GRAY, Color.BLUE, Color.RED, Color.CYAN, Color.MAGENTA,Color.ORANGE, Color.PINK};
		final int NUMCOLORS = 7;
		for (int i = 0 ; i < NUMBER_OF_ROBOTS; i++)
		{
			int destin = this.rand.nextInt(NUMBER_OF_SENSORS);
			int x = this.rand.nextInt(SwirlController.ENVX);
			int y = this.rand.nextInt(SwirlController.ENVY);

			robotLocationsSeq.put(charList[i], new Point(x,y));
			robotLocationsMulti.put(charList[i], new Point(x,y));
			robotLocationsSemiMulti.put(charList[i], new Point(x,y)); 
			robotLocationsMultiSingle.put(charList[i], new Point(x,y)); 
			originalRobotLocations.put(charList[i], new Point(x,y)); 
			robotDestinationsSeq.put(charList[i], destin);
			robotDestinationsMulti.put(charList[i], destin);
			robotDestinationsSemiMulti.put(charList[i], destin);
			robotDestinationsMultiSingle.put(charList[i], destin);
			robotColor.put(charList[i], robotColors[i % NUMCOLORS]);
			System.out.println("Robot "+ charList[i] + "'s destination is " + destin);
			System.out.println("Robot "+ charList[i] + "'s location is " + "(" + x+","+ y +")");
		}

	}
	/**
	 * Regenerates robots destinations
	 */
	public void regenerateDestinations()
	{
		char[] charList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
		for (int i = 0 ; i < NUMBER_OF_ROBOTS; i++)
		{
			int destin = this.rand.nextInt(NUMBER_OF_SENSORS);
			robotDestinationsSeq.put(charList[i], destin);
			robotDestinationsMulti.put(charList[i], destin);
			robotDestinationsSemiMulti.put(charList[i], destin);
			robotDestinationsMultiSingle.put(charList[i], destin);
			System.out.println("Robot "+ charList[i] + "'s destination is " + destin);
		}
	}
	/**
	 * Resets locations of robots
	 */
	public void resetLocations()
	{
		for (Map.Entry<Character,Point> entry : this.originalRobotLocations.entrySet())
		{

			robotLocationsSeq.put(entry.getKey(), new Point(entry.getValue().x,entry.getValue().y));
			robotLocationsMulti.put(entry.getKey(), new Point(entry.getValue().x,entry.getValue().y));
			robotLocationsSemiMulti.put(entry.getKey(), new Point(entry.getValue().x,entry.getValue().y));
			robotLocationsMultiSingle.put(entry.getKey(), new Point(entry.getValue().x,entry.getValue().y));
			//System.out.println("Robot "+ entry.getKey() + "'s location is " + "(" + entry.getValue().x+","+ entry.getValue().y+")");
		}
	}
	/**
	 * Setups system by generating sensors and robots
	 */
	public void setup()
	{
		generateSensorsList();
		generateRobot();
	}
	/**
	 * Logs files
	 * 
	 * @param lg whether to log or not
	 * @param path where to save the log file
	 */
	public void log(boolean lg, String path)
	{
		if (!lg)
		{
			return;
		}

		try
		{
			//mkdirs()
			System.setOut(new PrintStream(new FileOutputStream( new File(path))));
		} catch (FileNotFoundException e)
		{

		}
	}
	/**
	 * Tests learning of system
	 */
	public void testLearning()
	{
		LOG_RESULTS = true;
		RESET_LOCATIONS = true;
		int numRobs = 3;

		int idx = 0;
		for (int r = 0 ; r < 8; r ++ )
		{
			numRobs ++ ;
			int numSensors = 20;
			for (int s = 3; s < 5 ; s++)//change back later
			{
				numSensors *= 2;
				idx++;	

				SwirlController SC = new SwirlController(numRobs,numSensors);
				SC.log(LOG_RESULTS, "LearningTest_"+idx+"R"+numRobs+"_S"+numSensors+".txt");

				SC.setup();
				SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
						SC.robotDestinationsMulti,SC.robotColor);
				SwirlSemiMultiThreaded SSM = new  SwirlSemiMultiThreaded(SC.sensorsLocations,SC.robotLocationsSemiMulti, 
						SC.robotDestinationsSemiMulti,SC.robotColor);
				SwirlSequential SQ = new SwirlSequential(SC.sensorsLocations,SC.robotLocationsSeq, 
						SC.robotDestinationsSeq,SC.robotColor);
				System.out.println("********************************************  RUN " + 1 +"  ********************************************");
				for ( int j = 0 ; j < 20 ; j++)
				{
					System.out.println("********  Starting Multi-Threaded Run!  ********");
					SM.execute();
					System.out.println("********  Starting SemiMultiThreaded Run!  ********");
					SSM.execute();
					System.out.println("********  Starting Sequential Run!  ********");
					SQ.execute();
					System.out.println("********************************************  RUN " + (j+2) +"  ********************************************");
					SC.resetLocations();
				}
			}
		}
		LOG_RESULTS = false;
		RESET_LOCATIONS = false;


	}
	/**
	 * Tests learning with changing destination
	 */
	public void testLearningChangingDestination()
	{
		LOG_RESULTS = true;
		DEST_REGEN  = true;
		int numRobs = 2;

		int idx = 0;
		for (int r = 0 ; r < 8; r ++ )
		{
			numRobs ++ ; 
			int numSensors = 10;
			for (int s = 0 ; s < 5 ; s++)
			{
				numSensors *= 2;
				idx++;	
				SwirlController SC = new SwirlController(numRobs,numSensors);
				SC.log(LOG_RESULTS, "LearningTestDes_"+idx+"R"+numRobs+"_S"+numSensors+".txt");
				SC.setup();
				SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
						SC.robotDestinationsMulti,SC.robotColor);
				SwirlSemiMultiThreaded SSM = new  SwirlSemiMultiThreaded(SC.sensorsLocations,SC.robotLocationsSemiMulti, 
						SC.robotDestinationsSemiMulti,SC.robotColor);
				SwirlSequential SQ = new SwirlSequential(SC.sensorsLocations,SC.robotLocationsSeq, 
						SC.robotDestinationsSeq,SC.robotColor);
				System.out.println("********************************************  RUN " + 1 +"  ********************************************");
				for ( int j = 0 ; j < 20 ; j++)
				{
					System.out.println("********  Starting Multi-Threaded Run!  ********");
					SM.execute();
					System.out.println("********  Starting SemiMultiThreaded Run!  ********");
					SSM.execute();
					System.out.println("********  Starting Sequential Run!  ********");
					SQ.execute();
					System.out.println("********************************************  RUN " + (j+2) +"  ********************************************");
					SC.regenerateDestinations();
				}
			}
		}
		LOG_RESULTS = false;
		DEST_REGEN  = false;


	}
	/**
	 * Tests by different number of robots
	 */
	public void testChangingRobotNumber()
	{
		LOG_RESULTS = true;
		int numRobs = 2;
		int numSensors = 200;

		log(LOG_RESULTS, "RobotNumberTest_S"+numSensors+".txt");

		for (int r = 0 ; r < 40; r ++ )
		{
			numRobs ++ ; 
			System.out.println("Number of Robots " + numRobs);
			SwirlController SC = new SwirlController(numRobs,numSensors);
			SC.setup();
			SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
					SC.robotDestinationsMulti,SC.robotColor);
			System.out.println("********  Starting Multi-Threaded Run!  ********");
			SM.execute();

			SwirlSemiMultiThreaded SSM = new  SwirlSemiMultiThreaded(SC.sensorsLocations,SC.robotLocationsSemiMulti, 
					SC.robotDestinationsSemiMulti,SC.robotColor);
			System.out.println("********  Starting SemiMultiThreaded Run!  ********");
			SSM.execute();
			SwirlSequential SQ = new SwirlSequential(SC.sensorsLocations,SC.robotLocationsSeq, 
					SC.robotDestinationsSeq,SC.robotColor);
			System.out.println("********  Starting Sequential Run!  ********");
			SQ.execute();
		}
		LOG_RESULTS = false;

	}
	/**
	 * Tests by changing number of sensors	
	 */
	public void testChangingSensorNumber()
	{
		LOG_RESULTS = true;
		int numRobs = 8;
		int numSensors = 10;
		log(LOG_RESULTS, "SensorNumberTest_R"+numRobs+".txt");
		for (int s = 0 ; s < 30; s ++ )
		{
			numSensors += 15;
			System.out.println("Numeber of Sensors " + numSensors);
			SwirlController SC = new SwirlController(numRobs,numSensors);
			SC.setup();
			SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
					SC.robotDestinationsMulti,SC.robotColor);
			System.out.println("********  Starting Multi-Threaded Run!  ********");
			SM.execute();

			SwirlSemiMultiThreaded SSM = new  SwirlSemiMultiThreaded(SC.sensorsLocations,SC.robotLocationsSemiMulti, 
					SC.robotDestinationsSemiMulti,SC.robotColor);
			System.out.println("********  Starting SemiMultiThreaded Run!  ********");
			SSM.execute();
			SwirlSequential SQ = new SwirlSequential(SC.sensorsLocations,SC.robotLocationsSeq, 
					SC.robotDestinationsSeq,SC.robotColor);
			System.out.println("********  Starting Sequential Run!  ********");
			SQ.execute();

		}
		LOG_RESULTS = false;

	}
	public void testChangingSpeed()
	{
		LOG_RESULTS = true;
		int speed = 40;
		int numRobs = 8;
		int numSensors = 200;
		log(LOG_RESULTS, "SpeedTest_Speed_R"+numRobs+"_S"+numSensors+".txt");
		for (int sp = 0; sp < 10; sp++)
		{
			speed +=10;
			System.out.println("Speed =  " + speed);
			SwirlController SC = new SwirlController(numRobs,numSensors,speed);
			SC.setup();
			SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
					SC.robotDestinationsMulti,SC.robotColor);
			System.out.println("********  Starting Multi-Threaded Run!  ********");
			SM.execute();
			SwirlSemiMultiThreaded SSM = new  SwirlSemiMultiThreaded(SC.sensorsLocations,SC.robotLocationsSemiMulti, 
					SC.robotDestinationsSemiMulti,SC.robotColor);
			System.out.println("********  Starting SemiMultiThreaded Run!  ********");
			SSM.execute();
			SwirlSequential SQ = new SwirlSequential(SC.sensorsLocations,SC.robotLocationsSeq, 
					SC.robotDestinationsSeq,SC.robotColor);
			System.out.println("********  Starting Sequential Run!  ********");
			SQ.execute();
		}
		LOG_RESULTS = false;
	}
	public void testChangingEstimation()
	{
		LOG_RESULTS = true;
		long est = 5;
		int numRobs = 8;
		int numSensors = 200;
		log(LOG_RESULTS, "EstTest_Est_R"+numRobs+"_S"+numSensors+".txt");
		for (int es = 0; es < 10; es++)
		{
			System.out.println("Estimate time" + est);
			System.out.println("Number of Sensors " + numSensors);
			SwirlController SC = new SwirlController(numRobs,numSensors,est);
			SC.setup();
			SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
					SC.robotDestinationsMulti,SC.robotColor);
			System.out.println("********  Starting Multi-Threaded Run!  ********");
			SM.execute();

			SwirlSemiMultiThreaded SSM = new  SwirlSemiMultiThreaded(SC.sensorsLocations,SC.robotLocationsSemiMulti, 
					SC.robotDestinationsSemiMulti,SC.robotColor);
			System.out.println("********  Starting SemiMultiThreaded Run!  ********");
			SSM.execute();
			SwirlSequential SQ = new SwirlSequential(SC.sensorsLocations,SC.robotLocationsSeq, 
					SC.robotDestinationsSeq,SC.robotColor);
			System.out.println("********  Starting Sequential Run!  ********");
			SQ.execute();
			est +=5;

		}
		LOG_RESULTS = false;

	}
	public void testSingleVsMultiChannel()
	{
		LOG_RESULTS = true;
		RESET_LOCATIONS = true;
		int numRobs = 7;
		for (int r = 3; r < 7; r ++ )
		{
			numRobs ++ ;
			int numSensors = 20;
			log(LOG_RESULTS, "SingleVsMultiTest_R"+numRobs+"_S.txt");
			for (int s = 0; s < 7 ; s++)//change back later
			{
				numSensors += 40;
				System.out.println("Number of Sensors " + numSensors);
				SwirlController SC = new SwirlController(numRobs,numSensors);
				SC.setup();
				SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
						SC.robotDestinationsMulti,SC.robotColor);
				SwirlMultiThreadSingleChannel SMSC = new SwirlMultiThreadSingleChannel(SC.sensorsLocations,SC.robotLocationsMultiSingle, 
						SC.robotDestinationsMultiSingle,SC.robotColor);
				System.out.println("********  Starting Multi-Threaded Run!  ********");
				SM.execute();
				System.out.println("********  Starting Multi-Threaded Single Channel Run!  ********");
				SMSC.execute();
			}
		}
		LOG_RESULTS = false;
		RESET_LOCATIONS = false;
	}
	public static void runTests()
	{
		SwirlController SC = new SwirlController();
		//SC.testSingleVsMultiChannel();
		//SC.testLearning();
		//SC.testLearningChangingDestination();
		//SC.testChangingEstimation();
		//SC.testChangingRobotNumber();
		SC.testChangingSensorNumber();
		SC.testChangingSpeed();
	}
	public static void main(String[] args)
	{	
		//runTests();
		int idx = 0;
		while(idx <5)
		{
		SwirlController SC = new SwirlController();
		SC.setup();
		SwirlMultiThread SM = new SwirlMultiThread(SC.sensorsLocations,SC.robotLocationsMulti, 
				SC.robotDestinationsMulti,SC.robotColor);
		SwirlMultiThreadSingleChannel SMSC = new SwirlMultiThreadSingleChannel(SC.sensorsLocations,SC.robotLocationsMultiSingle, 
				SC.robotDestinationsMultiSingle,SC.robotColor);
		SwirlSemiMultiThreaded SSM = new  SwirlSemiMultiThreaded(SC.sensorsLocations,SC.robotLocationsSemiMulti, 
				SC.robotDestinationsSemiMulti,SC.robotColor);
		System.out.println("Number of active threads " + java.lang.Thread.activeCount());

		SwirlSequential SQ = new SwirlSequential(SC.sensorsLocations,SC.robotLocationsSeq, 
				SC.robotDestinationsSeq,SC.robotColor);
		System.out.println("********  Starting Multi-Threaded Run!  ********");
		SM.execute();
		System.out.println("********  Starting Multi-Threaded Single Channel Run!  ********");
		SMSC.execute();
		System.out.println("********  Starting SemiMultiThreaded Run!  ********");
		SSM.execute();
		System.out.println("********  Starting Sequential Run!  ********");
		SQ.execute();
		idx++;
		}

	}

}
