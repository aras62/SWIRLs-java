package swirl;
import java.util.*;
import java.awt.*;
/**
 * Class that contains messages passed i system
 * @author Amir Rasouli
 *
 */
public class Message {
	/**
	 * Types of messages
	 */
	public enum msgType {
		HELLO,TARGET,UPDATE,ESTIMATE, QUERY
	}
	//**********************Between Sensors************************************	
	/**
	 * HELLO message constructor
	 * @param id sensor's ID
	 * @param ty type of message
	 * @param myPos sender's position
	 * @param rId receiver's ID
	 */
	Message(int id,msgType ty, Point myPos, int rId)
	{
		this.senderId = id;
		this.type = ty;
		this.position = myPos;
		this.receiverId = rId;
	}
	/**
	 *  ESTIMATE message constructor
	 * @param id sensor's ID
	 * @param ty type of message
	 * @param v sender's distance vector
	 * @param rId receiver's ID
	 */
	Message(int id, msgType ty, Map<Integer,Double> v, int rId)
	{
		this.senderId = id;
		this.type = ty;
		this.dv = v;
		this.receiverId = rId;
	}
	//**********************Between Robots************************************
	/**
	 * UPDATE message constructor
	 * @param roboId robot's ID
	 * @param ty message type
	 * @param time time
	 * @param rId receiver's ID
	 * @param uId update distance ID
	 * @param dId destination
	 */
	Message(char roboId, msgType ty, long time, int rId, int uId, int dId)
	{
		this.type = ty;
		this.timer = time;
		this.receiverId = rId;
		this.updateId = uId;
		this.destination = dId;
		this.robotId = roboId;
	}
	/**
	 * TARGET message constructor
	 * @param id sender's ID
	 * @param ty message type
	 * @param tId target ID
	 * @param roboId robot's ID
	 */
	Message(int id,msgType ty, int tId, char roboId)
	{
		this.senderId = id;
		this.type = ty;
		this.targetId = tId;
		this.robotId = roboId;
	}
	/**
	 * QUERY message
	 * @param roboId robot's ID
	 * @param ty message type
	 * @param dId destination ID
	 * @param rId receiver's ID
	 */
	Message(char roboId,msgType ty,  int dId, int rId)
	{
		this.robotId = roboId;
		this.type = ty;
		this.destination = dId;//destination ID
		this.receiverId = rId;

	}
	/**
	 * Default constructor
	 */
	Message()
	{
		this.type = null;
		this.position = new Point(0,0);
		this.dv = null;
		this.timer =-1;
		this.updateId = -1;
		this.targetId = -1;
		this.receiverId = -1;
	}
	/**
	 * Sender's ID
	 */
	public int senderId;
	/**
	 * Message type
	 */
	public msgType type;
	/**
	 * Receiver's ID
	 */
	public int receiverId;
	/**
	 * Robot's ID
	 */
	public char robotId; 
	/**
	 * Sender's position
	 */
	public Point position;
	/**
	 * Distance vector of sender
	 */
	public  Map<Integer,Double> dv;
	/**
	 * Time
	 */
	public long timer;
	/**
	 * Updated node ID
	 */
	public int updateId;
	/**
	 * Destination ID
	 */
	public int destination;
	/**
	 * Target location ID
	 */
	public int targetId;
	/**
	 * Query ID
	 */
	public int queryId; 
}
