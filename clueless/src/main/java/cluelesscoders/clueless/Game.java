package cluelesscoders.clueless;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

enum Room {
    Study, Hall,Lounge, Library, Billiard_Room, Dining_Room, Conservatory, Ballroom, Kitchen
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
    ArrayList<PlayerName> active_players;
    ArrayList<PlayerName> board;
    public Weapon secret_weapon;
    public PlayerName secret_person;
    public Room secret_room;
    public HashMap<PlayerName, ArrayList<String>> hands = new HashMap<PlayerName, ArrayList<String>>();

    Game(){
        this.board = new ArrayList<PlayerName>();
    }

    public void start_game(ArrayList<PlayerName> active_players){
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
            for (PlayerName p : active_players ){
                if (hands.containsKey(p)){
                    hands.get(p).add(cards.removeFirst());

                } else {
                    ArrayList<String> l = new ArrayList<String>(6);
                    l.add(cards.removeFirst());
                    hands.put(p, l);
                }
            }
        }
    }


    public ArrayList<String> get_hand(PlayerName n) {
        return hands.get(n);
    }

    public void update_board(PlayerName name, Room loc) {
        // place player name in location loc
    }


    // possible moves for player
    public ArrayList<Room> possible_moves(PlayerName playername){
        // TODO impl
        ArrayList<Room> r = new ArrayList<Room>();
        r.add(Room.Hall);
        r.add(Room.Kitchen);
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


    
}
