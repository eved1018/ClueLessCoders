package cluelesscoders.clueless;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;
import cluelesscoders.clueless.Packet;
import cluelesscoders.clueless.Player;

/**
 * Code to run client
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
    private static final String ESC_SEQ = "X";

    public Boolean readInput(Scanner input, String line) {
        if (input.hasNextLine()) {
            line = input.nextLine();
            if (line.equals(ESC_SEQ)) {
                return false;
            }
        }
        return true;
    }

    public Boolean handlePacketRcv(SocketPacket pkt, Scanner input, ObjectOutputStream out) {
        try {
            switch(pkt.packet_type){
                case MESSAGE:
                    
                    System.out.println(pkt.message);
                    if (pkt.expect_response) {
                        String line = null;
                        if (input.hasNextLine()) {
                            line = input.nextLine();
                        }
                        SocketPacket r = new SocketPacket();
                        r.message = line;
                        r.packet_type = SocketPacket.PacketType.MESSAGE; 
                        out.writeObject(r);
                    }
                    break;
                    
                case BROADCAST:
                    switch(pkt.broadcast_type){
                        case NEW_PLAYER:
                            System.out.println("Print new player " + pkt.curr_player);
                            break;
                        case GAME_STATE:
                            switch(pkt.game_state_update){
                                case START:
                                    System.out.println("Game Starting");
                                    break;
                                case END:
                                    System.out.println(pkt.curr_player + " has won the game");
                                    break;
                                default:
                                    System.out.println("Cant decode gamee state broadcast packet");
                            }
                        break;    
                        case PLAYER_TURN:
                            switch(pkt.turn_type){
                                case MOVE:
                                    System.out.println("Player " + pkt.curr_player + " moved to " + pkt.destination);
                                    break;
                                case SUGGEST:
                                    System.out.println("Player " + pkt.curr_player + " Suggests the murder was done by " + pkt.other_player
                                    + " in the " + pkt.destination + " with the " + pkt.murder_weapon);
                                    break;
                                default:
                                    System.out.println("Cant decode player turn broadcast packet");
                                    break;
                            }
                        break;
                        case DISPROVE:
                            switch(pkt.disprove_type){
                                case DISPROVEN_WITH:
                                    System.out.println("Suggestion was disproved by " + pkt.curr_player + " with " + pkt.cards);
                                    break;
                                case DISPROVEN:
                                    System.out.println("Suggestion was disproved by " + pkt.curr_player);
                                    break;
                                case NOT_DISPROVEN:
                                     System.out.println(pkt.curr_player + " could not disprove the suggestion");
                                     break;
                                default:
                                    System.out.println("Cant decode disprove broadcast packet");
                                    break;
                            }
                            break;
                        case PLAYER_OUT:
                            System.out.println("Player " + pkt.curr_player + " is out of the game.");
                            break;
                        default:
                            System.out.println("Cant decode broadcast packet");
                            break;
                    }
                    break;
                case TURN:
                    System.out.println("Server: Its your turn. Pick one of [M]ove [S]uggest [A]ccuse");
                    // prompt for turn
                    String line = null;
                    if (input.hasNextLine()) {
                        line = input.nextLine();
                    }

                    if (line.toLowerCase().equals("m")) {
                        System.out.println("Enter a room to move to: " + pkt.valid_rooms);
                        String line2 = null;
                        if (input.hasNextLine()) {
                            line2 = input.nextLine();
                        }
                        // TODO check that line is a valid room:

                        AllRoom room = AllRoom.valueOf(line2);
                        SocketPacket o = new SocketPacket();
                        o.curr_player = pkt.curr_player;
                        o.destination = room;
                        o.turn_type = SocketPacket.TurnType.MOVE;
                        o.packet_type = SocketPacket.PacketType.TURN;
                        out.writeObject(o);
                    }
                    break;
                case DISPROVE:
                    switch(pkt.disprove_type){
                        case REQUEST:
                            System.out.println("Disprove with " + pkt.cards);
                            boolean append = true;
                            ArrayList<String> dis = new ArrayList<String>();
                            String disprove_with;
                            while(append){
                                System.out.println("Current send list is  " + dis + ". Add a card? No to quit.");
                                if (input.hasNextLine()) {
                                    disprove_with = input.nextLine();
                                    if(pkt.cards.contains(disprove_with)){
                                        dis.add(disprove_with);
                                    }
                                                                        
                                    if(disprove_with.toLowerCase().equals("no") || dis.size() == pkt.MAX_CARDS_TO_SEND){
                                        SocketPacket o = new SocketPacket();
                                        o.curr_player = pkt.curr_player;
                                        o.cards = dis;
                                        out.writeObject(o);
                                    }
                                    else{
                                        System.out.println(disprove_with + " is not in your current hand.");
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    System.out.println("Cant decode packet");
                    break;
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        return true;

    }

    /**
     * Constructor.
     */
    Client() {
    }

    /**
     * start(): starts up client for clueless and sends player input to server
     * 
     * @throws IOException
     */
    public void start() throws IOException {

        InetAddress ip = InetAddress.getByName("localhost");

        Socket cs = new Socket(ip, SERVER_PORT);

        System.out.println("Client started ");

        ObjectOutputStream cout = new ObjectOutputStream(cs.getOutputStream()); // Note: cout must be initialized before
                                                                                // cin
        ObjectInputStream cin = new ObjectInputStream(cs.getInputStream());

        Scanner input = new Scanner(System.in);
        SocketPacket p;
        Boolean run = true;
        String line = null;
        
        try {
            // initial respponse from sever
            //p = (SocketPacket) cin.readObject();
            //run = handlePacketRcv(p, input, cout);
            while (run) {           
                Object o = cin.readObject();
                // // wait until server asks for something or tell us to update UI:
                if(o != null){
                    p = (SocketPacket) o;
                    run = handlePacketRcv(p, input, cout);
                }
            }

        } catch (ClassNotFoundException  | IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        
        

        System.out.println("Closing connection");
        input.close();
        cs.close();
        cin.close();
        cout.close();
    }
}
