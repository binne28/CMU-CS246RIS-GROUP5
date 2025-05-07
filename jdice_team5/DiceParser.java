import java.util.*;
import java.util.logging.*;

/**
 * JDice: Java Dice Rolling Program
 * This class parses dice notation strings (e.g., "2d6+3") and converts them into DieRoll objects
 * that can be executed to simulate dice rolls.
 *
 * Refactored to improve readability, fix syntax issues, and add logging + validation.
 * Author: Andrew D. Hilton
 * Refactored by: [Your Name]
 * Date: May 2025
 */
public class DiceParser {

    private static final Logger logger = Logger.getLogger(DiceParser.class.getName());

    /* Helper class to manage input stream */
    private static class StringStream {
        private StringBuffer buffer;

        public StringStream(String s) {
            buffer = new StringBuffer(s);
        }

        private void munchWhiteSpace() {
            int index = 0;
            char curr;
            while (index < buffer.length()) {
                curr = buffer.charAt(index);
                if (!Character.isWhitespace(curr))
                    break;
                index++;
            }
            buffer = buffer.delete(0, index);
        }

        public boolean isEmpty() {
            munchWhiteSpace();
            return buffer.toString().equals("");
        }

        public Integer getInt() {
            return readInt();
        }

        public Integer readInt() {
            munchWhiteSpace();
            int index = 0;
            char curr;
            while (index < buffer.length()) {
                curr = buffer.charAt(index);
                if (!Character.isDigit(curr))
                    break;
                index++;
            }
            try {
                Integer ans = Integer.parseInt(buffer.substring(0, index));
                buffer = buffer.delete(0, index);
                return ans;
            } catch (Exception e) {
                return null;
            }
        }

        public Integer readSgnInt() {
            munchWhiteSpace();
            StringStream state = save();
            if (checkAndEat("+")) {
                Integer ans = readInt();
                if (ans != null)
                    return ans;
                restore(state);
                return null;
            }
            if (checkAndEat("-")) {
                Integer ans = readInt();
                if (ans != null)
                    return -ans;
                restore(state);
                return null;
            }
            return readInt();
        }

        public boolean checkAndEat(String s) {
            munchWhiteSpace();
            if (buffer.indexOf(s) == 0) {
                buffer = buffer.delete(0, s.length());
                return true;
            }
            return false;
        }

        public StringStream save() {
            return new StringStream(buffer.toString());
        }

        public void restore(StringStream ss) {
            this.buffer = new StringBuffer(ss.buffer);
        }

        public String toString() {
            return buffer.toString();
        }
    }

    /**
     * Parses a roll expression into a list of DieRolls.
     * 
     * @param s the string to parse (e.g. "2d6+1 ; d8")
     * @return list of DieRolls, or null if input is invalid
     */
    public static Vector<DieRoll> parseRoll(String s) {
        StringStream stream = new StringStream(s.toLowerCase());
        Vector<DieRoll> rolls = parseRollInner(stream, new Vector<>());
        if (stream.isEmpty()) {
            logger.info("Parsed successfully: " + s);
            return rolls;
        }
        logger.warning("Parsing failed: leftover input in '" + stream.toString() + "'");
        return null;
    }

    private static Vector<DieRoll> parseRollInner(StringStream stream, Vector<DieRoll> rolls) {
        Vector<DieRoll> xdice = parseXDice(stream);
        if (xdice == null) {
            return null;
        }
        rolls.addAll(xdice);
        if (stream.checkAndEat(";")) {
            return parseRollInner(stream, rolls);
        }
        return rolls;
//hehe
    }

    private static Vector<DieRoll> parseXDice(StringStream stream) {
        StringStream saved = stream.save();
        Integer multiplier = stream.getInt();
        int num;
        if (multiplier == null) {
            num = 1;
        } else {
            if (stream.checkAndEat("x")) {
                num = multiplier;
            } else {
                num = 1;
                stream.restore(saved);
            }
        }
        DieRoll dr = parseDice(stream);
        if (dr == null) {
            return null;
        }
        Vector<DieRoll> result = new Vector<>();
        for (int i = 0; i < num; i++) {
            result.add(dr);
        }
        return result;
    }

    private static DieRoll parseDice(StringStream stream) {
        return parseDTail(parseDiceInner(stream), stream);
    }

    private static DieRoll parseDiceInner(StringStream stream) {
        Integer num = stream.getInt();
        int ndice = (num == null) ? 1 : num;

        if (stream.checkAndEat("d")) {
            num = stream.getInt();
            if (num == null)
                return null;
            int dsides = num;

            int bonus = 0;
            Integer signedBonus = stream.readSgnInt();
            if (signedBonus != null)
                bonus = signedBonus;

            return new DieRoll(ndice, dsides, bonus);
        } else {
            return null;
        }
    }

    private static DieRoll parseDTail(DieRoll first, StringStream stream) {
        if (first == null)
            return null;
        if (stream.checkAndEat("&")) {
            DieRoll next = parseDice(stream);
            return parseDTail(new DiceSum(first, next), stream);
        } else {
            return first;
        }
    }

    /**
     * Test method for parsing and rolling expressions.
     * 
     * @param s expression string to test
     */
    private static void test(String s) {
        Vector<DieRoll> rolls = parseRoll(s);
        if (rolls == null) {
            System.out.println("Failure: " + s);
        } else {
            System.out.println("Results for " + s + ":");
            for (DieRoll dr : rolls) {
                System.out.print(dr);
                System.out.print(": ");
                System.out.println(dr.makeRoll());
            }
        }
    }

    public static void main(String[] args) {
        test("d6");
        test("2d6");
        test("d6+5");
        test("4X3d8-5");
        test("12d10+5 & 4d6+2");
        test("d6 ; 2d4+3");
        test("4d6+3 ; 8d12 -15 ; 9d10 & 3d6 & 4d12 +17");
        test("4d6 + xyzzy");
        test("hi");
        test("4d4d4");
    }
}