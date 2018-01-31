/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdr.spec.x13;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.RegressionTestSpec;
import ec.tstoolkit.modelling.TradingDaysSpecType;
import static ec.tstoolkit.modelling.TradingDaysSpecType.UserDefined;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.LengthOfPeriodType;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class TradingDaysSpec extends BaseRegArimaSpec {

    private ec.tstoolkit.modelling.arima.x13.TradingDaysSpec inner() {
        return core.getRegression().getTradingDays();
    }

    public TradingDaysSpec(RegArimaSpecification spec) {
        super(spec);
    }

    public String getOption() {
         if (inner().isStockTradingDays()) {
            return TradingDaysSpecType.Stock.name();
        } else if (inner().getHolidays() != null) {
            return TradingDaysSpecType.Holidays.name();
        } else if (inner().getUserVariables() != null) {
            return TradingDaysSpecType.UserDefined.name();
        } else if (inner().getTradingDaysType() != TradingDaysType.None) {
            return TradingDaysSpecType.Default.name();
        } else {
            return TradingDaysSpecType.None.name();
        }
    }

    public void setOption(String option) {
        TradingDaysSpecType value =TradingDaysSpecType.valueOf(option);
        switch (value) {
            case None:
        inner().disable();
                break;
            case Default:
                inner().setTradingDaysType(TradingDaysType.TradingDays);
                inner().setLengthOfPeriod(LengthOfPeriodType.LeapYear);
                inner().setTest(RegressionTestSpec.Remove);
                inner().setAutoAdjust(true);
                inner().setHolidays(null);
                break;
            case Holidays:
                inner().setTradingDaysType(TradingDaysType.TradingDays);
                inner().setLengthOfPeriod(LengthOfPeriodType.LeapYear);
                inner().setTest(RegressionTestSpec.Remove);
                inner().setAutoAdjust(true);
                inner().setHolidays(GregorianCalendarManager.DEF);
                break;
            case UserDefined:
                inner().setUserVariables(new String[]{});
                inner().setTest(RegressionTestSpec.Remove);
                break;
            case Stock:
                inner().disable();
                inner().setStockTradingDays(31);
                inner().setTest(RegressionTestSpec.Remove);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");

        }
    }

    public RegressionTestSpec getTest() {
        return inner().getTest();
    }

    public void setTest(RegressionTestSpec value) {
        inner().setTest(value);
    }

    public boolean isAutoAdjust() {
        return inner().isAutoAdjust();
    }

    public void setAutoAdjust(boolean value) {
        inner().setAutoAdjust(value);
    }

    public TradingDaysType getTradingDays() {

        return inner().getTradingDaysType();
    }

    public void setTradingDays(TradingDaysType value) {
        inner().setTradingDaysType(value);
    }

    public LengthOfPeriodType getLengthOfPeriod() {
        return inner().getLengthOfPeriod();
    }

    public void setLengthOfPeriod(LengthOfPeriodType value) {
        inner().setLengthOfPeriod(value);
    }

//    public Holidays getHolidays() {
//        return new Holidays(inner().getHolidays());
//    }
//
//    public void setHolidays(Holidays holidays) {
//        inner().setHolidays(holidays.getName());
//    }
//
//    public UserVariables getUserVariables() {
//        return new UserVariables(inner().getUserVariables());
//    }
//
//    public void setUserVariables(UserVariables vars) {
//        inner().setUserVariables(vars.getNames());
//    }
//
    public int getW() {
        return inner().getStockTradingDays();
    }

    public void setW(int w) {
        inner().setStockTradingDays(w);
    }

}
