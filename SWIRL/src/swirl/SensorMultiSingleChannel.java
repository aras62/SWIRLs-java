package swirl;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
/**
 * Sensor class for Multi-thread implementation using single channel
 * @author Amir Rasouli
 *
 */
public class SensorMultiSingleChannel implements Runnable{
	/**
	 * 
	 * @param id sensor's ID
	 * @param pos sensor's position
	 * @param comRange sensor's communication range
	 * @param sQSize sensor's queue size
	 * @param f graphics frame
	 * @param gc graphic components
	 * @param mList message list
	 * @param tList targets list
	 * @param sLoc sensors locations
	 * @param rIsA robots arrived list
	 */
	public SensorMultiSingleChannel(int id, Point pos,int comRange, 
			int sQSize, JFrame f, ConcurrentMap<Character,Component> gc,
			Map<Integer,BlockingQueue<Message>> mList,
			ConcurrentMap<Character, Message> tList, Map<Integer,Point> sLoc, BlockingQueue<Boolean> rIsA)
			{
		this.currentState = state.DEFAULT;
		this.dv = new ConcurrentHashMap<Integer,Double>();
		this.graphics = gc;
		this.frame = f;
		this.initDone = false;
		this.initReceived = 0;
		this.initSent = false;
		this.position = pos;
		this.msgList = mList;
		this.N= new ArrayList<Integer>();
		this.neighbors= new ArrayList<Integer>();
		this.QList = new HashMap<Integer,Map<Integer, Double>>();
		this.robotIsArrived = rIsA;
		this.selfId = id;  
		this.sensorQueueSize = sQSize + 5;
		this.sensorsLoc = sLoc;
		this.shortestList = new HashMap<Integer,Integer>();
		this.startTime = 0;
		this.targetList = tList;
		this.timeElapsed = 0;
		this.V= new ArrayList<Integer>();
		Map <Integer,Double> self = new HashMap<Integer,Double>();
		self.put(this.selfId,0.);
		this.QList.put(this.selfId, self);
		if (SwirlController.SHOW_GRAPHICS)
		{
			DrawSensor s = new DrawSensor(position.x,position.y, SwirlController.SENSORS_DIAMETER,
					selfId, SwirlController.INIT_COLOR, SwirlController.INIT_FONT_COLOR);
			graphics.put((char)selfId,frame.getContentPane().add(s));
			frame.revalidate();
			frame.repaint();
		}
		}
	/**
	 * N list for communicating neighbors
	 */
	private final java.util.List<Integer> N;
	/**
	 * List of destinations
	 */
	private final java.util.List<Integer> V;
	/**
	 * List of neighbors
	 */
	private final java.util.List<Integer> neighbors;
	/**
	 * Distance measures
	 */
	private final Map<Integer,Map<Integer, Double>> QList;
	/**
	 * Distance vector
	 */
	private final ConcurrentMap<Integer,Double> dv;
	/**
	 * list of shortest distances
	 */
	private final Map<Integer,Integer> shortestList; /**<distance,throughWho>*/
	/**
	 * Sensor's ID
	 */
	public int selfId;
	/**
	 * It is initiated
	 */
	public int initReceived;
	/**
	 * Sensors position
	 */
	public Point position;
	/**
	 * initialization message is sent
	 */
	public boolean initSent;
	/**
	 * Initialization is done
	 */
	private boolean initDone;
	/**
	 * Sensor queue size
	 */
	public int sensorQueueSize ;
	/**
	 * Graphics frame
	 */
	public JFrame frame;
	/**
	 * Current state of the robot
	 */
	public state currentState;
	/**
	 * Graphics component
	 */
	private ConcurrentMap<Character,Component> graphics;
	/**
	 * Start time for estimation
	 */
	private long startTime;
	/**
	 * Time elapsed
	 */
	private long timeElapsed;
	/**
	 * List of robots arrived
	 */
	private final BlockingQueue<Boolean> robotIsArrived;
	/**
	 * List of messages
	 */
	private final Map<Integer,BlockingQueue<Message>>  msgList;
	/**
	 * List of targets
	 */
	private final ConcurrentMap<Character, Message> targetList;
	/**
	 * sensors locations
	 */
	private Map<Integer,Point> sensorsLoc ;
	/**
	 * Sensor's states
	 */
	public enum state { DEFAULT,ESTIMATE  }
	/**
	 * Creates delay for graphics
	 */
	private void delay()
	{
		long sTime = System.currentTimeMillis();
		double sec = SwirlController.VISIBILITY_DELAY * 1000;
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
	 * Timer
	 * @param reset true to reset
	 * @return time in seconds
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
		return timeElapsed/1000;
	}
	/**
	 * Handles graphic changes
	 * @param st state of the sensor
	 * @return true if done
	 */
	private boolean setupGraphics(state st)
	{
		if (!SwirlController.SHOW_GRAPHICS)
		{
			return false;
		}
		currentState  = st;
		SwingWorker<Boolean, DrawSensor> worker = new SwingWorker<Boolean, DrawSensor>()
				{
			protected Boolean doInBackground() throws Exception 
			{
				Color col = Color.WHITE, font = Color.WHITE;

				if (currentState == state.DEFAULT)
				{
					col = SwirlController.DEFAULT_COLOR;
					font = SwirlController.DEFAULT_FONT_COLOR;
				}else if (currentState == state.ESTIMATE)
				{
					col = SwirlController.EST_COLOR;
					font = SwirlController.EST_FONT_COLOR;
				}
				DrawSensor s = new DrawSensor(position.x, position.y, SwirlController.SENSORS_DIAMETER, selfId, col, font);
				publish(s);
				return true;
			}
			protected void process(java.util.List<DrawSensor> chunks) {
				frame.getContentPane().remove(graphics.get((char)selfId));
				frame.revalidate();
				frame.repaint();
				graphics.put((char)selfId,frame.getContentPane().add(chunks.get(chunks.size()-1)));
				frame.revalidate();
				frame.repaint();
			}
				};

				worker.execute();
				return true;

	}
	/**
	 * Computes distance vectors
	 */
	private void computeDistanceVec()
	{
		for (int i = 0; i < this.V.size(); i++)
		{
			double minDist = -1;
			int throughWho = -1;

			for (int j = 0 ; j < this.N.size(); j++)
			{
				if(this.QList.get(this.N.get(j)).get(this.V.get(i)) != null)
				{
					if (minDist < 0 || minDist > this.QList.get(this.N.get(j)).get(this.V.get(i)))
					{
						minDist = this.QList.get(this.N.get(j)).get(this.V.get(i));
						throughWho = this.N.get(j);
					}
				}
			}
			this.dv.put(this.V.get(i),minDist);
			this.shortestList.put(this.V.get(i),throughWho);
		}
	}    
	/**
	 * Finds its neighbors
	 */
	private void findNeighbors()
	{
		for (Map.Entry<Integer, Point> entry: this.sensorsLoc.entrySet())
		{

			double dist = Math.sqrt( 
					Math.pow((this.position.getX()-entry.getValue().getX()),2)+
					Math.pow((this.position.getY()-entry.getValue().getY()),2)
					);
			if (dist <= SwirlController.SENSORS_COM_RANGE && entry.getKey() != this.selfId && !this.neighbors.contains(entry.getKey()))
			{
				this.neighbors.add(entry.getKey());
			}

		}

	}
	/**
	 * Sends HELLO messages
	 * @throws InterruptedException
	 */
	private void initialize() throws InterruptedException
	{
		if (this.initDone)
		{
			return;
		}
		findNeighbors();
		Iterator<Integer> it = neighbors.iterator();
		while(it.hasNext())
		{
			int n = it.next();
			this.msgList.get(n).offer(new Message(this.selfId,Message.msgType.HELLO,this.position, n));	
		}
		this.initDone = true;
		if (SwirlController.SHOW_DEBUG_MESSAGES)
		{
			System.out.println("Sensor " + this.selfId + " is initialized!" );
		}
		delay();
		delay();
		setupGraphics(state.DEFAULT);
	}
	/**
	 * Sends estimate messages
	 * @throws InterruptedException
	 */
	private void estimate() throws InterruptedException
	{
		this.computeDistanceVec();
		Iterator<Integer> it = N.iterator();
		this.setupGraphics(state.ESTIMATE);
		while(it.hasNext())
		{
			int n = it.next();
			msgList.get(n).offer(new Message(this.selfId,Message.msgType.ESTIMATE,this.dv, n));	
		}	
		this.delay();
		this.setupGraphics(state.DEFAULT);

	}
	/**
	 * Handles received messages
	 * @param m message
	 * @return true if done
	 */
	private boolean receive(Message m)
	{
		if (m == null)
		{
			return false;
		}
		if (m.type == Message.msgType.HELLO && !N.contains(m.senderId))
		{
			this.initReceived++;
			this.N.add(m.senderId);
			this.V.add(m.senderId);

			double dist = Math.sqrt( 
					Math.pow((this.position.getX()-m.position.getX()),2)+
					Math.pow((this.position.getY()-m.position.getY()),2)
					);
			double estimate = (dist/SwirlController.ROBOT_SPEED)*1000;
			Map<Integer,Double> d = new HashMap<Integer,Double>();
			d.put(m.senderId,estimate);
			this.QList.put(m.senderId,d);
			this.dv.put(m.senderId,estimate);
			this.shortestList.put(m.senderId,m.senderId);
			this.msgList.get(m.senderId).offer(new Message(this.selfId,Message.msgType.HELLO,this.position, m.senderId));
			return true;
		}


		if (m.type == Message.msgType.ESTIMATE)
		{
			for (Map.Entry<Integer, Double> entry : m.dv.entrySet())
			{
				if (entry.getKey() == this.selfId)
				{
					continue;
				}
				double newEstimate = entry.getValue() + this.QList.get(m.senderId).get(m.senderId);
				this.QList.get(m.senderId).put(entry.getKey(), newEstimate);
				if (!this.V.contains(entry.getKey()))
				{
					this.V.add(entry.getKey());
					this.dv.put(entry.getKey(),newEstimate);
					this.shortestList.put(entry.getKey(),m.senderId);
				}
			}
			return true;
		}
		if (m.type == Message.msgType.QUERY)
		{
			if (!this.V.contains(m.destination))
			{
				boolean putBack = this.msgList.get(this.selfId).offer(new Message(m.robotId,Message.msgType.QUERY,m.destination,this.selfId));
				if (!putBack)System.out.println("Failed to put back" + putBack);
			}else
			{
				computeDistanceVec();
				this.targetList.put(m.robotId, new Message(this.selfId,Message.msgType.TARGET,this.shortestList.get(m.destination), m.robotId));

			}
			return true;
		}
		if (m.type == Message.msgType.UPDATE){
			double updatedValue = this.QList.get(m.updateId).get(m.destination) + 
					SwirlController.ALPHA*(m.timer -QList.get(m.updateId).get(m.updateId));
			this.QList.get(m.updateId).put(m.destination, updatedValue);
			if (SwirlController.SHOW_DEBUG_MESSAGES)
			{
				System.out.println("Update distance from sensor " + this.selfId + " to " + m.updateId + " with " + m.timer +
						" updated by "+ m.robotId);
			}
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{


		try {
			this.initialize();
		} catch (InterruptedException e) {
			System.out.println("interrupted!");
			e.printStackTrace();
		}
		this.timer(SwirlController.START);
		while (true)
		{
			if (this.timer(SwirlController.STOP) >= SwirlController.ESTIMATE_TIME_INETRVAL)
			{
				this.timer(SwirlController.START);
				try {
					this.estimate();
				} catch (InterruptedException e) {
					System.out.println("interrupted!");
					e.printStackTrace();
				}
			}	

			while(!this.msgList.get(this.selfId).isEmpty())
			{
				receive(this.msgList.get(this.selfId).poll());
			}

			if (this.robotIsArrived.size() == SwirlController.NUMBER_OF_ROBOTS)
			{
				if (SwirlController.SHOW_DEBUG_MESSAGES)
				{
					System.out.println("Sensor "+ this.selfId + " exited!");
				}
				break;
			}
		}
	}
}
