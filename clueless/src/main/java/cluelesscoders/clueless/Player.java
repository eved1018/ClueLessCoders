package cluelesscoders.clueless;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class Player {
    ArrayList<String> hand;
    AllRoom currRoom;
    PlayerName name;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    Boolean is_out; // made false accusation
    Boolean moved_by_suggest;

    int player_number;

    Player(Socket player_socket, ObjectInputStream in, ObjectOutputStream out, int n) {
        this.socket = player_socket;
        this.player_number = n;
        this.in = in;
        this.out = out;
        this.hand = null;
        this.name = PlayerName.values()[n];
        this.currRoom = null;
        this.is_out = false;
        this.moved_by_suggest = false;
    }

    // Just for demo
    public SocketPacket sendTextRequest(String m) {
        SocketPacket t = new SocketPacket();
        t.message = m;
        t.curr_player = this.name;
        t.packet_type = SocketPacket.PacketType.MESSAGE;
        t.expect_response = true;

        try {
            out.writeObject(t);
            SocketPacket rcv = (SocketPacket) in.readObject();
            return rcv;
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return null;
    }
    // Just for demo
    public void broadcastMessage(String m, Player sender) {
        SocketPacket t = new SocketPacket();
        t.message = m;
        t.curr_player = sender.name;
        t.packet_type = SocketPacket.PacketType.MESSAGE;
        t.expect_response = false;

        try {
            out.writeObject(t);
        } catch (IOException e) {
            System.out.println(e);
        } 
    }

    public SocketPacket playerTurnRequest(ArrayList<AllRoom> valid_rooms, Boolean can_suggest, Boolean can_accuse){
        SocketPacket p = new SocketPacket();
        p.curr_player = this.name;
        p.packet_type = SocketPacket.PacketType.TURN;
        p.turn_type = SocketPacket.TurnType.TURN_REQUEST;
        p.valid_rooms = valid_rooms;
        p.can_accuse = can_accuse;
        p.can_suggest = can_suggest;
        p.expect_response = true;

        
        try {
            out.writeObject(p);
            SocketPacket rcv = (SocketPacket) in.readObject();
            return rcv;
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return null;
    }


//     public void sendNewPlayer(PlayerName n) {
//         NewPlayerbroadcast npr = new NewPlayerbroadcast(n);
//         try {
//             out.writeObject(npr);

//         } catch (IOException e) {
//             System.out.println(e);
//         }
//     }

//     public void sendGameStart(ArrayList<String> h, Room r, int t) {
//         GameStartBroadcast npr = new GameStartBroadcast(h, r, t);
//         try {
//             out.writeObject(npr);

//         } catch (IOException e) {
//             System.out.println(e);
//         }

//     }

//     // note return Packet should be PlayerMove or PlayerSuggestion
//     public Packet playerTurnRequest(Boolean can_suggest, ArrayList<Room> moves) {
//         try {
//             TurnRequest tp = new TurnRequest(can_suggest, moves);
//             out.writeObject(tp);
//             Packet rcv = (Packet) in.readObject();
//             return rcv;

//         } catch (IOException e) {
//             System.out.println(e);
//             return null;
//         } catch (ClassNotFoundException e) {
//             System.out.println(e);
//             return null;
//         }

//     }

//     public void sendBroadcastMove(PlayerName name, Room room) {
//         try {
//             BroadcastMove tp = new BroadcastMove(name, room);
//             out.writeObject(tp);

//         } catch (IOException e) {
//             System.out.println(e);
//         }
//     }

//     public DisproveResponse sendDisproveRequest(ArrayList<String> options) {
//         try {
//             DisproveRequest dr = new DisproveRequest(options);
//             out.writeObject(dr);
//             DisproveResponse rcv = (DisproveResponse) in.readObject();
//             return rcv;

//         } catch (IOException e) {
//             System.out.println(e);
//             return null;
//         } catch (ClassNotFoundException e) {
//             System.out.println(e);
//             return null;
//         }
//     }

//     public void sendSuggestResponse(PlayerName name2, String disprove_with) {
//         // response to original suggester
//         try {
//             SuggestionResponse sr = new SuggestionResponse(name2, disprove_with);
//             out.writeObject(sr);

//         } catch (IOException e) {
//             System.out.println(e);
//         }

//     }

//     public void sendPlayerOut(PlayerName name) {
//         // player n has won
//         try {
//             BroadcastPlayerOut b = new BroadcastPlayerOut(name);
//             out.writeObject(b);

//         } catch (IOException e) {
//             System.out.println(e);
//         }

//     }

//     public void sendDisproveSkip(PlayerName name) {
//         try {
//             DisproveSkip b = new DisproveSkip(name);
//             out.writeObject(b);

//         } catch (IOException e) {
//             System.out.println(e);
//         }
//     }

//     public void sendSuggestion(PlayerName suspect, Weapon weapon, Room room, PlayerName suggester) {

//         try {
//             SuggestBroadcast sb = new SuggestBroadcast(suspect, weapon, room, suggester);
//             out.writeObject(sb);

//         } catch (IOException e) {
//             System.out.println(e);
//         }

//     }

//     public void sendGameOver(PlayerName name) {
//         // player n has won
//         try {
//             BroadcastGameOver b = new BroadcastGameOver(name);
//             out.writeObject(b);

//         } catch (IOException e) {
//             System.out.println(e);
//         }

//     }

//     public void sendDisproveBroadcast(PlayerName disprover, PlayerName suggester) {
//         // player n has won
//         try {
//             DisproveBroadcast b = new DisproveBroadcast(disprover, suggester);
//             out.writeObject(b);

//         } catch (IOException e) {
//             System.out.println(e);
//         }
//     }

}