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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;




public class Server {
    
    private ServerSocket clServer;
    private ObjectInputStream  pin;
    private ObjectOutputStream  pout;
    public AtomicInteger currPlayerCt = new AtomicInteger(0); 
    public ArrayList<Player> playerList;
    public Game game = null;
    
            
    /**
     * Default constants, probably not final.
     * Note: Should probably add template for shared constants.
     */
    private static final int SERVER_PORT = 3834;
    // private static final int MAX_PLAYERS = 6;
    private static final int MAX_PLAYERS = 2;
    
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
            while (currPlayerCt.get() < MAX_PLAYERS){

                try{       
                    Socket playerSocket =  clServer.accept();                        
                    
                    pin = new ObjectInputStream(playerSocket.getInputStream());
                    pout = new ObjectOutputStream(playerSocket.getOutputStream());
                    
                    int player_num = currPlayerCt.getAndIncrement();

                    Player new_player = new Player(playerSocket, pin, pout, player_num);
                    playerList.add(new_player);
                    broadcastNewPlayer(new_player.name);

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


    public void waiting_room() throws IOException {
        while (currPlayerCt.get() < MAX_PLAYERS){
            System.out.println("Waiting for players to connect");

            Socket playerSocket =  clServer.accept();                        
                    
            pin = new ObjectInputStream(playerSocket.getInputStream());
            pout = new ObjectOutputStream(playerSocket.getOutputStream());
            
            int player_num = currPlayerCt.getAndIncrement();

            Player new_player = new Player(playerSocket, pin, pout, player_num);
            playerList.add(new_player);
            // game.broadcastNewPlayer(new_player.name);
        }
    }

    public void chat_room() {
        while (true) {
            for (Player p: this.playerList){ 
                SocketPacket m = p.sendTextRequest("Respond With Message to broadcast: ");
                System.out.println("Player " + p.name + " Sent: " + m.message);
                for (Player l: this.playerList){
                    if (l.equals(p)) {
                        continue;
                    }
                    l.broadcastMessage("Player " + p.name + " Sent: " + m.message, p);
                }
                
            }
            
        }
    }

    
    /**
     * start() starts the server for clueless and checks for server
     *         shutdown parameters
     * @throws IOException 
     */
    public void start() throws IOException{
        boolean runServer = true;
        System.out.println("Server Up");
        Scanner input = new Scanner(System.in); 
        
        this.playerList = new ArrayList<Player>();
        this.game = new Game();
        Boolean run_game = true;
        while(runServer){
            
            // wait for a player to start the game            
            System.out.println(currPlayerCt);
            waiting_room();
            System.out.println("All players connected - Starting game");
            chat_room();
        
            // game.start_game(playerList);
            // while (run_game) {
            //     run_game = game.game_loop(); 
            // }

            System.out.println(runServer);

        
            if(input.nextLine().equals(ESC_SEQ)){
                // checkConnections.interrupt();
                clServer.close();
                break;
            }
        }
        input.close();
    }
}
    

// /**
//  * PlayerThread: Thread child to communicate with each player independently
//  * @author Chris Dixon
//  */
// class PlayerThread extends Thread{
    
//     private final Socket s;
//     private final ObjectInputStream  in;
//     private final ObjectOutputStream  out;
//     private final int playerNum;
    
//     /**
//      * Character to close Thread..
//      */
//     private static final String ESC_SEQ= "X";
    
//     /**
//      * Constructor
//      * @param s player socket
//      * @param in input stream from client
//      * @param out output stream to client
//      * @param playerNum player index, a little buggy
//      * @throws IOException 
//      */
//     PlayerThread(Socket s, ObjectInputStream in, ObjectOutputStream out, int playerNum) throws IOException{
//         this.s = s;
//         this.in = in;
//         this.out = out;
//         this.playerNum = playerNum;
//     }

    
    
//     /**
//      * Basic Output communicating to server and player
//      * No complexity yet...
//      */
//     @Override
//     public void run(){
//         try{

//             System.out.println("Player " + playerNum + " connected");
//             Packet p = new TextPacket("Welcome Player Number " + playerNum);
//             out.writeObject(p);

//             while(true){
//                 Object rev_obj =  in.readObject();
//                 if (!(rev_obj instanceof Packet)) {
//                     break;
//                 }
//                 TextPacket input = (TextPacket) rev_obj;
//                 String input_text = input.text;
//                 if(input_text.equals(ESC_SEQ)){
                    
//                     this.s.close();
//                     this.in.close();
//                     this.out.close();
                    
//                     System.out.println("Player " + playerNum + " disconnected");
                    
//                     break;
//                 }
//                 else {
                    
//                     System.out.println("Message from player " + playerNum + ":\t" + input_text);
//                     TextPacket r = new TextPacket("Message received");
//                     out.writeObject(r);
//                 }
                
//             }
//         }
//         catch(IOException e){
//             System.out.println(e);
//         } catch (ClassNotFoundException ex) {
//         }
//     }
             
// }