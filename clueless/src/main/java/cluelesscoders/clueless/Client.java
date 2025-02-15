package cluelesscoders.clueless;

import java.net.*;
import java.io.*;
import java.util.*;

/** Code to run client 
 *
 * @author Chris Dixon
 * @version 1.0
 */

public class Client {
    
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
    Client(){}
    
    /**
     * start(): starts up client for clueless and sends player input to server
     * @throws IOException 
     */
    public void start() throws IOException{
        
        InetAddress ip = InetAddress.getByName("localhost");
        
        Socket cs = new Socket(ip,SERVER_PORT); 
        
        DataInputStream cin = new DataInputStream(cs.getInputStream());
        DataOutputStream cout = new DataOutputStream(cs.getOutputStream());
        
        Scanner input = new Scanner(System.in);
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
