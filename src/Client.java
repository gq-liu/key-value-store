import java.util.*;
import org.apache.xmlrpc.*;

public class Client {
   public static void main (String [] args) {
   
	  if (args.length != 3) {
	  	System.err.println("Usage: Client host:port /basedir blockSize");
		System.exit(1);
	  }

      try {
         XmlRpcClient client = new XmlRpcClient("http://localhost:8080/RPC2");

         Vector params = new Vector();

		 // Test ping
		 params = new Vector();
		 client.execute("surfstore.ping", params);
		 System.out.println("Ping() successful");

		 // Test PutBlock
		 params = new Vector();
		 byte[] blockData = new byte[10];
		 params.addElement(blockData);
         boolean putresult = (boolean) client.execute("surfstore.putblock", params);
		 System.out.println("PutBlock() successful");

		 // Test GetBlock
		 params = new Vector();
		 params.addElement("h0");
         byte[] blockData2 = (byte[]) client.execute("surfstore.getblock", params);
		 System.out.println("GetBlock() successfully read in " + blockData2.length + " bytes");

      } catch (Exception exception) {
         System.err.println("Client: " + exception);
      }
   }
}
