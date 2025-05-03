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
import cluelesscoders.clueless.SocketPacket.PacketType;
import cluelesscoders.clueless.Card;

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
    private CCUI clientUI;

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
            switch (pkt.packet_type) {
                case MESSAGE:

                    clientUI.printlnUI(pkt.message);
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
                    switch (pkt.broadcast_type) {
                        case NEW_PLAYER:
                            clientUI.printlnUI("New player joined: " + pkt.curr_player);
                            clientUI.printlnUI("Waiting for more players");
                            
                            break;
                        case GAME_STATE:
                            switch (pkt.game_state_update) {
                                case START:
                                    clientUI.printlnUI("Game Starting");
                                    clientUI.setCharacter(pkt.curr_player.name());
                                    for(String p : pkt.player_list){
                                        clientUI.addPlayer(p);
                                    }
                                    clientUI.printlnUI("Your starting position is: " + pkt.destination);
                                    clientUI.updatePosition(pkt.destination.name());
                                    clientUI.printlnUI("Your hand contains the following cards:");
                                    for (Card card : pkt.cards) {
                                        clientUI.printlnUI("- " + card.getName());
                                    }
                                    clientUI.addCards(pkt.cards);
                                    break;
                                case TURN:
                                    clientUI.printlnUI("It's " + pkt.curr_player + "'s turn.");
                                    clientUI.updateTurn(pkt.curr_player.name());
                                    clientUI.updateBoard(pkt.player_locations);
                                    break;
                                case END:
                                    clientUI.printlnUI(pkt.curr_player + " has won the game");
                                    break;
                                default:
                                    clientUI.printlnUI("Cant decode game state broadcast packet");
                            }
                            break;
                        case TURN_MADE:
                            switch (pkt.turn_type) {
                                case MOVE:
                                    clientUI.printlnUI("Player " + pkt.curr_player + " moved to " + pkt.destination);
                                    break;
                                case SUGGEST:
                                    clientUI.printlnUI("Player " + pkt.curr_player + " Suggests the murder was done by "
                                            + pkt.other_player
                                            + " in the " + pkt.crime_scene + " with the " + pkt.murder_weapon);
                                    break;
                                case ACCUSE:
                                    clientUI.printlnUI("Player " + pkt.curr_player + " accuses "
                                            + pkt.other_player
                                            + " of the murder in the " + pkt.crime_scene + " using the " + pkt.murder_weapon);
                                    break;
                                default:
                                    clientUI.printlnUI("Cant decode player turn broadcast packet");
                                    break;
                            }
                            break;
                        case DISPROVE:
                            switch (pkt.disprove_type) {
                                case DISPROVEN_WITH:
                                    clientUI.printlnUI(
                                            "Suggestion was disproved by " + pkt.curr_player + " with " + pkt.cards);
                                    break;
                                case DISPROVEN:
                                    clientUI.printlnUI("Suggestion was disproved by " + pkt.curr_player);
                                    break;
                                case NOT_DISPROVEN:
                                    clientUI.printlnUI(pkt.curr_player + " could not disprove the suggestion");
                                    break;
                                default:
                                    clientUI.printlnUI("Cant decode disprove broadcast packet");
                                    break;
                            }
                            break;
                        case PLAYER_OUT:
                            clientUI.printlnUI("Player " + pkt.curr_player + " made an incorrect accusation and is out of the game.");
                            break;
                        default:
                            clientUI.printlnUI("Cant decode broadcast packet");
                            break;
                    }
                    break;
                case TURN:

                    clientUI.updateBoard(pkt.player_locations);
                    String prompt =  "Server: Its your turn. Pick one of ";
                    
                    
                    if (!pkt.valid_rooms.isEmpty()) {
                        prompt += "[M]ove ";
                        clientUI.toggleButton(CCUI.BUTTON_OPTION.MOVE, true);
                    }
                    if (pkt.can_suggest){
                        prompt += "[S]uggest ";
                        clientUI.toggleButton(CCUI.BUTTON_OPTION.SUGGEST, true);
                    }
                    if (pkt.can_accuse) {

                        prompt += "[A]ccuse ";
                        clientUI.toggleButton(CCUI.BUTTON_OPTION.ACCUSE, true);
                    }
                    prompt += "[E]nd Turn";
                    clientUI.toggleButton(CCUI.BUTTON_OPTION.END_TURN, true);
                        
                    clientUI.printlnUI(prompt);
                    // prompt for turn
                    String line = clientUI.GetButtonInput();


                    if (line.toLowerCase().equals("m")) {
                        // TODO check that line is a valid room:
                        AllRoom room = clientUI.movePopup(pkt.valid_rooms);
                        clientUI.updatePosition(room.name());
                        
                        SocketPacket o = new SocketPacket();
                        o.curr_player = pkt.curr_player;
                        o.destination = room;
                        o.turn_type = SocketPacket.TurnType.MOVE;
                        o.packet_type = SocketPacket.PacketType.TURN;
                        out.writeObject(o);
                        
                        clientUI.disableAllButtons();
                        
                    } else if (line.toLowerCase().equals("s")) {
                        // Handle suggestion
                        ArrayList<String> sPckg = clientUI.SuggestionPopup();

                        SocketPacket suggestPacket = new SocketPacket();
                        suggestPacket.curr_player = pkt.curr_player;
                        suggestPacket.other_player = PlayerName.valueOf(sPckg.get(CCUI.S_INDEX.SUSPECT.getValue()));
                        suggestPacket.murder_weapon = Weapon.valueOf(sPckg.get(CCUI.S_INDEX.WEAPON.getValue()));
                        suggestPacket.crime_scene = Room.valueOf(sPckg.get(CCUI.S_INDEX.LOCATION.getValue()));
                        suggestPacket.turn_type = SocketPacket.TurnType.SUGGEST;
                        suggestPacket.packet_type = SocketPacket.PacketType.TURN;
                        out.writeObject(suggestPacket);
                        
                        clientUI.disableAllButtons();
                        
                    } else if (line.toLowerCase().equals("a")) {
                        // Handle accusation
                        ArrayList<String> aPckg = clientUI.AccusationPopup();

                        SocketPacket accusePacket = new SocketPacket();
                        accusePacket.curr_player = pkt.curr_player;
                        accusePacket.other_player = PlayerName.valueOf(aPckg.get(CCUI.S_INDEX.SUSPECT.getValue()));
                        accusePacket.murder_weapon = Weapon.valueOf(aPckg.get(CCUI.S_INDEX.WEAPON.getValue()));
                        accusePacket.crime_scene = Room.valueOf(aPckg.get(CCUI.S_INDEX.LOCATION.getValue()));
                        accusePacket.turn_type = SocketPacket.TurnType.ACCUSE;
                        accusePacket.packet_type = SocketPacket.PacketType.TURN;
                        out.writeObject(accusePacket);
                        
                        clientUI.disableAllButtons();
                        
                    } else if (line.toLowerCase().equals("e")) {
                        
                        SocketPacket endTurnPacket = new SocketPacket();
                        endTurnPacket.packet_type = PacketType.TURN;
                        endTurnPacket.turn_type = SocketPacket.TurnType.END;
                        out.writeObject(endTurnPacket);
                        
                        clientUI.printlnUI("Ending your turn...");  
                        clientUI.disableAllButtons();
                    } else {
                        clientUI.printlnUI("Invalid input. Please try again.");
                        return handlePacketRcv(pkt, input, out);
                       
                    }
                    break;
                case DISPROVE:
                    switch (pkt.disprove_type) {
                        case REQUEST:
                            String linebuffer;
                            linebuffer = clientUI.DisprovePopup(pkt.cards);
                            
                            Card disprove_with = new Card(linebuffer);
                            SocketPacket disprovePacket  = new SocketPacket();
                            disprovePacket.cards.add(disprove_with);
                            out.writeObject(disprovePacket);
                            
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    clientUI.printlnUI("Cant decode packet");
                    break;
            }
        } catch (IOException e) {
            clientUI.printlnUI(e.getMessage());
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
        
        clientUI = new CCUI();
        clientUI.show();

        ObjectOutputStream cout = new ObjectOutputStream(cs.getOutputStream()); // Note: cout must be initialized before
                                                                                // cin
        ObjectInputStream cin = new ObjectInputStream(cs.getInputStream());

        Scanner input = new Scanner(System.in);
        SocketPacket p;
        Boolean run = true;
        String line = null;

        try {
            // initial respponse from sever
            p = (SocketPacket) cin.readObject();
            run = handlePacketRcv(p, input, cout);
            while (run) {
                Object o = cin.readObject();
                // // wait until server asks for something or tell us to update UI:
                if (o != null) {
                    p = (SocketPacket) o;
                    run = handlePacketRcv(p, input, cout);
                }
            }

        } catch (ClassNotFoundException | IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }

        System.out.println("Closing connection");
        input.close();
        cs.close();
        cin.close();
        cout.close();
    }

    // Helper method to get player names
    private String getPlayerNames() {
        StringBuilder sb = new StringBuilder();
        for (PlayerName player : PlayerName.values()) {
            sb.append(player.toString()).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // Remove trailing comma and space
    }

    // Helper method to get weapon names
    private String getWeaponNames() {
        StringBuilder sb = new StringBuilder();
        for (Weapon weapon : Weapon.values()) {
            sb.append(weapon.toString()).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // Remove trailing comma and space
    }

    // Helper method to get room names
    private String getRoomNames() {
        StringBuilder sb = new StringBuilder();
        for (Room room : Room.values()) {
            sb.append(room.toString()).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // Remove trailing comma and space
    }
}
