package bungeestaff.bungee.commands.framework;

public class Range {

    private final int max;
    private final int min;

    public Range(int wanted) {
        this.max = wanted;
        this.min = wanted;
    }

    public Range(int max, int min) {
        this.max = max;
        this.min = min;
    }

    public int check(int n) {
        if (n > max && max != -1)
            return 1;
        else if (n < min && min != -1)
            return -1;
        else return 0;
    }
}
