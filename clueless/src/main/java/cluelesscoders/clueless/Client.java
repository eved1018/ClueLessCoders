package cluelesscoders.clueless;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
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
        
        System.out.println("Client started ");
        
        ObjectOutputStream cout = new ObjectOutputStream(cs.getOutputStream()); // Note: cout must be initialized before cin 
        ObjectInputStream cin = new ObjectInputStream(cs.getInputStream());
        System.out.println("Client started 2");
        

        Scanner input = new Scanner(System.in);
        String next_line;
        Packet p;

        while (true){
            try {
                System.out.println("Packet sent from server: ");
                Packet rcv = (Packet) cin.readObject();
                System.out.println(rcv.text);
                System.out.println(rcv.arr.get(0));
                
                System.out.println("Enter packet to sent to server: ");
                if(input.hasNextLine()){
                    next_line = input.nextLine();
                    if(next_line.equals(ESC_SEQ)){
                        p = new Packet(0, ESC_SEQ);
                        cout.writeObject(p);
                        break;
                    }
                    else {
                        p = new Packet(5, next_line);
                        cout.writeObject(p);
                    }
                }
            } catch (ClassNotFoundException ex) {
                System.err.println("Error");
            }
           

        }
        System.out.println("Closing connection");
        input.close();
        cs.close();
        cin.close();
        cout.close();
    }
}
