package cluelesscoders.clueless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.logging.Logger;

enum Room {
    Study, Hall, Lounge, Library, Billiard_Room, Dining_Room, Conservatory, Ballroom, Kitchen
}

enum AllRoom {

    Study, Study2Hall, Hall, Hall2Lounge, Lounge,
    Study2Library, Hall2BillardRoom, Lounge2DiningRoom,
    Library, Library2Billardroom, Billiard_Room, BilliardRoom2Diningroom, Dining_Room,
    Library2Conservatory, BilliardRoom2Ballroom, DiningRoom2Kitchen,
    Conservatory, Conservatory2Ballroom, Ballroom, Ballroom2Kitchen, Kitchen;

    boolean isRoom() {
        // Check if the current enum value is a valid room
        for (Room room : Room.values()) {
            if (this.name().equals(room.name())) {
                return true;
            }
        }
        return false;
    }

    boolean isHallway() {
        // Check if the current enum value is a hallway (not a valid room)
        return !isRoom();
    }
}

enum PlayerName {
    Miss_Scarlet, Colonel_Mustard, Mrs_White, Reverend_Green, Mrs_Peacock, Professor_Plum
}

enum Weapon {
    Candlestick, Dagger, Revolver, LeadPipe, Wrench, Rope
}

public class Game {
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    ArrayList<Player> player_list;
    private final GameBoard board;
    private DeckHelper deckHelper;
    private HashMap<PlayerName, ArrayList<Card>> hands;

    public Game() {
        this.board = new GameBoard();
        this.deckHelper = new DeckHelper();
        LOGGER.info("Game initialized.");
        LOGGER.log(Level.INFO, "\nSecret Person: {0}\nSecret Weapon: {1}\nSecret Room: {2}", 
                new Object[]{this.deckHelper.getSecretPerson().toString(), this.deckHelper.getSecretWeapon().toString(), this.deckHelper.getSecretRoom().toString()});
    }

    public void start_game(ArrayList<Player> player_list) {
        this.player_list = player_list;
        LOGGER.log(Level.INFO, "Starting game with players: {0}", player_list);
        
                
        // Distribute cards to players
        this.hands = deckHelper.distributeCards(player_list);
        LOGGER.info("Cards distributed to players.");
        
        // Initialize starting positions for each player
        for (Player p : player_list) {
            AllRoom startingRoom = this.get_starting_room(p.name);
            p.currRoom = startingRoom;
            this.board.updatePlayerLocation(p.name, startingRoom);
            LOGGER.log(Level.INFO, "Player {0} placed in starting room: {1}", new Object[]{p.name, startingRoom});
        }
        
        int npc_num = player_list.size();
        while(npc_num < PlayerName.values().length){
            PlayerName name = PlayerName.values()[npc_num];
            this.board.updatePlayerLocation(name,this.get_starting_room(name));
            npc_num++;
        }

        broadcastGameStart();
    }

    public boolean check_accusation(Weapon w, PlayerName n, Room r) {
        boolean result = w.toString().equals(deckHelper.getSecretWeapon().getName()) &&
                n.toString().equals(deckHelper.getSecretPerson().getName()) &&
                r.toString().equals(deckHelper.getSecretRoom().getName());
        LOGGER.log(Level.INFO, "Accusation checked: Weapon={0}, Player={1}, Room={2} -> Result: {3}", new Object[]{w, n, r, result});
        return result;
    }

    public AllRoom get_starting_room(PlayerName name) {
        // Assign starting rooms based on the player's name
        switch (name) {
            case Miss_Scarlet:
                return AllRoom.Hall2Lounge;
            case Colonel_Mustard:
                return AllRoom.Lounge2DiningRoom;
            case Mrs_White:
                return AllRoom.Ballroom2Kitchen;
            case Reverend_Green:
                return AllRoom.Conservatory2Ballroom;
            case Mrs_Peacock:
                return AllRoom.Library2Conservatory;
            case Professor_Plum:
                return AllRoom.Study2Library;
            default:
                LOGGER.log(Level.WARNING, "No starting room found for player: {0}", name);
                return null; // Default starting room
        }
    }

