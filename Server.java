/*
Authors: 
Greagorey Markerian
Alp Deniz Senyurt
*/



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server
{  
   public static void main(String[] args) throws IOException
   {
       Inventory inv = new Inventory();
       ServerSocket server = new ServerSocket(Protocol.PORT);

      while (true)
      {
         System.out.println("Waiting for clients to connect...");
         Socket client = server.accept();
         System.out.println("Client connected.");
         InvService service = new InvService(client, inv);
         Thread t = new Thread(service);
         t.start();
      }
   }
}

class InvService implements Runnable, Protocol
{
   private Socket client;
   private DataInputStream in;
   private DataOutputStream out;
   private Inventory inv;

    public InvService(Socket client, Inventory inv)
   {
      this.client = client;
      this.inv = inv;
   }

   public void run()
   {
      try
      {
         in = new DataInputStream(client.getInputStream());
         out = new DataOutputStream(client.getOutputStream());
         doInventory();           
        
      }
      catch (IOException exception)
      {
         System.out.println("something is wrong");
         // do nothing
      } 
      finally
      {
        
         try{
            client.close();
         }  
         catch (IOException exception){
            // do nothing
         }  
        }
   }
    public void doInventory() throws IOException {      
      int command=0;
      String item;
      int amt;
      int check;
      while(command!=Protocol.QUIT)
      {  
         command = in.readInt();
         switch(command){
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
               if(check != -1)
               {
                 out.writeInt(Protocol.SUCCEED);
                 out.writeInt(check);
               }
               else{
                out.writeInt(Protocol.FAILED);
               }
               out.flush();
            break;  
            case Protocol.TAKE_ITEM :
               item = in.readUTF();
               amt = in.readInt();
               check = inv.takeItem(item,amt);
                if(check != -1)
               {
                 out.writeInt(Protocol.SUCCEED);
                 out.writeInt(check);
               }
               else{
                 out.writeInt(Protocol.FAILED);
               }
               out.flush();
            break;
            case Protocol.GET_THRESHOLD:
               amt = in.readInt();
               String thresh = inv.getThreshold(amt);
               if(thresh != "")
               {
                 out.writeInt(Protocol.SUCCEED);
                 out.writeUTF(thresh);
               }
               else{
                   out.writeInt(Protocol.FAILED);
               }
               out.flush();
            break;
            case Protocol.QUIT:
               out.writeInt(Protocol.SUCCEED);
               out.flush();
            break;

            default:
               out.writeInt(Protocol.INVALID_COMMAND);
               out.flush();
         }
      }
    }
}
