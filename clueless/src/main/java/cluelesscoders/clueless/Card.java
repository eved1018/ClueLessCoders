package cluelesscoders.clueless;
import java.io.Serializable;

public class Card implements Serializable {
    public enum CardType {
        WEAPON, PLAYER, ROOM
    }

    private final String name;
    private final CardType type;

    public Card(String name, CardType type) {
        this.name = name;
        this.type = type;
    }

    public Card(String name) {
        this.name = name;
        this.type = determineCardType(name);
    }

    public Card() {
        this.name = null;
        this.type = null;
    }


    private CardType determineCardType(String name) {
        switch (name.toLowerCase()) {
            case "knife":
            case "candlestick":
            case "revolver":
            case "rope":
            case "leadpipe":
            case "wrench":
            case "dagger":

                return CardType.WEAPON;
            case "miss_scarlet":
            case "colonel_mustard":
            case "mrs_white":
            case "reverend_green":
            case "mrs_peacock":
            case "professor_plum":
                return CardType.PLAYER;
            case "kitchen":
            case "ballroom":
            case "conservatory":
            case "dining room":
            case "billiard room":
            case "library":
            case "lounge":
            case "hall":
            case "study":
                return CardType.ROOM;
            default:
                throw new IllegalArgumentException("Unknown card name: " + name);
        }
    }


    public String getName() {
        return name;
    }

    public CardType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}