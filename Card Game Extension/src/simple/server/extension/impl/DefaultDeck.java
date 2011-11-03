package simple.server.extension.impl;

import java.util.*;
import simple.server.extension.ICard;
import simple.server.extension.ICardType;
import simple.server.extension.IDeck;
import simple.server.extension.RPDeck;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class DefaultDeck implements IDeck {

    protected String name;
    protected Random rand = new Random();

    public DefaultDeck(RPDeck rpdeck) {
        this.name = rpdeck.getName();
    }

    public DefaultDeck(String name) {
        this.name = name;
    }

    @Override
    public List<ICard> getCards() {
        return deck;
    }

    @Override
    public List<ICard> getUsedCards() {
        return used;
    }

    @Override
    public ICard ditch(Class<? extends ICardType> type) {
        for (int i = 0; i < deck.size(); i++) {
            if (deck.get(i).getLookup().lookup(type) != null) {
                ICard ditched = deck.remove(i);
                used.add(ditched);
                return ditched;
            }
        }
        return null;
    }

    @Override
    public List<ICard> ditch(Class<? extends ICardType> type, boolean random, int amount) {
        ArrayList<ICard> ditched = new ArrayList<>();
        if (random) {
            ArrayList<Integer> indices = new ArrayList<>();
            //Build a list of indices of this type
            for (int i = 0; i < deck.size(); i++) {
                if (deck.get(i).getLookup().lookup(type) != null) {
                    indices.add(i);
                }
            }
            Collections.sort(indices);
            //Now randomly pick one from the list to be removed
            int remove = indices.size() < 0 ? 0
                    : (indices.size() < amount ? indices.size() : amount);
            int count = 0;
            for (int j = 0; j < remove; j++) {
                for (final Iterator i = deck.iterator(); i.hasNext();) {
                    int toRemove = indices.remove(rand.nextInt(indices.size()));
                    if (count == toRemove) {
                        i.remove();
                    }
                    count++;
                }
            }
        } else {
            for (int j = 0; j < amount; j++) {
                for (final Iterator i = deck.iterator(); i.hasNext();) {
                    ICard next = (ICard) i.next();
                    if (next.getLookup().lookup(type) != null) {
                        ditched.add(next);
                        break;
                    }
                }
            }
        }
        //Move them to the used pile

        used.addAll(ditched);

        return ditched;
    }

    @Override
    public ICard ditchBottom() {
        ICard ditched = deck.remove(deck.size() - 1);
        used.add(ditched);
        return ditched;
    }

    @Override
    public List<ICard> ditch(int amount, boolean random) {
        ArrayList<ICard> ditched = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ditched.add(ditch(random));
        }
        return ditched;
    }

    @Override
    public List<ICard> ditch(int amount) {
        ArrayList<ICard> ditched = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ditched.add(ditch());
        }
        return ditched;
    }

    @Override
    public ICard ditch(boolean random) {
        ICard ditched = deck.remove(random ? rand.nextInt(deck.size()) : 0);
        used.add(ditched);
        return ditched;
    }

    @Override
    public ICard ditch() {
        return ditch(false);
    }

    @Override
    public ICard draw(Class<? extends ICardType> type) {
        for (int i = 0; i < deck.size(); i++) {
            if (type.isInstance(deck.get(i))) {
                return deck.remove(i);
            }
        }
        return null;
    }

    @Override
    public ICard draw() {
        return draw(false);
    }

    @Override
    public ICard draw(boolean random) {
        ICard drawn = null;
        if (random) {
            drawn = deck.remove(rand.nextInt(deck.size()));
        } else {
            drawn = deck.remove(0);
        }
        hand.add(drawn);
        return drawn;
    }

    @Override
    public ICard drawBottom() {
        return deck.remove(deck.size() - 1);
    }

    @Override
    public List<ICard> draw(int amount, boolean random) {
        ArrayList<ICard> drawn = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            drawn.add(draw(random));
        }
        return drawn;
    }

    @Override
    public List<ICard> draw(int amount) {
        ArrayList<ICard> drawn = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            drawn.add(draw());
        }
        return drawn;
    }

    @Override
    public void shuffle() {
        java.util.Collections.shuffle(deck);
    }

    @Override
    public int getSize() {
        return deck.size();
    }

    @Override
    public int getUsedPileSize() {
        return used.size();
    }

    @Override
    public String getName() {
        return name;
    }
}
