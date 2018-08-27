package swirl;
import java.util.*;
import java.util.Queue;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
/**
 * This class implements sensor for Sequential implementation
 * @author Amir Rasouli
 *
 */
public class SensorSeq {
	/**
	 * Constructor
	 * @param id sensor's ID
	 * @param pos sensor's position
	 * @param comRange sensors's communication range
	 * @param speed robot's maximum speed
	 * @param f graphics frame
	 * @param gc graphic components
	 */
	public SensorSeq(int id, Point pos,int comRange, int speed, JFrame f, Map<Character,Component> gc)
	{
		this.broadcastDone = false;
		this.currentState = state.DEFAULT;
		this.graphics = gc;
		this.frame = f;
		this.dv = new HashMap<Integer, Double>();
		this.N= new ArrayList<Integer>();
		this.neighbors= new ArrayList<Integer>();
		this.position = pos;
		this.QList = new HashMap<Integer,Map<Integer, Double>>();
		this.selfId = id;    
		this.shortestList = new HashMap<Integer,Integer>();
		this.V= new ArrayList<Integer>();
		Map <Integer,Double> self = new HashMap<Integer,Double>();
		self.put(selfId,0.);
		this.QList.put(selfId, self);
		this.intSent = new LinkedList<Integer>();
	}
	private final Queue<Integer> intSent;
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
	 * Indicate whether Hello messages are sent
	 */
	public boolean broadcastDone;
	/**
	 * List of distances
	 */
	private final Map<Integer,Map<Integer, Double>> QList;
	/**
	 * Distance Vector
	 */
	public Map<Integer,Double> dv;
	/**
	 * List of shortest distances <distance,throughWho>
	 */
	private final Map<Integer,Integer> shortestList; 
	/**
	 * Sensor's ID
	 */
	public int selfId;
	/**
	 * Sensor's position
	 */
	public Point position;
	/**
	 * Sensor's current state
	 */
	public state currentState;
	/**
	 * Graphics frame
	 */
	public JFrame frame;
	/**
	 * Graphics component
	 */
	public Map<Character,Component> graphics;
	/**
	 * Sensor's states
	 */
	public enum state { DEFAULT,ESTIMATE  }
	/**
	 * Creates delay for graphic
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
		this.currentState  = st;
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
	 * Calculates distance vector
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
					if (minDist < 0 || minDist > this.QList.get(this.N.get(j)).get(this.V.get(i)) )
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
	 * Find neighbors
	 * @param sensorLocs sensors locations
	 */
	private void findNeighbors(Map<Integer,Point> sensorLocs)
	{
		for (Map.Entry<Integer, Point> entry: sensorLocs.entrySet())
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
	 * Initializes the sensor
	 * @param sensorLocs sensors locations
	 * @param msg messages received
	 * @return responses
	 */
	public java.util.List<Message> initialize(Map<Integer,Point> sensorLocs, java.util.List<Message> msg)
	{
		java.util.List<Message> response= new ArrayList<Message>();

		if (msg != null)
		{
			for (int i = 0; i < msg.size(); i++)
			{
				response.add(receive(msg.get(i)));
			}
		}

		if (this.broadcastDone)
		{
			return response;
		}

		findNeighbors(sensorLocs);

		for (int i = 0; i < this.neighbors.size();i++)
		{
			if (this.N.contains(this.neighbors.get(i)))
			{
				continue;
			}
			this.intSent.offer(this.neighbors.get(i));
			response.add( new Message(this.selfId,Message.msgType.HELLO,this.position, this.neighbors.get(i)));

		}
		this.broadcastDone = true;
		setupGraphics(state.DEFAULT);	
		return response;
	}
	/**
	 * Perform estimation for the sensor by sending and receiving messages
	 * @param msg message received
	 * @param estDone indicates if estimation is done
	 * @return list of responses
	 */
	public java.util.List<Message> estimate(java.util.List<Message> msg, boolean estDone)
	{
		java.util.List<Message> response= new ArrayList<Message>();

		if (msg != null)
		{
			for (int i = 0; i < msg.size(); i++)
			{
				receive(msg.get(i));
			}
		}
		if (estDone)
		{
			return null;
		}
		computeDistanceVec();
		for (int i = 0; i < this.N.size();i++)
		{
			response.add( new Message(this.selfId,Message.msgType.ESTIMATE,dv, this.N.get(i)));

		}
		setupGraphics(state.ESTIMATE);
		delay();
		setupGraphics(state.DEFAULT);
		return response;
	}
	/**
	 * Handles received messages
	 * @param m message
	 * @return response
	 */
	public Message receive(Message m)
	{
		if (m == null)
		{
			return null;
		}
		if (m.type == Message.msgType.HELLO && !N.contains(m.senderId))
		{
			this.N.add(m.senderId);
			this.V.add(m.senderId);
			double dist = Math.sqrt( 
					Math.pow((this.position.getX()-m.position.getX()),2)+
					Math.pow((this.position.getY()-m.position.getY()),2)
					);
			double estimate = (dist/SwirlController.ROBOT_SPEED)*1000;
			Map<Integer,Double> d = new HashMap<Integer,Double>();
			d.put(m.senderId, estimate);
			this.QList.put(m.senderId,d); 
			this.dv.put(m.senderId,estimate);
			this.shortestList.put(m.senderId,m.senderId);
			if (!this.intSent.contains(m.senderId))
			{
				this.intSent.offer(m.senderId);
				return new Message(this.selfId,Message.msgType.HELLO,this.position, m.senderId);
			}
			return null;
		}
		if (m.type == Message.msgType.ESTIMATE)
		{
			for (Map.Entry<Integer, Double> entry : m.dv.entrySet())
			{
				if (entry.getKey() == this.selfId) //here is changed
				{
					continue;
				}

				double newEstimate = entry.getValue() + this.QList.get(m.senderId).get(m.senderId);
				this.QList.get(m.senderId).put(entry.getKey(), newEstimate);
				if (!this.V.contains(entry.getKey()) )
				{
					this.V.add(entry.getKey());
					this.dv.put(entry.getKey(),newEstimate);
					this.shortestList.put(entry.getKey(),m.senderId);
				}
			}
		} 
		if (m.type == Message.msgType.QUERY)
		{
			if (!V.contains(m.destination))
			{

				return new Message(this.selfId,Message.msgType.TARGET,-1, m.robotId);
			}else
			{

				return new Message(this.selfId,Message.msgType.TARGET,this.shortestList.get(m.destination), m.robotId);
			}
		}
		if (m.type == Message.msgType.UPDATE){
			double updatedValue = this.QList.get(m.updateId).get(m.destination) + 
					SwirlController.ALPHA*(m.timer -QList.get(m.updateId).get(m.updateId));
			this.QList.get(m.updateId).put(m.destination, updatedValue);
			if (SwirlController.SHOW_DEBUG_MESSAGES)
			{
				System.out.println("Update distance from sensor " + this.selfId + " to " + m.updateId + " with " + m.timer);
			}
		}

		return null;//new Message();
	}





}
