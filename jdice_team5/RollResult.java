import java.util.Vector;
import java.util.logging.Logger;

/**
 * RollResult class represents the result of rolling dice.
 * 
 * Refactored:
 * - Fixed class name and syntax issues.
 * - Moved rolls declaration to class level.
 * - Used StringBuilder in toString() for performance.
 * 
 * Added:
 * - Logging feature using java.util.logging to track added results.
 */
public class RollResult {
    private static final Logger logger = Logger.getLogger(RollResult.class.getName());

    private int total;
    private int modifier;
    private Vector<Integer> rolls;

    /**
     * Private constructor for internal use (e.g., combining results).
     *
     * @param total    Total value of all rolls.
     * @param modifier Modifier to be added to total.
     * @param rolls    List of individual dice rolls.
     */
    private RollResult(int total, int modifier, Vector<Integer> rolls) {
        this.total = total;
        this.modifier = modifier;
        this.rolls = rolls;
    }

    /**
     * Constructor that initializes RollResult with a bonus (modifier).
     *
     * @param bonus The bonus to start with.
     */
    public RollResult(int bonus) {
        this.total = bonus;
        this.modifier = bonus;
        this.rolls = new Vector<>();
    }

    /**
     * Adds a single dice roll to the current result.
     * Also logs the added value.
     *
     * @param res The value of the dice roll.
     */
    public void addResult(int res) {
        total += res;
        rolls.add(res);
        logger.info("Added roll: " + res + ", New total: " + total);
    }

    /**
     * Combines this result with another RollResult.
     *
     * @param r2 The other RollResult to combine with.
     * @return A new RollResult that is the combination of both.
     */
    public RollResult andThen(RollResult r2) {
        int newTotal = this.total + r2.total;
        Vector<Integer> combinedRolls = new Vector<>();
        combinedRolls.addAll(this.rolls);
        combinedRolls.addAll(r2.rolls);
        return new RollResult(newTotal, this.modifier + r2.modifier, combinedRolls);
    }

    /**
     * Returns a string summary of the result.
     *
     * @return A string like "15 <= [6, 5, 4] +3"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(total).append(" <= ").append(rolls);
        if (modifier > 0) {
            sb.append(" +").append(modifier);
        } else if (modifier < 0) {
            sb.append(" ").append(modifier);
        }
        return sb.toString();
        //heheheheheh
//hihi
    }
}
