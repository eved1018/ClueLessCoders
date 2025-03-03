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
            broadcastNewPlayer(new_player.name);
        }
    }

    public void broadcastNewPlayer(PlayerName n){
        for (Player p : this.playerList){
            p.sendNewPlayer(n);
        }
    }

    public void broadcastGameStart(){
        int i = 0;
        for (Player p : this.playerList){
            Room start_room = this.game.get_starting_room(p.name);
            ArrayList<String> player_hand = this.game.get_hand(p.name);
            p.sendGameStart(player_hand, start_room, i);
            i+=1;
        }        

    }

    public void broadcastDisproveSkip(PlayerName n){
        // player name cant disprove
        for (Player p : this.playerList){
            p.sendDisproveSkip(n);
        }

    }

    public void suggestBroadcast(PlayerName suspect, Weapon weapon, Room room, PlayerName suggester){
        // suggester made suggestion 
        for (Player p : this.playerList){
            p.sendSuggestion(suspect, weapon, room, suggester);
        }
    }

    public void broadcastMove(PlayerName name, Room room) {
        // player has moved to room by player 

        for (Player p : this.playerList){
            p.sendBroadcastMove(name, room);
        }
    }

    public void broadcastDisprove(PlayerName disprover, PlayerName suggester){
        for (Player p : this.playerList){
            p.sendDisproveBroadcast(disprover, suggester);
        }

    }

    public void broadcastGameOver(PlayerName name){
        for (Player p : this.playerList){
            p.sendGameOver(name);
        }

        // game over player has won
    }

    public void broadcastPlayerOut(PlayerName name){
        // player has made false accusation 
        for (Player p : this.playerList){
            p.sendPlayerOut(name);
        }
    }

    public void suggestionLoop(PlayerSuggestion sgt, Player p ) {

        // move suggested player to room

        this.game.update_board(sgt.suspect, p.currRoom);
        for (Player i: this.playerList) {
            if (i.name.equals(sgt.suspect)) {
                i.currRoom = p.currRoom;
                i.moved_by_suggest = true;
            }
        }
        
        // can prob combine these
        broadcastMove(sgt.suspect, p.currRoom); // this should handle updating the suspects position on the UI
        suggestBroadcast(sgt.suspect, sgt.murder_weapon, p.currRoom, p.name); // Notify all players of suggestion (ie a ui thing)

        // ring buffer
        int size = playerList.size();
        int start = p.player_number;
        Player op;
        String disprove_with;
        for (int i = 0; i < size; i++){
            int index = (start + 1 + i ) % size; // wrap list 
            op = this.playerList.get(index);
            ArrayList<String> oph = this.game.get_hand(op.name);
            ArrayList<String> suspecthand = new ArrayList<String>();
            suspecthand.add(sgt.suspect.toString());
            suspecthand.add(sgt.murder_weapon.toString());
            suspecthand.add(p.currRoom.toString());

            // set notation to get intersection 
            Set<String> overlap = oph.stream()
                .distinct()
                .filter(suspecthand::contains)
                .collect(Collectors.toSet());

            if (overlap.size() > 0){

                if (overlap.size() == 1) {
                    disprove_with = overlap.stream().findFirst().orElse(null);
    
                } else {
                    DisproveResponse dr = op.sendDisproveRequest(suspecthand);
                    disprove_with = dr.disprove_with;
                }                
                p.sendSuggestResponse(op.name, disprove_with);
                broadcastDisprove(op.name, p.name);
            }

            else {
                broadcastDisproveSkip(op.name); // player x cant disprove 
            } 
        }
    }


    public Boolean gameLoop() throws IOException{

        // Game is ready 
        ArrayList<PlayerName> names = new ArrayList<>();
        for (Player player : this.playerList) {
            names.add(player.name);
        }

        this.game.start_game(names);
        broadcastGameStart();

        for (Player p: this.playerList){

            if (p.is_out){
                //TODO not sure what the rules r for for out player
                return true;
            }
            
            // check if the player can move ie are the hallways blocked
            ArrayList<Room> moves =  this.game.possible_moves(p.name);

            Boolean can_suggest = true;
            // check if player can suggest: player cant move + wasnt moved by suggestion
            if (moves.size() == 0 && !p.moved_by_suggest) {
                // player can only accuse 
                can_suggest = false;
            }

            // request a turn from this player 
            Packet rpkt = p.playerTurnRequest(can_suggest, moves);

            if (rpkt instanceof PlayerMove){
                // update board 
                PlayerMove pm = (PlayerMove) rpkt;
                p.currRoom = pm.destination;
                this.game.update_board(p.name, pm.destination);
                broadcastMove(p.name, p.currRoom); // Update all players UI


            } else if (rpkt instanceof PlayerSuggestion) {
                PlayerSuggestion sgt = (PlayerSuggestion) rpkt;
                if (sgt.is_accusation) {
                    if (this.game.check_accusation(sgt.murder_weapon, sgt.suspect, p.currRoom)){
                        // game over 
                        System.out.println("Game over " + p.name + " won");
                        broadcastGameOver(p.name);
                        return false;
                    } else {
                        p.is_out = true;
                        broadcastPlayerOut(p.name);
                    }
                } else {
                    suggestionLoop(sgt, p);
                }

            }

        }
        return true;
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
            
            System.out.println(currPlayerCt);
            waiting_room();
            System.out.println("All players connected - Starting game");
        
            // wait for a player to start the game            
            while (run_game) {
                run_game = gameLoop(); 
            }

            System.out.println(runServer);

        
            if(input.nextLine().equals(ESC_SEQ)){
                // checkConnections.interrupt();
                clServer.close();
                break;
            }
        }

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