    public void broadcastGameStart() {
        LOGGER.info("Broadcasting game start.");
        int i = 0;
        
        ArrayList<String> player_names = new ArrayList<String>();
        for (Player pl : this.player_list) {
            if(!pl.is_out){
                player_names.add(pl.name.toString());
            }
        }
        
        for (Player p : this.player_list) {
            AllRoom start_room = this.get_starting_room(p.name);
            ArrayList<Card> player_hand = (ArrayList<Card>) this.hands.get(p.name);
            p.sendGameStart(player_hand, start_room, i, player_names);
            i += 1;
            LOGGER.log(Level.INFO, "Player {0} notified of game start. Starting room: {1}", new Object[]{p.name, start_room});
        }
    }

    public void suggestionLoop(SocketPacket sgt, Player p) {
        LOGGER.log(Level.INFO, "Suggestion made by player {0}: Suspect={1}, Weapon={2}, Room={3}", new Object[]{p.name, sgt.other_player, sgt.murder_weapon, p.currRoom});

        // move suggested player to room

        this.board.updatePlayerLocation(sgt.other_player, p.currRoom);
        for (Player i : this.player_list) {
            if (i.name.equals(sgt.other_player)) {
                i.currRoom = p.currRoom;
                i.moved_by_suggest = true;
            }
        }

        // can prob combine these
        suggestBroadcast(sgt.other_player, sgt.murder_weapon, p.currRoom, p.name); // Notify all players of suggestion
        broadcastMove(sgt.other_player, p.currRoom); // this should handle updating the suspects position on the UI
        ArrayList<Card> disprove_with;
        int size = this.player_list.size();
        int start = p.player_number;
        Player op;
        // Iterate through other players starting from the current player's position
        for (int i = 1; i < size; i++) { // Start from 1 to skip the current player
            int index = (start + i) % size; // Wrap around the list
            if (index == p.player_number) {
            continue; // Skip the current player
            }
            op = this.player_list.get(index);
            LOGGER.info(op.toString());
            ArrayList<Card> oph = this.hands.get(op.name);
            disprove_with = oph.stream()
                    .filter(card -> card.getName().equals(sgt.other_player.toString()) ||
                            card.getName().equals(sgt.murder_weapon.toString()) ||
                            card.getName().equals(p.currRoom.toString()))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (disprove_with.isEmpty()) {

                broadcastDisproveSkip(op.name, p.name); // player x cant disprove
                LOGGER.log(Level.INFO, "Player {0} could not disprove the suggestion.", op.name);
            } else {

                Card disprove_with_repsonse = op.sendDisproveRequest(disprove_with);
                broadcastDisprove(op.name, p.name, disprove_with_repsonse);
                LOGGER.log(Level.INFO, "Player {0} disproved the suggestion with: {1}", new Object[]{op.name, disprove_with_repsonse});
                
            }
        }
        return;
    }

