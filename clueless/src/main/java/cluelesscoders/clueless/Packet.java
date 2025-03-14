/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cluelesscoders.clueless;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import cluelesscoders.clueless.Clueless.*;


class Packet implements Serializable  {



    // What do we need here??

    public Packet() {
       
    }

}


class SocketPacket extends Packet {
    enum PacketType {
        LOBBY, TURN, BROADCAST, DISPROVE,  MESSAGE,
    }
    enum GameState {
        START, END
    }
    
    enum TurnType {
        TURN_REQUEST, MOVE, SUGGEST, ACCUSE
    }
    
    enum DisproveType {
        DISPROVE_RESPONSE, DISPROVE_REQUEST, DISPROVED_WITH
    }
    enum BroadcastType {
        GAME_STATE, NEW_PLAYER, TURN_MADE, PLAYER_OUT, DISPROVE_MESSAGE , DISPROVE_SKIP
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
    public ArrayList<String> cards;  // TODO use an enum for cards (person/weapon)
    public ArrayList<String> player_locations; //TODO: change type to match board
    public Room crime_scene;
    public GameState game_state_update;
    public Weapon murder_weapon;

    public ArrayList<AllRoom> valid_rooms; //TODO use an enum for all rooms
    public Boolean can_suggest;
    public Boolean can_accuse;

    SocketPacket(){
        this.cards = new ArrayList<String>();
        this.player_locations = new ArrayList<String>();

    }

}


