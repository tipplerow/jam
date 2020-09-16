
package jam.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.vector.JamVector;

/**
 * Represents a set of mutually exclusive enumerated events with the
 * probability of each event specified with a fixed value.
 */
public final class EventSet<E extends Enum<E>> {
    private final List<E> eventList;
    private final Map<E, Probability> eventProb;
    private final JamVector cumulProb;

    /**
     * Creates a new mutually exclusive event set.
     *
     * @param eventProb the probability of occurrence for each event.
     *
     * @throws IllegalArgumentException unless the probabilities are
     * normalized.
     */
    public EventSet(Map<E, Probability> eventProb) {
        validate(eventProb.values());

        this.eventList = new ArrayList<E>(eventProb.keySet());
        this.eventProb = new EnumMap<E, Probability>(eventProb);
        this.cumulProb = StatUtil.cumsum(mapProb());

        assert eventList.size() == eventProb.size();
        assert eventList.size() == cumulProb.length();
    }

    private JamVector mapProb() {
        JamVector probVector = new JamVector(eventList.size());

        for (int index = 0; index < eventList.size(); index++)
            probVector.set(index, eventProb.get(eventList.get(index)).doubleValue());

        return probVector;
    }

    /**
     * Creates a new mutually exclusive event set.
     *
     * @param <E> the enumerated event class.
     *
     * @param eventProb the probability of occurrence for each event.
     *
     * @return the new event set.
     *
     * @throws IllegalArgumentException unless the probabilities are
     * normalized.
     */
    public static <E extends Enum<E>> EventSet<E> create(Map<E, Probability> eventProb) {
        return new EventSet<E>(eventProb);
    }

    /**
     * Returns the number of mutually exclusive events in this set.
     *
     * @return the number of mutually exclusive events in this set.
     */
    public int getEventCount() {
        return eventList.size();
    }

    /**
     * Returns the probability of occurrence for an event in this set.
     *
     * @param event the enumerated event of interest.
     *
     * @return the probability of occurrence for an event in this set,
     * or {@code null} if the event is not contained in this set.
     */
    public Probability getEventProbability(E event) {
        return eventProb.get(event);
    }

    /**
     * Returns the enumerated events contained in this set.
     *
     * @return the enumerated events contained in this set.
     */
    public Set<E> getEvents() {
        return EnumSet.copyOf(eventList);
    }

    /**
     * Selects one event at random.
     *
     * <p>The {@code JamRandom.global()} instance is used as the
     * random number source.
     *
     * @return the index of the selected event, corresponding to the
     * order in which the event probabilities where passed to the
     * constructor.
     */
    public E select() {
        return select(JamRandom.global());
    }

    /**
     * Selects one event at random.
     *
     * @param random the random number source.
     *
     * @return the index of the selected event, corresponding to the
     * order in which the event probabilities where passed to the
     * constructor.
     */
    public E select(JamRandom random) {
        int    eventIndex = 0;
        double randomDraw = random.nextDouble();

        // This must always be true with normalized event
        // probabilities and a valid random number source...
        assert randomDraw <= cumulProb.last();

        while (randomDraw > cumulProb.get(eventIndex))
            eventIndex++;

        return eventList.get(eventIndex);
    }

    /**
     * Validates a collection of mutually exclusive events.
     *
     * @param events the event probabilities to validate.
     *
     * @throws IllegalArgumentException unless all probabilities are
     * valid and normalized.
     */
    public static void validate(Probability... events) {
        validate(Arrays.asList(events));
    }

    /**
     * Validates a collection of mutually exclusive events.
     *
     * @param events the event probabilities to validate.
     *
     * @throws IllegalArgumentException unless all probabilities are
     * valid and normalized.
     */
    public static void validate(Collection<Probability> events) {
        if (!Probability.isNormalized(events))
            throw new IllegalArgumentException("Non-normalized event collection.");
    }
}
