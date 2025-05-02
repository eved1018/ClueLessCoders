package cluelesscoders.clueless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    private ObjectOutputStream mockOut;

    @BeforeEach
    void setUp() {
        mockOut = mock(ObjectOutputStream.class);
        player = new Player(null, null, mockOut, 0);
    }

    @Test
    void testSendTextRequest() throws Exception {
        SocketPacket response = new SocketPacket();
        response.message = "Test response";

        ObjectInputStream mockIn = mock(ObjectInputStream.class);
        when(mockIn.readObject()).thenReturn(response);

        player.in = mockIn;
        SocketPacket result = player.sendTextRequest("Test message");

        assertNotNull(result, "Response should not be null.");
        assertEquals("Test response", result.message, "Response message should match.");
    }

    @Test
    void testBroadcastMessage() throws Exception {
        player.broadcastMessage("Test broadcast", player);

        verify(mockOut, times(1)).writeObject(any(SocketPacket.class));
    }

    @Test
    void testPlayerTurnRequest() throws Exception {
        ArrayList<AllRoom> validRooms = new ArrayList<>();
        validRooms.add(new AllRoom("Room1"));
        validRooms.add(new AllRoom("Room2"));

        SocketPacket response = new SocketPacket();
        response.message = "Turn response";

        ObjectInputStream mockIn = mock(ObjectInputStream.class);
        when(mockIn.readObject()).thenReturn(response);

        player.in = mockIn;
        SocketPacket result = player.playerTurnRequest(validRooms, true, false, new AllRoom("Room1"));

        assertNotNull(result, "Response should not be null.");
        assertEquals("Turn response", result.message, "Response message should match.");
    }

    @Test
    void testSendPacket() throws Exception {
        SocketPacket packet = new SocketPacket();
        packet.message = "Test packet";

        player.sendPacket(packet);

        verify(mockOut, times(1)).writeObject(packet);
    }

    @Test
    void testSendGameStart() throws Exception {
        ArrayList<Card> playerHand = new ArrayList<>();
        playerHand.add(new Card("Card1"));
        playerHand.add(new Card("Card2"));

        ArrayList<String> playerList = new ArrayList<>();
        playerList.add("Player1");
        playerList.add("Player2");

        player.sendGameStart(playerHand, new AllRoom("StartRoom"), 1, playerList);

        verify(mockOut, times(1)).writeObject(any(SocketPacket.class));
    }

    @Test
    void testSendDisproveRequest() throws Exception {
        ArrayList<Card> options = new ArrayList<>();
        options.add(new Card("Card1"));
        options.add(new Card("Card2"));

        SocketPacket response = new SocketPacket();
        response.cards = new ArrayList<>();
        response.cards.add(new Card("Card1"));

        ObjectInputStream mockIn = mock(ObjectInputStream.class);
        when(mockIn.readObject()).thenReturn(response);

        player.in = mockIn;
        Card result = player.sendDisproveRequest(options);

        assertNotNull(result, "Response card should not be null.");
        assertEquals("Card1", result.name, "Response card name should match.");
    }

    @Test
    void testSendDisproveBroadcast() throws Exception {
        PlayerName disprover = PlayerName.PLAYER1;
        PlayerName suggester = PlayerName.PLAYER2;
        Card card = new Card("Card1");

        player.sendDisproveBroadcast(disprover, suggester, card);

        verify(mockOut, times(1)).writeObject(any(SocketPacket.class));
    }
}
