package cluelesscoders.clueless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private ArrayList<Player> players;

    @BeforeEach
    void setUp() {
        game = new Game();
        players = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            players.add(new Player(null, null, null, i));
        }
        game.start_game(players);
    }

    @Test
    void testGameInitialization() {
        assertNotNull(game.player_list, "Player list should not be null after game initialization.");
        assertEquals(3, game.player_list.size(), "Player list size should match the number of players.");
    }

    @Test
    void testAccusationCorrect() {
        DeckHelper deckHelper = new DeckHelper();
        deckHelper.setSecretCards();
        Weapon secretWeapon = deckHelper.getSecretWeapon().getWeapon();
        PlayerName secretPerson = deckHelper.getSecretPerson().getPlayer();
        Room secretRoom = deckHelper.getSecretRoom().getRoom();

        assertTrue(game.check_accusation(secretWeapon, secretPerson, secretRoom),
                "Accusation should be correct when matching secret cards.");
    }

    @Test
    void testAccusationIncorrect() {
        assertFalse(game.check_accusation(Weapon.Rope, PlayerName.Miss_Scarlet, Room.Kitchen),
                "Accusation should be incorrect when not matching secret cards.");
    }

    @Test
    void testPlayerMovement() {
        Player player = players.get(0);
        AllRoom initialRoom = player.currRoom;
        AllRoom newRoom = game.board.getPossibleMoves(initialRoom).get(0);

        game.board.updatePlayerLocation(player.name, newRoom);
        player.currRoom = newRoom;

        assertEquals(newRoom, player.currRoom, "Player should move to the new room.");
    }
}