    public boolean game_loop() {

        for (Player p : this.player_list) {

            if (p.is_out) {
                // TODO not sure what the rules are for for out player
                LOGGER.log(Level.INFO, "Skipping player {0} (out of the game).", p.name);
                continue;
            }
            boolean is_player_turn = true;
            boolean can_move = true;
            int turnsTaken = 0;
            while (is_player_turn) {
                // check if the player can move ie are the hallways blocked
                ArrayList<AllRoom> moves = new ArrayList<AllRoom>();
                if (can_move) {
                    moves = this.board.getPossibleMoves(p.currRoom);
                }

                boolean can_suggest = true;
                boolean can_accuse = true;

                // Check if the player can suggest (must be in a room and not moved by
                // suggestion)
                if (p.currRoom.isHallway()) {
                    can_suggest = false;
                }
                // If all exits are blocked (i.e., there are characters in all of the hallways)
                // and you are not in one
                // of the corner rooms (with a secret passage), and you weren’t moved to the
                // room by another
                // player making a suggestion, you can not make a suggestion (but you can make
                // an
                // accusation).
                if (moves.isEmpty() && p.moved_by_suggest && turnsTaken == 0) {
                    // player can only accuse
                    can_suggest = false;
                }
                
                if(onePlayerLeft(p)){
                    LOGGER.log(Level.INFO, "Player {0} is the only player left and has won the game.", p.name);
                            broadcastGameOver(p.name);
                            return false;
                }

                // request a turn from this player
                turnBroadcast(p.name);
                ArrayList<AllRoom> player_locations = new ArrayList<AllRoom>();
                for(PlayerName n : PlayerName.values()){
                    player_locations.add(this.board.getPlayerLocation(n));
                }
                SocketPacket rpkt = p.playerTurnRequest(moves, can_suggest, can_accuse, p.currRoom, player_locations);
                
                LOGGER.log(Level.INFO, "Player {0} turn: {1}", new Object[]{p.name, rpkt.turn_type});
                
                

                switch (rpkt.turn_type) {
                    case MOVE:
                        // update board
                        AllRoom destination = rpkt.destination;
                        p.currRoom = destination;
                        this.board.updatePlayerLocation(p.name, destination);
                        broadcastMove(p.name, destination); // Update all players UI
                        LOGGER.log(Level.INFO, "Player {0} moved to: {1}", new Object[]{p.name, destination});
                        can_move = false;
                        break;
                    case SUGGEST:
                        suggestionLoop(rpkt, p);
                        is_player_turn = false;
                        break;
                    case ACCUSE:
                        is_player_turn = false;
                        accuseBroadcast(rpkt.other_player, rpkt.murder_weapon, rpkt.crime_scene, p.name);
                        if (this.check_accusation(rpkt.murder_weapon, rpkt.other_player, rpkt.crime_scene)) {
                            // game over
                            LOGGER.log(Level.INFO, "Player {0} made a correct accusation and won the game.", p.name);
                            broadcastGameOver(p.name);
                            return false;
                        } else {
                            // Incorrect accusation: mark player as out
                            p.is_out = true;
                            broadcastPlayerOut(p.name);
                            LOGGER.log(Level.INFO, "Player {0} made an incorrect accusation and is out of the game.", p.name);

                            // Check if the player is blocking a door and move them into the room
                            if (!isARoom(p.currRoom)) {
                                AllRoom roomToEnter = getConnectedRoom(p.currRoom);
                                if (roomToEnter != null) {
                                    p.currRoom = roomToEnter;
                                    this.board.updatePlayerLocation(p.name, roomToEnter);
                                    broadcastMove(p.name, roomToEnter); // Notify all players of the move
                                    System.out.println(p.name + " has been moved into " + roomToEnter
                                            + " after a false accusation.");
                                }
                            }
                        }
                        break;
                    case END:
                        LOGGER.log(Level.INFO, "Player {0} ended their turn.", p.name);
                        is_player_turn = false;
                        break;

                    default:
                        break;
                }
                // Increment turn count
                turnsTaken++;
                p.moved_by_suggest = false;
            }
        }

        return true;
    }
    
    
    public boolean onePlayerLeft(Player currPlayer){
        boolean onlyPlayer = true;
        for (Player p : this.player_list) {
            if(!p.name.equals(currPlayer.name) && !p.is_out){
                onlyPlayer = false;
            }
        }
        return onlyPlayer;
    }
    

    public boolean is_suggestable_room(String r) {
        // check if room is a room u can suggest in
        Room[] rooms = Room.values();
        for (Room i : rooms) {
            if (i.toString().equals(r)) {
                return true;
            }
        }
        return false;
    }

    public boolean isARoom(AllRoom room) {
        for (Room value : Room.values()) {
            if (value.equals(room)) {
                return true;
            }
        }
        return false;
    }

    Room toRoom(AllRoom room) {
        return Room.valueOf(room.toString());
    }

    public void broadcastNewPlayer(PlayerName n) {
        SocketPacket p = new SocketPacket();
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.broadcast_type = SocketPacket.BroadcastType.NEW_PLAYER;
        p.curr_player = n;

        for (Player player : this.player_list) {
            player.sendPacket(p);

        }
    }

