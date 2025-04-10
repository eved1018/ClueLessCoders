package cluelesscoders.clueless;

import java.util.ArrayList;
import java.util.HashMap;

public class DeckHelper {
    private final Deck deck;
    private final Deck weapons;
    private final Deck players;
    private final Deck rooms;

    private Card secretWeapon;
    private Card secretPerson;
    private Card secretRoom;

    public DeckHelper() {
        this.deck = new Deck();
        this.weapons = new Deck();
        this.players = new Deck();
        this.rooms = new Deck();
        initializeDeck();
    }

    private void initializeDeck() {
        // Add weapon cards
        for (Weapon weapon : Weapon.values()) {
            weapons.addCard(new Card(weapon.toString(), Card.CardType.WEAPON));
        }
        
        // Add player cards
        for (PlayerName player : PlayerName.values()) {
            players.addCard(new Card(player.toString(), Card.CardType.PLAYER));
        }
        
        // Add room cards
        for (Room room : Room.values()) {
            rooms.addCard(new Card(room.toString(), Card.CardType.ROOM));
        }

        weapons.shuffle();
        players.shuffle();
        rooms.shuffle();
        
        secretWeapon = weapons.drawCard();
        secretPerson = players.drawCard();
        secretRoom = rooms.drawCard();

        // Add weapon cards
        for (Card wCard : weapons.cards) {
            deck.addCard(wCard);
        }
        
        // Add player cards
        for (Card pCard: players.cards) {
            deck.addCard(pCard);
        }
        
        // Add room cards
        for (Card rCard : rooms.cards) {
            deck.addCard(rCard);
        }
        
        deck.shuffle();
    }

    public void setSecretCards() {
        
    }

    public HashMap<PlayerName, ArrayList<Card>> distributeCards(ArrayList<Player> players) {
        HashMap<PlayerName, ArrayList<Card>> hands = new HashMap<>();
        while (!deck.isEmpty()) {
            for (Player player : players) {
                if (deck.isEmpty()) break;
                hands.computeIfAbsent(player.name, k -> new ArrayList<>()).add(deck.drawCard());
            }
        }
        return hands;
    }

    public Card getSecretWeapon() {
        return secretWeapon;
    }

    public Card getSecretPerson() {
        return secretPerson;
    }

    public Card getSecretRoom() {
        return secretRoom;
    }
}