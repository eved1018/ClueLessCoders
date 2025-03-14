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

class TextPacket extends Packet {
    public String text; 
    public boolean respond;

    public TextPacket(String t, Boolean resp) {
        this.text = t;
        this.respond = resp;
    }
    public TextPacket(String t) {
        this.text = t;
        this.respond = false;
    }
}

class WaitingOnResponse extends Packet {
    public String text; 

    public WaitingOnResponse(String t) {
        this.text = t;
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
        GAME_STATE, NEW_PLAYER, TURN_MADE, PLAYER_OUT, DISPROVE_MESSAGE 
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
    public ArrayList<AllRoom> player_locations; //TODO: change type to match board
    public GameState game_state_update;
    public Weapon murder_weapon;

    public ArrayList<AllRoom> valid_rooms; //TODO use an enum for all rooms
    public Boolean can_suggest;
    public Boolean can_accuse;

}


// *Response or *Request is a server -> client 
// responses should update ui, requests need a follow up from client
// player* is client -> server ie the follow up from a request 
// *Broadcast is server -> all clients

// new player has join game
class NewPlayerbroadcast extends Packet {
    PlayerName name;

    NewPlayerbroadcast(PlayerName n) {
        this.name = n;
    }

}

class GameStartBroadcast extends Packet {
    ArrayList<String> hand;
    Room starting_room; 
    int player_turn;

    public GameStartBroadcast(ArrayList<String> h, Room r, int t){
        this.hand = h;
        this.starting_room = r;
        this.player_turn = t;
    }

}

class TurnRequest extends Packet {
    Boolean can_suggest;
    ArrayList<Room> valid_moves;

    TurnRequest(Boolean cs, ArrayList<Room> vm){
        this.can_suggest = cs;
        this.valid_moves = vm;
    }


}

class PlayerMove extends Packet {
    Room destination;

    public PlayerMove(Room dest) {
        this.destination = dest;
    }
}

class PlayerSuggestion extends Packet {
    PlayerName suspect;
    Weapon murder_weapon;
    boolean is_accusation;
    boolean was_moved_by_suggest;
    // room is currRoom

    public PlayerSuggestion(PlayerName name, Room room, PlayerName suspect, Weapon weapon, boolean is_accusation) {
        this.suspect = suspect;
        this.murder_weapon = weapon;
        this.is_accusation = is_accusation;
    }
}

class BroadcastMove extends Packet {
    PlayerName name;
    Room new_room;

    public BroadcastMove(PlayerName n , Room r){
        this.name = n;
        this.new_room = r;
    }
}

class BroadcastPlayerOut extends Packet {
    PlayerName player;
    public BroadcastPlayerOut(PlayerName n){
        this.player = n;
    }

}


// Update other players when a player (and maybe a suggested player) moves during a turn
class SuggestBroadcast extends Packet {
    PlayerName suspect;
    Weapon weapon;
    Room room;
    PlayerName suggester;
    public SuggestBroadcast(PlayerName suspect, Weapon weapon, Room room, PlayerName suggester){
        this.suspect = suspect;
        this.weapon = weapon;
        this.room = room;
        this.suggester = suggester;
    }
}

class SuggestionResponse extends Packet {
    PlayerName disprover;
    String disproved_with;

    public SuggestionResponse(PlayerName n , String d ){
        this.disprover = n;
        this.disproved_with = d;
    }
}

// Ask player for to disprove card
class DisproveRequest extends Packet {
    ArrayList<String> options; // options to disprove

    DisproveRequest(ArrayList<String> s) {
        this.options = s;
    }

}


// Respond to original suggester
class DisproveResponse extends Packet {
    String disprove_with;

    public DisproveResponse(String d){
        this.disprove_with = d;
    }
    
}

// Let other players know it was disproved and by who (obvs not what card it was)
class DisproveBroadcast extends Packet {
    PlayerName disprover;
    PlayerName suggester;

    public DisproveBroadcast(PlayerName d, PlayerName s){
        this.disprover = d;
        this.suggester = s;
    }

}

// Player X cant disprove 
class DisproveSkip extends Packet {
    PlayerName player; // options to disprove

    DisproveSkip(PlayerName n) {
        this.player = n;
    }

}

// Game over
class BroadcastGameOver extends Packet {
    PlayerName winner;
    public BroadcastGameOver(PlayerName n){
        this.winner = n;
    }

}