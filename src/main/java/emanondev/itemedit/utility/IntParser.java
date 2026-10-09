package emanondev.itemedit.utility;

public class IntParser {

    private final Integer value;

    public IntParser(Integer value) {
        this.value = value;
    }

    public IntParser(String value) {
        this(value, 0);
    }

    public IntParser(String value, int add) {
        Integer tmp;
        try {
            tmp = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            tmp = null;
        }
        this.value = tmp == null ? null : (tmp + add);
    }

    public boolean isNumber() {
        return value != null;
    }

    public boolean is(int value) {
        return isNumber() && this.value == value;
    }

    public boolean isMin(int valueInclusive) {
        return !isNumber() || this.value >= valueInclusive;
    }

    public boolean isNumberMin(int valueInclusive) {
        return isNumber() && isMin(valueInclusive);
    }

    public boolean isInRange(int minInclusive, int maxInclusive) {
        return !isNumber() || value >= minInclusive && value <= maxInclusive;
    }

    public boolean isNumberInRange(int minInclusive, int maxInclusive) {
        return isNumber() && isInRange(minInclusive, maxInclusive);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
