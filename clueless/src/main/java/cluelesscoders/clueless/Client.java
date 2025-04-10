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
                    switch (pkt.broadcast_type) {
                        case NEW_PLAYER:
                            System.out.println("Print new player " + pkt.curr_player);
                            System.out.println("Waiting for more players");
                            break;
                        case GAME_STATE:
                            switch (pkt.game_state_update) {
                                case START:
                                    System.out.println("Game Starting");
                                    System.out.println("Your starting position is: " + pkt.destination);
                                    System.out.println("Your hand contains the following cards:");
                                    for (Card card : pkt.cards) {
                                        System.out.println("- " + card.getName());
                                    }
                                    break;
                                case END:
                                    System.out.println(pkt.curr_player + " has won the game");
                                    break;
                                default:
                                    System.out.println("Cant decode gamee state broadcast packet");
                            }
                            break;
                        case TURN_MADE:
                            switch (pkt.turn_type) {
                                case MOVE:
                                    System.out.println("Player " + pkt.curr_player + " moved to " + pkt.destination);
                                    break;
                                case SUGGEST:
                                    System.out.println("Player " + pkt.curr_player + " Suggests the murder was done by "
                                            + pkt.other_player
                                            + " in the " + pkt.crime_scene + " with the " + pkt.murder_weapon);
                                    break;
                                default:
                                    System.out.println("Cant decode player turn broadcast packet");
                                    break;
                            }
                            break;
                        case DISPROVE:
                            switch (pkt.disprove_type) {
                                case DISPROVEN_WITH:
                                    System.out.println(
                                            "Suggestion was disproved by " + pkt.curr_player + " with " + pkt.cards);
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

                    String prompt =  "Server: Its your turn. Pick one of ";
                    if (pkt.valid_rooms.size() > 0) {
                        prompt += "[M]ove ";
                    }
                    if (pkt.can_suggest){
                        prompt += "[S]uggest ";
                    }
                    if (pkt.can_accuse) {

                        prompt += "[A]ccuse ";
                    }
                    prompt += "[E]nd Turn";
                        
                    System.out.println(prompt);
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
                    } else if (line.toLowerCase().equals("s")) {
                        // Handle suggestion
                        System.out.println("Available players: " + getPlayerNames());
                        System.out.println("Available weapons: " + getWeaponNames());

                        System.out.println("Enter the suspect (player name): ");
                        String suspect = null;
                        if (input.hasNextLine()) {
                            suspect = input.nextLine();
                        }

                        System.out.println("Enter the weapon: ");
                        String weapon = null;
                        if (input.hasNextLine()) {
                            weapon = input.nextLine();
                        }


                        SocketPacket suggestPacket = new SocketPacket();
                        suggestPacket.curr_player = pkt.curr_player;
                        suggestPacket.other_player = PlayerName.valueOf(suspect);
                        suggestPacket.murder_weapon = Weapon.valueOf(weapon);
                        suggestPacket.crime_scene = Room.valueOf(pkt.destination.toString());
                        suggestPacket.turn_type = SocketPacket.TurnType.SUGGEST;
                        suggestPacket.packet_type = SocketPacket.PacketType.TURN;
                        out.writeObject(suggestPacket);
                    } else if (line.toLowerCase().equals("a")) {
                        // Handle accusation
                        System.out.println("Available players: " + getPlayerNames());
                        System.out.println("Available weapons: " + getWeaponNames());
                        System.out.println("Available rooms: " + getRoomNames());
                        System.out.println("Enter the suspect (player name): ");
                        String suspect = null;
                        if (input.hasNextLine()) {
                            suspect = input.nextLine();
                        }

                        System.out.println("Enter the weapon: ");
                        String weapon = null;
                        if (input.hasNextLine()) {
                            weapon = input.nextLine();
                        }

                        System.out.println("Enter the room: ");
                        String room = null;
                        if (input.hasNextLine()) {
                            room = input.nextLine();
                        }

                        SocketPacket accusePacket = new SocketPacket();
                        accusePacket.curr_player = pkt.curr_player;
                        accusePacket.other_player = PlayerName.valueOf(suspect);
                        accusePacket.murder_weapon = Weapon.valueOf(weapon);
                        accusePacket.crime_scene = Room.valueOf(room);
                        accusePacket.turn_type = SocketPacket.TurnType.ACCUSE;
                        accusePacket.packet_type = SocketPacket.PacketType.TURN;
                        out.writeObject(accusePacket);
                    } else if (line.toLowerCase().equals("e")) {
                        SocketPacket endTurnPacket = new SocketPacket();
                        endTurnPacket.packet_type = PacketType.TURN;
                        endTurnPacket.turn_type = SocketPacket.TurnType.END;
                        System.out.println("Ending your turn...");
                        out.writeObject(endTurnPacket);
                    } else {
                        System.out.println("Invalid input. Please try again.");
                        return handlePacketRcv(pkt, input, out);
                       
                    }
                    break;
                case DISPROVE:
                    switch (pkt.disprove_type) {
                        case REQUEST:
                            System.out.println("Disprove with " + pkt.cards);
                            String linebuffer;
                            Card disprove_with = new Card();
                            System.out.println("Which card would you like to disprove with? ");
                            if (input.hasNextLine()) {
                                linebuffer = input.nextLine();
                                disprove_with = new Card(linebuffer);
                                SocketPacket disprovePacket  = new SocketPacket();
                                disprovePacket.cards.add(disprove_with);
                                out.writeObject(disprovePacket);
                            } else {
                                System.out.println(disprove_with + " is not in your current hand.");
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
