/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cluelesscoders.clueless;
import java.io.Serializable;
import java.util.ArrayList;
import cluelesscoders.clueless.Clueless.*;


class Packet implements Serializable  {
    // What do we need here??
    public Packet() {
       
    }

}


class SocketPacket extends Packet {
    
    public final int MAX_CARDS_TO_SEND = 3;
    
    enum PacketType {
        LOBBY, TURN, BROADCAST, DISPROVE,  MESSAGE,
    }
    enum GameState {
        START, END
    }
    
    enum TurnType {
        TURN_REQUEST, MOVE, SUGGEST, ACCUSE, END
    }
    
    enum DisproveType {
        REQUEST, DISPROVEN_WITH, NOT_DISPROVEN, DISPROVEN
    }
    enum BroadcastType {
        GAME_STATE, NEW_PLAYER, PLAYER_OUT, DISPROVE, TURN_MADE
    }
    
    public SocketPacket(){
        this.curr_player = null;
        this.other_player = null;
        this.expect_response = false;
        this.packet_type = null;
        this.turn_type= null;
        this.disprove_type = null;
        this.broadcast_type = null;
        this.message = new String();
        this.turn_number = 0;
        this.cards = new ArrayList<Card>();
        this.player_locations = new ArrayList<AllRoom>();
        this.crime_scene = null;
        this.game_state_update = null;
        this.murder_weapon = null;
        this.valid_rooms = null;
        this.destination = null;
        this.can_accuse = false;
        this.can_suggest = false;    
    }

    
    public PlayerName curr_player; // player making the turn
    public PlayerName other_player; // suspect for accuse or 
    
    public Boolean expect_response ;
    public PacketType packet_type;
    public TurnType turn_type;
    public DisproveType disprove_type;
    public BroadcastType broadcast_type;

    public String message;

    public int turn_number;
    public ArrayList<Card> cards;  // TODO use an enum for cards (person/weapon)
    public ArrayList<AllRoom> player_locations;
    public Room crime_scene;
    public GameState game_state_update;
    public Weapon murder_weapon;

    public ArrayList<AllRoom> valid_rooms; //TODO use an enum for all rooms
    public AllRoom destination;
    public Boolean can_suggest;
    public Boolean can_accuse;

}
