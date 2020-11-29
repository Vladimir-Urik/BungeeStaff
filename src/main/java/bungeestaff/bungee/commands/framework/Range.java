package bungeestaff.bungee.commands.framework;

public class Range {

    private final int max;
    private final int min;

    public Range(int wanted) {
        this.min = wanted;
        this.max = wanted;
    }

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int check(int n) {
        if (n > max && max != -1)
            return 1;
        else if (n < min && min != -1)
            return -1;
        else return 0;
    }

    @Override
    public String toString() {
        return min + " - " + max;
    }
}
