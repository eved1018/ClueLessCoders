package cluelesscoders.clueless;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;



public class Player {
    ArrayList<String>  hand;
    Room currRoom;
    PlayerName name;
    Socket socket;
    ObjectInputStream  in;
    ObjectOutputStream  out;
    Boolean is_out; // made false accusation
    Boolean moved_by_suggest;

    
    int player_number;

    Player(Socket player_socket, ObjectInputStream in, ObjectOutputStream out, int n){
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
    public void sendMessage(String m) {

        TextPacket t = new TextPacket(m);
        try {
            out.writeObject(t);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    // This is just for demoing 
    public Packet sendTextRequest(String m)  {
        TextPacket t = new TextPacket(m, true);
        try {
            out.writeObject(t);
            Packet rcv = (Packet) in.readObject();
            return rcv;

        } catch (IOException e) {
            System.out.println(e);
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }

    }

    public void sendNewPlayer(PlayerName n){
        NewPlayerbroadcast npr = new NewPlayerbroadcast(n);
        try {
            out.writeObject(npr);

        } catch (IOException e) {
            System.out.println(e);
        } 
    }


    public void sendGameStart(ArrayList<String> h, Room r, int t){
        GameStartBroadcast npr = new GameStartBroadcast(h, r, t);
        try {
            out.writeObject(npr);

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    // note return Packet should be PlayerMove or PlayerSuggestion
    public Packet playerTurnRequest(Boolean can_suggest, ArrayList<Room> moves) {
        try {
            TurnRequest tp = new TurnRequest(can_suggest, moves);
            out.writeObject(tp);
            Packet rcv = (Packet) in.readObject();
            return rcv;

        } catch (IOException e) {
            System.out.println(e);
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            return null;
        } 

    }

    public void sendBroadcastMove(PlayerName name, Room room) {
        try {
            BroadcastMove tp = new BroadcastMove(name, room);
            out.writeObject(tp);

        } catch (IOException e) {
            System.out.println(e);
        } 
    }




    public DisproveResponse sendDisproveRequest(ArrayList<String> options) {
        try {
            DisproveRequest dr = new DisproveRequest(options);
            out.writeObject(dr);
            DisproveResponse rcv = (DisproveResponse) in.readObject();
            return rcv;

        } catch (IOException e) {
            System.out.println(e);
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            return null;
        } 
    }

    public void sendSuggestResponse(PlayerName name2, String disprove_with) {
        // response to original suggester
        try {
            SuggestionResponse sr = new SuggestionResponse(name2, disprove_with);
            out.writeObject(sr);

        } catch (IOException e) {
            System.out.println(e);
        } 

    }

    public void sendPlayerOut(PlayerName name) {
        // player n has won
        try {
            BroadcastPlayerOut b = new BroadcastPlayerOut(name);
            out.writeObject(b);

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public void sendDisproveSkip(PlayerName name) {
        try {
            DisproveSkip b = new DisproveSkip(name);
            out.writeObject(b);

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void sendSuggestion(PlayerName suspect, Weapon weapon, Room room, PlayerName suggester) {
        
        try {
            SuggestBroadcast sb = new SuggestBroadcast(suspect, weapon, room, suggester);
            out.writeObject(sb);

        } catch (IOException e) {
            System.out.println(e);
        } 

    }

    
    public void sendGameOver(PlayerName name) {
        // player n has won
        try {
            BroadcastGameOver b = new BroadcastGameOver(name);
            out.writeObject(b);

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public void sendDisproveBroadcast(PlayerName disprover, PlayerName suggester) {
         // player n has won
         try {
            DisproveBroadcast b = new DisproveBroadcast(disprover, suggester);
            out.writeObject(b);

        } catch (IOException e) {
            System.out.println(e);
        }
    }

}