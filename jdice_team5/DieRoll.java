import java.util.Random;
import java.util.logging.Logger;

/**
 * DieRoll class represents a dice roll with a specified number of dice, sides, and a bonus.
 * It supports rolling and retrieving results using the makeRoll method.
 *
 * Refactored:
 * - Fixed syntax errors in makeRoll method.
 * - Improved naming and formatting for readability.
 * - Added input validation in constructor.
 * - Added logging to track roll operations.
 */
//code sửa
public class DieRoll {
    private final int ndice;
    private final int nsides;
    private final int bonus;
    private static final Random rnd;
    private static final Logger logger = Logger.getLogger(DieRoll.class.getName());

    static {
        rnd = new Random();
    }

    /**
     * Constructs a DieRoll with the given parameters.
     * @param ndice Number of dice to roll. Must be > 0.
     * @param nsides Number of sides per die. Must be > 0.
     * @param bonus Bonus to add to the total roll.
     * @throws IllegalArgumentException if ndice or nsides is not positive.
     */
    public DieRoll(int ndice, int nsides, int bonus) {
        if (ndice <= 0 || nsides <= 0) {
            throw new IllegalArgumentException("Number of dice and sides must be positive.");
        }
        this.ndice = ndice;
        this.nsides = nsides;
        this.bonus = bonus;
        logger.info("DieRoll created: " + this.toString());
    }

    /**
     * Rolls the dice and returns the result.
     * @return A RollResult object containing individual rolls and the bonus.
     */
    public RollResult makeRoll() {
        RollResult result = new RollResult(bonus);
        for (int i = 0; i < ndice; i++) {
            int roll = rnd.nextInt(nsides) + 1;
            result.addResult(roll);
            logger.fine("Rolled: " + roll);
        }
        return result;
    }

    /**
     * Returns a string representation of the dice roll, e.g. "2d6+1".
     * @return String representing the dice roll format.
     */
    @Override
    public String toString() {
        String resultStr = ndice + "d" + nsides;
        if (bonus > 0) {
            resultStr += "+" + bonus;
        } else if (bonus < 0) {
            resultStr += bonus;
        }
        return resultStr;
    }
}
