
/** Test code
 *
 * @author Chris Dixon
 * @version 1.0
 */

import java.net.*;
import java.io.*;
import java.util.*;



public class Client1 {
    /**
    * Default constants, probably not final.
    * Note: Should probably add template for shared constants.
    */
    private static final int SERVER_PORT = 3834;
    
    /**
     * Character to close Thread..
     */
    private static final String ESC_SEQ= "X";
    
    /**
     * Constructor.
     */
    
    Client1(){}
    
    public void start() throws IOException{
        Scanner input = new Scanner(System.in);
        InetAddress ip = InetAddress.getByName("localhost");
        Socket cs = new Socket(ip,SERVER_PORT); 
        DataInputStream cin = new DataInputStream(cs.getInputStream());
        DataOutputStream cout = new DataOutputStream(cs.getOutputStream());
        while (true){
            String rcv = cin.readUTF();
            System.out.println(rcv);
            if(input.hasNextLine()){
                if(input.nextLine().equals(ESC_SEQ)){
                    cout.writeUTF(ESC_SEQ);
                    break;
                }
            }

        }
        System.out.println("Closing connection");
        input.close();
        cs.close();
        cin.close();
        cout.close();
    }

}
