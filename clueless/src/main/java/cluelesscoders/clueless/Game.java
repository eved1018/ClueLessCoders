package cluelesscoders.clueless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


// TODO broadast becomes loop + packet 
// reimpl all comm functs w new packet

enum Room {
    Study, Hall,Lounge, Library, Billiard_Room, Dining_Room, Conservatory, Ballroom, Kitchen
}

enum AllRoom {

    Study,          Study2Hall,             Hall,               Hall2Lounge,                Lounge,
    Study2Library,                          Hall2BillardRoom,                               Lounge2DiningRoom,
    Library,        Library2Billardroom,    Billiard_Room,      BilliardRoom2Diningroom,    Dining_Room,
    Library2Conservatory,                   BilliardRoom2Ballroom,                          DiningRoom2Kitchen,
    Conservatory,   Conservatory2Ballroom,  Ballroom,           Ballroom2Kitchen,           Kitchen,         
}



enum PlayerName {
    Miss_Scarlet, Colonel_Mustard, Mrs_White, Reverend_Green, Mrs_Peacock, Professor_Plum
}

enum Weapon {
    Candlestick, Dagger, Revolver, LeadPipe, Wrench, Rope
}


// prob only need a hashmap for each players current loc 
// and then another map for neighbors 
public class Game {
    ArrayList<Player> player_list; // all players
    ArrayList<PlayerName> board;
    public Weapon secret_weapon;
    public PlayerName secret_person;
    public Room secret_room;
    public HashMap<PlayerName, ArrayList<String>> hands = new HashMap<PlayerName, ArrayList<String>>();

    Game(){
        this.board = new ArrayList<PlayerName>();
    }

    public void start_game(ArrayList<Player> player_list){
        this.player_list = player_list;
        ArrayList<Weapon> weapons = new ArrayList<Weapon>(Arrays.asList(Weapon.values()));
        ArrayList<PlayerName> player_names = new ArrayList<PlayerName>(Arrays.asList(PlayerName.values()));
        ArrayList<Room> rooms = new ArrayList<Room>(Arrays.asList(Room.values()));
        
        Collections.shuffle(weapons);
        Collections.shuffle(player_names);
        Collections.shuffle(rooms);

        // Why doesnt java have pop
        this.secret_weapon = weapons.remove(0);
        this.secret_person = player_names.remove(0);
        this.secret_room = rooms.remove(0);
        
        // Dont know enough java to do this properly.
        List<String> cards = new ArrayList<String>();

        for (Weapon i:  weapons){
            cards.add(i.toString());
        }

        for (PlayerName i:  player_names){
            cards.add(i.toString());
        }

        for (Room i:  rooms){
            cards.add(i.toString());
        }

        Collections.shuffle(cards);
        // Add cards to each player hands - hands size doesnt matter
        while (!cards.isEmpty()){
            for (Player p : player_list ){
                if (this.hands.containsKey(p.name)){
                    this.hands.get(p.name).add(cards.removeFirst());

                } else {
                    ArrayList<String> l = new ArrayList<String>(6);
                    l.add(cards.removeFirst());
                    this.hands.put(p.name, l);
                }
            }
        }
        broadcastGameStart();
    }
    public void suggestionLoop(SocketPacket sgt, Player p ) {

        // move suggested player to room

        this.update_board(sgt.other_player, p.currRoom);
        for (Player i: this.player_list) {
            if (i.name.equals(sgt.other_player)) {
                i.currRoom = p.currRoom;
                i.moved_by_suggest = true;
            }
        }
        
        // can prob combine these
        broadcastMove(sgt.other_player, p.currRoom); // this should handle updating the suspects position on the UI
        suggestBroadcast(sgt.other_player, sgt.murder_weapon, toRoom(p.currRoom), p.name); // Notify all players of suggestion (ie a ui thing)

        // ring buffer
        int size = this.player_list.size();
        int start = p.player_number;
        Player op;
        ArrayList<String> disprove_with;
        for (int i = 0; i < size; i++){
            int index = (start + 1 + i ) % size; // wrap list 
            op = this.player_list.get(index);
            ArrayList<String> oph = this.get_hand(op.name);
            ArrayList<String> suspecthand = new ArrayList<String>();
            suspecthand.add(sgt.other_player.toString());
            suspecthand.add(sgt.murder_weapon.toString());
            suspecthand.add(p.currRoom.toString());

            // set notation to get intersection 
            Set<String> overlap = oph.stream()
                .distinct()
                .filter(suspecthand::contains)
                .collect(Collectors.toSet());

            if (!overlap.isEmpty()){

                if (overlap.size() == 1) {
                    ArrayList<String> oneCard = new ArrayList<String>();
                    oneCard.add(overlap.stream().findFirst().orElse(null));
                    disprove_with = oneCard;
    
                } else {
                    SocketPacket dr = op.sendDisproveRequest(suspecthand);
                    disprove_with = dr.cards;
                }
                broadcastDisprove(op.name, p.name, disprove_with);
            }

            else {
                broadcastDisproveSkip(op.name); // player x cant disprove 
            } 
        }
    }

