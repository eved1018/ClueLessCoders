/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package cluelesscoders.clueless;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
/** To run, in command line use java Clueless c for client side
 *                              java Clueless s for server side
 *  Type ESC_SEQ in command line to terminate both programs
 * @see ESC_SEQ X
 * @author  Chris Dixon
 * @version 1.0
 */
public class Clueless {

    enum Room {
        ROOM1, ROOM2
        
    }

    enum PlayerName {
        player1, player2
    }

    enum Weapon {
        knife, wrench
    }

    enum Card {
        Room, Weapon, PlayerName
    }

    class Player {
        ArrayList<Card>  hand;
        PlayerName name;
    }


    class Turn implements Serializable {
        PlayerName currPlayer;
        Room currRoom;
        
        public Turn(PlayerName name, Room room){
            this.currPlayer = name;
            this.currRoom = room;
        }
    }

    class Move extends Turn {
        Room destination;

        public Move(PlayerName name, Room room, Room dest) {
            super(name, room);
            this.destination = dest;
        }
    }

    // suggestion and accusstion are the 
    class Suggestion extends Turn {
        PlayerName suspect;
        Weapon murder_weapon;
        boolean is_accusstion;
        boolean was_moved_by_suggest;
        // room is currRoom

        public Suggestion(PlayerName name, Room room, PlayerName suspect, Weapon weapon, boolean is_accusstion) {
            super(name, room);
            this.suspect = suspect;
            this.murder_weapon = weapon;
            this.is_accusstion = is_accusstion;
        }


    }

    public static void main(String[] args) throws IOException{
        if(args[0].equals("s")){ 
            Server s = new Server();
            s.start();
        }
        else if(args[0].equals("c")){
            Client c = new Client();
            c.start();
        } 
    }
}

