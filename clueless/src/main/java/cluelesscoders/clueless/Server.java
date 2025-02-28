package cluelesscoders.clueless;

/**
 * Code to run server for clueless
 * @author Last Edited by : Chris Dixon
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Server {
    
    private ServerSocket clServer;
    private ObjectInputStream  pin;
    private ObjectOutputStream  pout;
    private int currPlayerCt = 0; 
            
    /**
     * Default constants, probably not final.
     * Note: Should probably add template for shared constants.
     */
    private static final int SERVER_PORT = 3834;
    private static final int MAX_PLAYERS = 6;
    
    /**
     * Character to close Thread..
     */
    private static final String ESC_SEQ= "X";
    
    /**
     * checkConnections : Thread object that does looping checks for client
     * connections.
     */
    private Thread checkConnections = new Thread(){
        
        @Override
        public void run(){
            while (currPlayerCt < MAX_PLAYERS){

                try{       
                        Socket player =  clServer.accept();                        
                        
                        pin = new ObjectInputStream(player.getInputStream());
                        pout = new ObjectOutputStream(player.getOutputStream());
                        
                        currPlayerCt = 
                                Thread.currentThread().getThreadGroup().activeCount() - 2;
                        int player_num = currPlayerCt + 1;
                        
                        PlayerThread pt = new PlayerThread(player,pin,pout,player_num);
                        pt.start();
                }
                catch(SocketException e){
                    if(clServer.isClosed()){
                        System.out.println("Server Down");
                        break;
                    }                
                }
                catch(IOException e){
                    System.out.println(e);
                    break;
                }
            
            }
        }
    };
    
    /**
     * Constructor.
     */
    public Server(){
        try{
            clServer = new ServerSocket(SERVER_PORT);
        }
        catch(IOException e){
            System.out.println(e);
        }
        
    }
    
    /**
     * Constructor for specific port
     * @param port : Port of server location 
     */
    public Server(int port){
        try{
            clServer = new ServerSocket(port);
            
        }
        catch(IOException e){
            System.out.println(e);
        }
        
    }
    
    /**
     * start() starts the server for clueless and checks for server
     *         shutdown parameters
     * @throws IOException 
     */
    public void start() throws IOException{
        System.out.println("Server Up");
        Scanner input = new Scanner(System.in); 
        
        checkConnections.start();
        
        boolean runServer = true;
        
        while(runServer){
            if(input.nextLine().equals(ESC_SEQ)){
                checkConnections.interrupt();
                clServer.close();
                break;
            }
        }

    }
    
}

/**
 * PlayerThread: Thread child to communicate with each player independently
 * @author Chris Dixon
 */
class PlayerThread extends Thread{
    
    private final Socket s;
    private final ObjectInputStream  in;
    private final ObjectOutputStream  out;
    private final int playerNum;
    
    /**
     * Character to close Thread..
     */
    private static final String ESC_SEQ= "X";
    
    /**
     * Constructor
     * @param s player socket
     * @param in input stream from client
     * @param out output stream to client
     * @param playerNum player index, a little buggy
     * @throws IOException 
     */
    PlayerThread(Socket s, ObjectInputStream in, ObjectOutputStream out, int playerNum) throws IOException{
        this.s = s;
        this.in = in;
        this.out = out;
        this.playerNum = playerNum;
    }
    
    /**
     * Basic Output communicating to server and player
     * No complexity yet...
     */
    @Override
    public void run(){
        try{

            System.out.println("Player " + playerNum + " connected");
            Packet p = new Packet(0 , "Welcome Player Number " + playerNum);
            out.writeObject(p);

            while(true){
                Object rev_obj =  in.readObject();
                if (!(rev_obj instanceof Packet)) {
                    break;
                }
                Packet input = (Packet) rev_obj;
                String input_text = input.text;
                if(input_text.equals(ESC_SEQ)){
                    
                    this.s.close();
                    this.in.close();
                    this.out.close();
                    
                    System.out.println("Player " + playerNum + " disconnected");
                    
                    break;
                }
                else {
                    
                    System.out.println("Message from player " + playerNum + ":\t" + input_text + " | " + input.d);
                    Packet r = new Packet(0, "Message received");
                    out.writeObject(r);
                }
                
            }
        }
        catch(IOException e){
            System.out.println(e);
        } catch (ClassNotFoundException ex) {
        }
    }
             
}