    public boolean game_loop(){

        for (Player p: this.player_list){

            if (p.is_out){
                //TODO not sure what the rules are for for out player
                return true;
            }
            
            // check if the player can move ie are the hallways blocked
            ArrayList<AllRoom> moves =  this.possible_moves(p.name);

            Boolean can_suggest = true;
            Boolean can_accuse = false;
            // check if player can suggest: player cant move + wasnt moved by suggestion
            if (moves.isEmpty() && !p.moved_by_suggest) {
                // player can only accuse 
                can_suggest = false;
            }
            
            if(isARoom(p.currRoom)){
                can_accuse = true;
            }

            // request a turn from this player 
            SocketPacket rpkt = p.playerTurnRequest(moves,can_suggest, can_accuse);

            if (null != rpkt.turn_type) switch (rpkt.turn_type) {
                case MOVE:
                    // update board
                    AllRoom destination = rpkt.destination;
                    p.currRoom = destination;
                    this.update_board(p.name, destination);
                    broadcastMove(p.name, destination); // Update all players UI
                    break;
                case SUGGEST:
                    suggestionLoop(rpkt, p);
                    break;
                case ACCUSE:
                    Weapon w = Weapon.valueOf(rpkt.cards.get(0));
                    if (this.check_accusation(w, rpkt.other_player, toRoom(p.currRoom))){
                        // game over
                        System.out.println("Game over " + p.name + " won");
                        broadcastGameOver(p.name);
                        return false;
                    } else {
                        p.is_out = true;
                        broadcastPlayerOut(p.name);
                    }   break;
                default:
                    break;
            }

        }
        return true;
    }


    public ArrayList<String> get_hand(PlayerName n) {
        return hands.get(n);
    }

    //TODO impl
    public void update_board(PlayerName name, AllRoom loc) {
        // place player name in location loc
    }

    //TODO impl
    // possible moves for player
    public ArrayList<AllRoom> possible_moves(PlayerName playername){
        ArrayList<AllRoom> r = new ArrayList<AllRoom>();
        r.add(AllRoom.Hall);
        r.add(AllRoom.Kitchen);
        return r;
    }

    public boolean check_accusation(Weapon w, PlayerName n, Room r) {
        if (w == this.secret_weapon && n == this.secret_person && r == this.secret_room){
            return true;
        } 
        return false;
    }

    //TODO impl
	public Room get_starting_room(PlayerName name) {
		return Room.Study;
	}
        
    public boolean isARoom(AllRoom room){
        for(Enum value : Room.values() ){
            if(value.equals(room)){
                return true;
            }
        }
        return false;
    }

    Room toRoom(AllRoom room){
        return Room.valueOf(room.toString());
    }

    public void broadcastNewPlayer(PlayerName n){
        for (Player p : this.player_list){
            p.sendNewPlayer(n);
        }
    }

    public void broadcastGameStart(){
        for (Player p : this.player_list){
            p.sendGameStart();
        }        

    }

    public void broadcastDisproveSkip(PlayerName n){
        // player name cant disprove
        for (Player p : this.player_list){
            p.sendDisproveSkip(n);
        }

    }

    public void suggestBroadcast(PlayerName suspect, Weapon weapon, Room room, PlayerName suggester){
        // suggester made suggestion 
        for (Player p : this.player_list){
            p.sendSuggestion(suspect, weapon, room, suggester);
        }
    }

    public void broadcastMove(PlayerName name, AllRoom room) {
        // player has moved to room by player 

        for (Player p : this.player_list){
            p.sendBroadcastMove(name, room);
        }
    }

    public void broadcastDisprove(PlayerName disprover, PlayerName suggester, ArrayList<String> things){
        for (Player p : this.player_list){
            p.sendDisproveBroadcast(disprover, suggester, things);
        }
    }

    public void broadcastGameOver(PlayerName name){
        for (Player p : this.player_list){
            p.sendGameOver(name);
        }

        // game over player has won
    }

    public void broadcastPlayerOut(PlayerName name){
        // player has made false accusation 
        for (Player p : this.player_list){
            p.sendPlayerOut(name);
        }
    }


    
}
