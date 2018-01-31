/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdr.spec.ts;

import ec.tstoolkit.timeseries.PeriodSelectorType;
import ec.tstoolkit.timeseries.TsPeriodSelector;

/**
 *
 * @author Jean Palate
 */
public class SpanSelector {

    public static enum Type {

        All,
        From,
        To,
        Between,
        Last,
        First,
        Excluding;

        public static Type of(PeriodSelectorType type) {
            switch (type) {
                case All:
                    return All;
                case From:
                    return From;
                case To:
                    return To;
                case Between:
                    return Between;
                case Last:
                    return Last;
                case First:
                    return First;
                case Excluding:
                    return Excluding;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public static PeriodSelectorType to(Type type) {
            switch (type) {
                case All:
                    return PeriodSelectorType.All;
                case From:
                    return PeriodSelectorType.From;
                case To:
                    return PeriodSelectorType.To;
                case Between:
                    return PeriodSelectorType.Between;
                case Last:
                    return PeriodSelectorType.Last;
                case First:
                    return PeriodSelectorType.First;
                case Excluding:
                    return PeriodSelectorType.Excluding;
                default:
                    return PeriodSelectorType.None;
            }
        }
    }

    private final TsPeriodSelector core;

    public SpanSelector(TsPeriodSelector sel) {
        core = sel;
    }

    public TsPeriodSelector getCore() {
        return core;
    }

    public String getType() {
        return core.getType().name();
    }

    public String getStart() {
        return Utility.toString(core.getD0());
    }

    public String getEnd() {
        return Utility.toString(core.getD1());
    }

    public int getFirst() {
        return core.getN0();
    }

    public int getLast() {
        return core.getN1();
    }

}
