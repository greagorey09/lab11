/**
* Assignment 11
* @author Alp Deniz Senyurt
* Student ID: 100342433
* @author Greagorey Markerian
* Student ID: 100338209
* Self explanatory variables and parameters will not be documented as they are, "self-explanatory".
* This code is highly based off the example4 given to us by prof. Modifications were introduced for Inventory class
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Seperate class, when client is connected will create a new object Inservice and starts the thread
 */
public class Server
{  
	public static void main(String[] args) throws IOException
	{
		Inventory inv = new Inventory();
		ServerSocket server = new ServerSocket(Protocol.PORT);

		while (true)
		{
			System.out.println("Waiting for clients to connect...");
			Socket serverSocket = server.accept();
			System.out.println("Client connected.");
			Runnable service = new InvService(serverSocket, inv);
			Thread t = new Thread(service);
			t.start();
		}
	}
}

class InvService implements Runnable, Protocol
{
	private Socket serverSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Inventory inv;

	public InvService(Socket serverSocket, Inventory inv)
	{
		this.serverSocket = serverSocket;
		this.inv = inv;
	}
	/**
	 * runs the thread by calling the doInventory() method
	 */
	public void run()
	{
		try
		{
			in = new DataInputStream(serverSocket.getInputStream());
			out = new DataOutputStream(serverSocket.getOutputStream());
			doInventory();

		}
		catch (IOException exception)
		{
			System.out.println("Server Error!");
			try
			{
				out.writeInt(Protocol.FAILED);
				out.flush();
			}
			catch (Exception e) { }
		} 
	}
	/**
	 * method uses switch cases to recieve protocol from client. Depending on what was sent
	 * will call iventory methods and return the values to client
	 */
	public void doInventory() throws IOException
	{      
		int command = 0;
		String item;
		int amt;
		int check;

		outerWhile : 
		while (true)
		{  
			command = in.readInt();
			switch (command)
			{
				case Protocol.ADD_ITEM:
					item = in.readUTF(); 
					amt = in.readInt();
					inv.addItem(item, amt);
					out.writeInt(Protocol.SUCCEED);
					out.flush();
					break;
				case Protocol.CHECK_ITEM:
					item = in.readUTF();
					check = inv.checkInventory(item);
					if (check != -1)
					{
						out.writeInt(Protocol.SUCCEED);
						out.writeInt(check);
					}
					else
					{
						out.writeInt(Protocol.FAILED);
					}
					out.flush();
					break;  
				case Protocol.TAKE_ITEM :
					item = in.readUTF();
					amt = in.readInt();
					check = inv.takeItem(item,amt);
					if (check != -1)
					{
						out.writeInt(Protocol.SUCCEED);
						out.writeInt(check);
					}
					else
					{
						out.writeInt(Protocol.FAILED);
					}
					out.flush();
					break;
				case Protocol.GET_THRESHOLD:
					amt = in.readInt();
					String thresh = inv.getThreshold(amt);
					if (thresh != "")
					{
						out.writeInt(Protocol.SUCCEED);
						out.writeUTF(thresh);
					}
					else
					{
						out.writeInt(Protocol.FAILED);
					}
					out.flush();
					break;
				case Protocol.QUIT:
					try
					{
						out.writeInt(Protocol.SUCCEED);
						out.flush();
						serverSocket.close();
						System.out.println("Closing the connection with the client.");
						Thread.currentThread().interrupt();
						break outerWhile;
					}
					catch (Exception e)
					{
						out.writeInt(Protocol.FAILED);
						out.flush();
						System.out.println("Error! Failed to disconnect from client");
					}
				default:
					out.writeInt(Protocol.FAILED);
					out.flush();
					break;
			}
		}
	}
}