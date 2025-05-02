package cluelesscoders.clueless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DeckHelperTest {
    private DeckHelper deckHelper;

    @BeforeEach
    void setUp() {
        deckHelper = new DeckHelper();
    }

    @Test
    void testSecretCardsSetup() {
        deckHelper.setSecretCards();
        assertNotNull(deckHelper.getSecretWeapon(), "Secret weapon should not be null.");
        assertNotNull(deckHelper.getSecretPerson(), "Secret person should not be null.");
        assertNotNull(deckHelper.getSecretRoom(), "Secret room should not be null.");
    }

    @Test
    void testCardDistribution() {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            players.add(new Player(null, null, null, i));
        }

        HashMap<PlayerName, ArrayList<Card>> hands = deckHelper.distributeCards(players);

        assertEquals(3, hands.size(), "Each player should receive a hand.");
        int totalCards = hands.values().stream().mapToInt(ArrayList::size).sum();
        assertTrue(totalCards > 0, "Cards should be distributed among players.");
    }
}