    public AllRoom getConnectedRoom(AllRoom hallway) {
        // Get the connected rooms for the hallway
        ArrayList<AllRoom> connectedRooms = this.board.getPossibleMoves(hallway);

        // Return the first connected room (assuming hallways connect to exactly one or
        // two rooms)
        for (AllRoom room : connectedRooms) {
            if (isARoom(room)) {
                return room;
            }
        }
        return null; // No connected room found
    }

    public void broadcastDisproveSkip(PlayerName suggester, PlayerName disprover) {
        // player name cant disprove
        SocketPacket p = new SocketPacket();
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.broadcast_type = SocketPacket.BroadcastType.DISPROVE;
        p.disprove_type = SocketPacket.DisproveType.NOT_DISPROVEN;
        p.other_player = disprover;
        p.curr_player = suggester;

        for (Player player : this.player_list) {
            player.sendPacket(p);

        }

    }

    public void suggestBroadcast(PlayerName suspect, Weapon weapon, AllRoom room, PlayerName suggester) {
        SocketPacket p = new SocketPacket();
        p.broadcast_type = SocketPacket.BroadcastType.TURN_MADE;
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.turn_type = SocketPacket.TurnType.SUGGEST;
        p.crime_scene = Room.valueOf(room.toString());
        p.other_player = suspect;
        p.murder_weapon = weapon;
        p.curr_player = suggester;
        // suggester made suggestion
        for (Player player : this.player_list) {
            player.sendPacket(p);
        }
    }
    
     public void accuseBroadcast(PlayerName suspect, Weapon weapon, Room room, PlayerName accuser) {
        SocketPacket p = new SocketPacket();
        p.broadcast_type = SocketPacket.BroadcastType.TURN_MADE;
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.turn_type = SocketPacket.TurnType.ACCUSE;
        p.crime_scene = room;
        p.other_player = suspect;
        p.murder_weapon = weapon;
        p.curr_player = accuser;
        // player made accusation 
        for (Player player : this.player_list) {
            player.sendPacket(p);
        }
    }
    
    public void turnBroadcast(PlayerName name){
        SocketPacket p = new SocketPacket();
        p.broadcast_type = SocketPacket.BroadcastType.GAME_STATE;
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.game_state_update = SocketPacket.GameState.TURN;
        p.curr_player = name;
        ArrayList<AllRoom> player_locations = new ArrayList<AllRoom>();
        for(PlayerName n : PlayerName.values()){
            player_locations.add(this.board.getPlayerLocation(n));
        }
        p.player_locations = player_locations;
        for (Player player : this.player_list) {
            player.sendPacket(p);
        }
    }

    public void broadcastMove(PlayerName name, AllRoom room) {
        // player has moved to room by player
        SocketPacket p = new SocketPacket();
        p.broadcast_type = SocketPacket.BroadcastType.TURN_MADE;
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.turn_type = SocketPacket.TurnType.MOVE;
        p.curr_player = name;
        p.destination = room; // make this a dict or something
        for (Player player : this.player_list) {
            player.sendPacket(p);
        }
    }

    public void broadcastDisprove(PlayerName disprover, PlayerName suggester, Card things) {
        for (Player player : this.player_list) {
            player.sendDisproveBroadcast(disprover, suggester, things);

        }
    }

    // game over player has won
    public void broadcastGameOver(PlayerName name) {
        SocketPacket p = new SocketPacket();
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.broadcast_type = SocketPacket.BroadcastType.GAME_STATE;
        p.game_state_update = SocketPacket.GameState.END;
        p.curr_player = name;

        for (Player player : this.player_list) {
            player.sendPacket(p);
        }

    }

    public void broadcastPlayerOut(PlayerName name) {
        // player has made false accusation
        SocketPacket p = new SocketPacket();
        p.packet_type = SocketPacket.PacketType.BROADCAST;
        p.broadcast_type = SocketPacket.BroadcastType.PLAYER_OUT;
        p.curr_player = name;

        for (Player player : this.player_list) {
            player.sendPacket(p);
        }
    }

}