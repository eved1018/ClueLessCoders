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

    public void sendPacket(SocketPacket packet){
        try {
            out.writeObject(packet);
        } catch (IOException e) {
            System.out.println(e);
        } 
    }

    public void sendGameStart(ArrayList<String>player_hand, AllRoom start_room, int player_number){
        SocketPacket p = new SocketPacket();
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.broadcast_type = SocketPacket.BroadcastType.NEW_PLAYER;
        p.player_locations.add(start_room.toString());
        p.turn_number = player_number;
        try {
            out.writeObject(p);
        } catch (IOException e) {
            System.out.println(e);
        } 

    }

   public  SocketPacket sendDisproveRequest(ArrayList<String> options) {
        SocketPacket p = new SocketPacket();
        p.curr_player = name;
        p.cards = options;
        p.packet_type = SocketPacket.PacketType.DISPROVE;
        p.disprove_type = SocketPacket.DisproveType.REQUEST;
        try {
            out.writeObject(p);
            SocketPacket rcv = ( SocketPacket) in.readObject();
            return rcv;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }
    }
    
    public void sendDisproveBroadcast(PlayerName disprover, PlayerName suggester, ArrayList<String> things) {
        
        SocketPacket p = new SocketPacket();
        p.curr_player = disprover;
        p.other_player = suggester;
        p.cards = things;
        if(p.other_player == this.name){
            p.disprove_type = SocketPacket.DisproveType.DISPROVEN_WITH;
        }
        else{
            p.disprove_type = SocketPacket.DisproveType.DISPROVEN;
        }
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.broadcast_type = SocketPacket.BroadcastType.DISPROVE;
        try {
            out.writeObject(p);

        } catch (IOException e) {
            System.out.println(e);
        }
    }

}