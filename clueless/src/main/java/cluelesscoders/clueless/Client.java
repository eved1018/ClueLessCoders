package cluelesscoders.clueless;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import cluelesscoders.clueless.Packet;
import cluelesscoders.clueless.Player;


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


    public Boolean readInput(Scanner input, String line){
        if (input.hasNextLine()) {
            line = input.nextLine();
            if (line.equals(ESC_SEQ)){
                return false;
            }
        }
        return true;
    }
   


    public Boolean handlePacketRcv(Packet pkt, Scanner input, ObjectOutputStream out){
        try {
            if (pkt instanceof TextPacket) {
                TextPacket tp = (TextPacket) pkt;
                System.out.println("Server: " + tp.text);
                 
                if (tp.respond){
                    System.out.print("Respond: ");
                    String line = null;
                    if (input.hasNextLine()) {
                        line = input.nextLine();
                    }
                    TextPacket tps = new TextPacket(line);
                    out.writeObject(tps);
                    
                }

            }


            else if (pkt instanceof TurnRequest ) {
                TurnRequest tp = (TurnRequest) pkt;
                
                // tp will have some info about what turn types the player can make 
                System.out.println("Server: Its your turn. Pick one of [M]ove [S]uggest [A]ccuse");
                // prompt for turn 
                String line = null;
                if (input.hasNextLine()) {
                    line = input.nextLine();
                }

                if (line.toLowerCase().equals("m")){
                    System.out.println("Enter a room to move too: " + tp.valid_moves);
                    String line2 = null;
                    if (input.hasNextLine()) {
                        line2 = input.nextLine();
                    }
                    //TODO  check that line is a valid room:

                    Room room = Room.valueOf(line2);
                    PlayerMove pm = new PlayerMove(room);
                    out.writeObject(pm);
                }
            
            } else if (pkt instanceof NewPlayerbroadcast) {
                NewPlayerbroadcast npb = (NewPlayerbroadcast) pkt;
                System.out.println("Print new player " + npb.name);
            } else if (pkt instanceof GameStartBroadcast){
                // GameStartBroadcast gsb = (GameStartBroadcast) pkt;
                System.out.println("Game Starting");
            } else if (pkt instanceof BroadcastMove ){
                BroadcastMove bm = (BroadcastMove) pkt;
                System.out.println("Player " + bm.name + " moved to " + bm.new_room);
            } else if (pkt instanceof DisproveRequest ){

                DisproveRequest dr = (DisproveRequest) pkt;
                System.out.println("Disprove with " + dr.options);
                String disprove_with = null;
                if (input.hasNextLine()) {
                    disprove_with = input.nextLine();
                }
                // to any error handeling here
                DisproveResponse disproveResponse = new DisproveResponse(disprove_with);
                out.writeObject(disproveResponse);
            } else if (pkt instanceof SuggestionResponse)  {
                SuggestionResponse  sr = (SuggestionResponse) pkt;
                System.out.println("Suggestion was disproved by " + sr.disprover + " with " + sr.disproved_with);
            } else if (pkt instanceof BroadcastPlayerOut ) {
                BroadcastPlayerOut bpo = (BroadcastPlayerOut) pkt;
                System.out.println("Player " + bpo.player);
            } else if (pkt instanceof DisproveSkip ) {
                DisproveSkip dsk = (DisproveSkip) pkt;
                System.out.println("Player " + dsk.player + " could not disporve suggestion");
            } else if (pkt instanceof SuggestBroadcast) {
                SuggestBroadcast sgb = (SuggestBroadcast) pkt;
                System.out.println("Player " + sgb.suggester + " Suggests the murder was done by " + sgb.suspect + " in the " + sgb.room + " with the " + sgb.weapon );
            
            } else if (pkt instanceof BroadcastGameOver) {
                BroadcastGameOver  bgo = (BroadcastGameOver) pkt;
                System.out.println(bgo.winner + " has won the game");
            } else if (pkt instanceof DisproveBroadcast){
                DisproveBroadcast dbc = (DisproveBroadcast) pkt;
                System.out.println("Player " + dbc.disprover + " disproved the suggestion");
            }            
            else {

                System.out.println("Cant decode packet");


            }
        } catch (IOException e) {
            System.out.println(e);
        }
            

        return true;

    }
    
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

        Scanner input = new Scanner(System.in);
        Packet p;        
        Boolean run = true;
        try {
            //initial respponse from sever
            p = (Packet) cin.readObject();
            run =  handlePacketRcv(p, input, cout);

            while (run){
                
                // // wait until server asks for something or tell us to update UI:
                p = (Packet) cin.readObject();
                run =  handlePacketRcv(p, input, cout);
                

                
            }
            
        } catch (ClassNotFoundException ex) {
            System.err.println("Error");
        }
           
        System.out.println("Closing connection");
        input.close();
        cs.close();
        cin.close();
        cout.close();
    }
}
