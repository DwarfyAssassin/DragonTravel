/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

public class SuggestionContext {
    private static final SuggestionContext FOR_LAST = new SuggestionContext(null, true);
    private static final SuggestionContext FOR_HANGING = new SuggestionContext(null, false);
    private final Character flag;
    private final boolean forLast;

    private SuggestionContext(Character flag, boolean forLast) {
        this.flag = flag;
        this.forLast = forLast;
    }

    public boolean forHangingValue() {
        return this.flag == null && !this.forLast;
    }

    public boolean forLastValue() {
        return this.flag == null && this.forLast;
    }

    public boolean forFlag() {
        return this.flag != null;
    }

    public Character getFlag() {
        return this.flag;
    }

    public String toString() {
        return this.forFlag() ? "-" + this.getFlag() : (this.forHangingValue() ? "hanging" : "last");
    }

    public static SuggestionContext flag(Character flag) {
        return new SuggestionContext(flag, false);
    }

    public static SuggestionContext lastValue() {
        return FOR_LAST;
    }

    public static SuggestionContext hangingValue() {
        return FOR_HANGING;
    }
}

