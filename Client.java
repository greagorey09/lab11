/**
* Assignment 11
* @author Alp Deniz Senyurt
* Student ID: 100342433
* @author Greagorey Markerian
* Student ID: 100338209
* Self explanatory variables and parameters will not be documented as they are, "self-explanatory".
*/

import java.io.*;
import java.io.DataInputStream;
import java.net.*;
import java.util.*;


public class Client implements Runnable, Protocol
{
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private ProtocolFunctional protocol;

	public static void main(String[] args)
	{
		for (int i = 0; i < 5; i++)
		{
			Runnable client = new Client();
			Thread t = new Thread(client);
			t.start();
		}
	}

	public Client()
	{	
		try
		{
			protocol = (int command, int response, DataInputStream in) -> {
				String tempStr = "";
				try
				{
					switch (response)
					{
						case Protocol.SUCCEED:
							tempStr = "Server responded with: SUCCEED. \n";
							switch (command)
							{
								case Protocol.ADD_ITEM:
									tempStr += "Item has been added successfully." + "\n";
									break;
								case Protocol.CHECK_ITEM:
									tempStr += "The inventory has " + in.readInt() + " number of this item." + "\n";
									break;
								case Protocol.TAKE_ITEM:
									tempStr += in.readInt() + " items has been removed from the inventory" + "\n";
									break;
								case Protocol.GET_THRESHOLD:
									tempStr += "Items under this threshold are: " + in.readUTF() + "\n";
							}
							break;
						case Protocol.FAILED:
							tempStr = "Server responded with: FAILED!\n";
							break;
					}
				}
				catch (Exception e) { }
				return tempStr;
			};
			this.socket = new Socket("localhost", Protocol.PORT);	
			this.out = new DataOutputStream(socket.getOutputStream());
			this.in = new DataInputStream(socket.getInputStream());
		}
		catch(IOException e)
		{
			System.out.println("Error! Can't connect to the server.");
		}
	}

	public void run()
	{
		Random r = new Random();
		
		outerLoop: 
		while (! Thread.currentThread().isInterrupted())
		{
			try
			{
				var count = (r.nextInt((20 - 10) + 1) + 10); //random number of requests between 10-20. random nth command after 10th command will be QUIT command. However on the final iteration, the sent command will always be QUIT to make sure client disconnects from the server no matter what.
				for (int i = 0; i < count; i++)
				{
					if (i == count - 1)
					{
						clientActions(Protocol.QUIT);
						break outerLoop;
					}
					clientActions(r.nextInt((5 - 2) + 1) + 2); //adding randomization to command sent
					Thread.sleep(r.nextInt((500 - 100) + 1) + 100); //random delay between 100-500 ms
				}
			}
			catch (IOException e) 
			{
				System.out.println("Error with Input/Output stream!");
			}
			catch (InterruptedException e)
			{
				System.out.println("Thread interrupted!");
			}
		}
	}

	public void clientActions(int command) throws IOException
	{
		Random rand = new Random();
		String readableResponse = "";
		boolean serverConnectionClosed = false;
		var tempItem = ListInventory.list[rand.nextInt(7)];

		try
		{
			switch (command)
			{
				case Protocol.ADD_ITEM:
					out.writeInt(Protocol.ADD_ITEM);
					out.writeUTF(tempItem);
					out.writeInt(rand.nextInt((35 - 5) + 1) + 5); //random integer between 5 and 35
					out.flush();
					System.out.println("Sending ADD_ITEM command for " + tempItem);
					readableResponse = protocol.m_serverResponse(Protocol.ADD_ITEM, in.readInt(), in);
					break;
				case Protocol.CHECK_ITEM:
					out.writeInt(Protocol.CHECK_ITEM);
					out.writeUTF(tempItem);
					out.flush();
					System.out.println("Sending CHECK_ITEM command for " + tempItem);
					readableResponse = protocol.m_serverResponse(Protocol.CHECK_ITEM, in.readInt(), in);
					break;
				case Protocol.TAKE_ITEM:
					out.writeInt(Protocol.TAKE_ITEM);
					out.writeUTF(tempItem);
					out.writeInt(rand.nextInt(5)); //random int between 0-5
					out.flush();
					System.out.println("Sending TAKE_ITEM command for " + tempItem);
					readableResponse = protocol.m_serverResponse(Protocol.TAKE_ITEM, in.readInt(), in);
					break;
				case Protocol.GET_THRESHOLD:
					var tempInt = rand.nextInt(35); //random int between 0-35
					out.writeInt(Protocol.GET_THRESHOLD);
					out.writeInt(tempInt);
					out.flush();
					System.out.println("Sending GET_THRESHOLD command for number of " + tempInt + "items.");
					readableResponse = protocol.m_serverResponse(Protocol.GET_THRESHOLD, in.readInt(), in);
					break;
				case Protocol.QUIT:
					System.out.println("Sending QUIT command to server.");
					out.writeInt(Protocol.QUIT);
					out.flush();
					serverConnectionClosed = (in.readInt() == Protocol.SUCCEED) ? true : false;
					break;
				default:
					out.writeInt(command);
					out.flush();
					readableResponse = protocol.m_serverResponse(7, in.readInt(), in);
					break;
			}
		}
		catch (IOException e)
		{
			System.out.println("Error with Input/Output stream!");
		}
		finally
		{
			if (serverConnectionClosed)
			{
				try
				{
					socket.close();
					System.out.println("Disconnected from server");
				}
				catch (Exception e)
				{
					System.out.println("Error when closing the connection! Error is: " + e.toString());
				}
			}
			else
			{
				System.out.println(readableResponse);
			}
		}
	}
}