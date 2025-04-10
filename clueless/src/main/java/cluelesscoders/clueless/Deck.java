package cluelesscoders.clueless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    public final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }
}