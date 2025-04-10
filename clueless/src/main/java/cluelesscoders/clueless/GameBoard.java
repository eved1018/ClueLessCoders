package cluelesscoders.clueless;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class GameBoard {
    // Map to track the current location of each player
    private HashMap<PlayerName, AllRoom> playerLocations;

    // Map to define the connections between rooms and hallways
    private HashMap<AllRoom, ArrayList<AllRoom>> roomConnections;

    public GameBoard() {
        playerLocations = new HashMap<>();
        roomConnections = new HashMap<>();
        initializeBoard();
    }

    // Initialize the board with room connections
    private void initializeBoard() {
        // Define connections for each room and hallway
        roomConnections.put(AllRoom.Study, new ArrayList<>(List.of(AllRoom.Study2Hall, AllRoom.Study2Library, AllRoom.Kitchen)));
        roomConnections.put(AllRoom.Hall, new ArrayList<>(List.of(AllRoom.Study2Hall, AllRoom.Hall2Lounge, AllRoom.Hall2BillardRoom)));
        roomConnections.put(AllRoom.Lounge, new ArrayList<>(List.of(AllRoom.Hall2Lounge, AllRoom.Lounge2DiningRoom, AllRoom.Conservatory)));
        roomConnections.put(AllRoom.Library, new ArrayList<>(List.of(AllRoom.Study2Library, AllRoom.Library2Billardroom, AllRoom.Library2Conservatory)));
        roomConnections.put(AllRoom.Billiard_Room, new ArrayList<>(List.of(AllRoom.Hall2BillardRoom, AllRoom.Library2Billardroom, AllRoom.BilliardRoom2Diningroom, AllRoom.BilliardRoom2Ballroom)));
        roomConnections.put(AllRoom.Dining_Room, new ArrayList<>(List.of(AllRoom.Lounge2DiningRoom, AllRoom.BilliardRoom2Diningroom, AllRoom.DiningRoom2Kitchen)));
        roomConnections.put(AllRoom.Conservatory, new ArrayList<>(List.of(AllRoom.Library2Conservatory, AllRoom.Conservatory2Ballroom, AllRoom.Lounge)));
        roomConnections.put(AllRoom.Ballroom, new ArrayList<>(List.of(AllRoom.Conservatory2Ballroom, AllRoom.BilliardRoom2Ballroom, AllRoom.Ballroom2Kitchen)));
        roomConnections.put(AllRoom.Kitchen, new ArrayList<>(List.of(AllRoom.DiningRoom2Kitchen, AllRoom.Ballroom2Kitchen, AllRoom.Study)));

        // Add connections for hallways
        roomConnections.put(AllRoom.Study2Hall, new ArrayList<>(List.of(AllRoom.Study, AllRoom.Hall)));
        roomConnections.put(AllRoom.Hall2Lounge, new ArrayList<>(List.of(AllRoom.Hall, AllRoom.Lounge)));
        roomConnections.put(AllRoom.Study2Library, new ArrayList<>(List.of(AllRoom.Study, AllRoom.Library)));
        roomConnections.put(AllRoom.Hall2BillardRoom, new ArrayList<>(List.of(AllRoom.Hall, AllRoom.Billiard_Room)));
        roomConnections.put(AllRoom.Lounge2DiningRoom, new ArrayList<>(List.of(AllRoom.Lounge, AllRoom.Dining_Room)));
        roomConnections.put(AllRoom.Library2Billardroom, new ArrayList<>(List.of(AllRoom.Library, AllRoom.Billiard_Room)));
        roomConnections.put(AllRoom.BilliardRoom2Diningroom, new ArrayList<>(List.of(AllRoom.Billiard_Room, AllRoom.Dining_Room)));
        roomConnections.put(AllRoom.Library2Conservatory, new ArrayList<>(List.of(AllRoom.Library, AllRoom.Conservatory)));
        roomConnections.put(AllRoom.BilliardRoom2Ballroom, new ArrayList<>(List.of(AllRoom.Billiard_Room, AllRoom.Ballroom)));
        roomConnections.put(AllRoom.DiningRoom2Kitchen, new ArrayList<>(List.of(AllRoom.Dining_Room, AllRoom.Kitchen)));
        roomConnections.put(AllRoom.Conservatory2Ballroom, new ArrayList<>(List.of(AllRoom.Conservatory, AllRoom.Ballroom)));
        roomConnections.put(AllRoom.Ballroom2Kitchen, new ArrayList<>(List.of(AllRoom.Ballroom, AllRoom.Kitchen)));
        

    }

    // Update the location of a player
    public void updatePlayerLocation(PlayerName player, AllRoom newLocation) {
        playerLocations.put(player, newLocation);
    }

    // Get the current location of a player
    public AllRoom getPlayerLocation(PlayerName player) {
        return playerLocations.get(player);
    }


    // Get possible moves from a given room, excluding occupied rooms
    public ArrayList<AllRoom> getPossibleMoves(AllRoom currentRoom) {
        // Get all connected rooms
        ArrayList<AllRoom> possibleMoves = roomConnections.getOrDefault(currentRoom, new ArrayList<>());

        // Get a list of occupied Hallways
        ArrayList<AllRoom> occupiedHallways = playerLocations.values().stream()
            .filter(AllRoom::isHallway)
            .collect(Collectors.toCollection(ArrayList::new));
      
        // Remove occupied rooms from the list of possible moves
        possibleMoves.removeAll(occupiedHallways);

        return possibleMoves;
    }
   
}