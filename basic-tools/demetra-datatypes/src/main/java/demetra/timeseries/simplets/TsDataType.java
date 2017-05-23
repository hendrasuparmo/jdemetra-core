/*
 * Copyright 2017 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.timeseries.simplets;

import demetra.data.DoubleValues;
import demetra.design.Development;
import demetra.design.Immutable;
import demetra.timeseries.ITimeSeries;
import java.util.Random;

/**
 * A TsDataType is a raw time series, containing only the actual data.
 * TsDataType can only handle regular time series, with observations
 * corresponding to the usual time decomposition of an year (frequency lower or
 * equal to the monthly frequency). Observations are represented by double
 * values. Missing values are allowed; they are represented by Double.NaN.
 *
 * @author Jean Palate
 */
@Development(status = Development.Status.Alpha)
@Immutable
@lombok.EqualsAndHashCode
public final class TsDataType implements ITimeSeries.OfDouble<TsPeriod, TsObservation> {

    /**
     * Creates a random time series
     *
     * @param freq The frequency of the series.
     * @param seed
     * @return A time series with a random length (<600 observations), a random
     * starting period (between 1970 and 1990) and random observations is
     * generated
     */
    public static TsDataType random(TsFrequency freq, int seed) {
        Random rnd = new Random(seed);
        int beg = rnd.nextInt(240);
        int count = rnd.nextInt(600);
        double[] data = new double[count];
        double cur = rnd.nextDouble() + 100;
        for (int i = 0; i < count; ++i) {
            cur = cur + rnd.nextDouble() - .5;
            data[i] = cur;
        }
        return new TsDataType(TsDomain.of(new TsPeriod(freq, beg), data.length), DoubleValues.ofInternal(data));
    }

    public static TsDataType of(TsPeriod start, DoubleValues values) {
        return new TsDataType(TsDomain.of(start, values.length()), values);
    }

    private final TsDomain domain;
    private final DoubleValues values;

    private TsDataType(TsDomain domain, DoubleValues values) {
        this.domain = domain;
        this.values = values;
    }

    @Override
    public TsDomain getDomain() {
        return domain;
    }

    @Override
    public DoubleValues getValues() {
        return values;
    }

    @Override
    public TsObservation get(int index) throws IndexOutOfBoundsException {
        return new TsObservation(getPeriod(index), getValue(index));
    }

    /**
     * Gets the frequency of the series.
     *
     * @return The frequency.
     */
    public TsFrequency getFrequency() {
        return domain.getFrequency();
    }

    public TsPeriod getStart() {
        return domain.getStart();
    }

    /**
     * *
     * Gets the data corresponding to a given period. The period should have the
     * same frequency of the time series, otherwise an exception will be thrown.
     *
     * @param period The considered period.
     * @return The corresponding data or Nan if the period doesn't belong to
     * this time series
     */
    public double getDoubleValue(TsPeriod period) {
        int pos = domain.search(period);
        return (pos < 0 || pos >= values.length()) ? Double.NaN : values.get(pos);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < domain.length(); ++i) {
            builder.append(domain.get(i)).append('\t').append(values.get(i));
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    /**
     * Makes a frequency change of this series.
     *
     * @param newfreq The new frequency. Must be la divisor of the present
     * frequency.
     * @param conversion Aggregation mode.
     * @param complete If true, the observation for a given period in the new
     * series is set to Missing if some data in the original series are Missing.
     * @return A new time series is returned.
     */
    public TsDataType changeFrequency(final TsFrequency newfreq,
            final TsAggregationType conversion, final boolean complete) {
        int freq = domain.getFrequency().getAsInt(), nfreq = newfreq.getAsInt();
        if (freq % nfreq != 0) {
            return null;
        }
        if (freq == nfreq) {
            return this;
        }
        int nconv = freq / nfreq;
        int c = length();
        int z0 = 0;
        int beg = domain.getStart().id();

        // d0 and d1
        int nbeg = beg / nconv;
        // nbeg is the first period in the new frequency
        // z0 is the number of periods in the old frequency being dropped
        int n0 = nconv, n1 = nconv;
        if (beg % nconv != 0) {
            if (complete) {
                // Attention! Different treatment if beg is negative 
                // We always have that x = x/q + x%q
                // but the integer division is rounded towards 0
                if (beg > 0) {
                    ++nbeg;
                    z0 = nconv - beg % nconv;
                } else {
                    z0 = -beg % nconv;
                }
            } else {
                if (beg < 0) {
                    --nbeg;
                }
                n0 = (nbeg + 1) * nconv - beg;
            }
        }

        int end = beg + c; // excluded
        int nend = end / nconv;

        if (end % nconv != 0) {
            if (complete) {
                if (end < 0) {
                    --nend;
                }
            } else {
                if (end > 0) {
                    ++nend;
                }
                n1 = end - (nend - 1) * nconv;
            }
        }
        int n = nend - nbeg;
        double[] result = new double[n];
        if (n > 0) {
            for (int i = 0, j = z0; i < n; ++i) {
                int nmax = nconv;
                if (i == 0) {
                    nmax = n0;
                } else if (i == n - 1) {
                    nmax = n1;
                }
                double d = 0;
                int ncur = 0;

                for (int k = 0; k < nmax; ++k, ++j) {
                    double dcur = getValue(j);
                    if (Double.isFinite(dcur)) {
                        switch (conversion) {
                            case Last:
                                d = dcur;
                                break;
                            case First:
                                if (ncur == 0) {
                                    d = dcur;
                                }
                                break;
                            case Min:
                                if ((ncur == 0) || (dcur < d)) {
                                    d = dcur;
                                }
                                break;
                            case Max:
                                if ((ncur == 0) || (dcur > d)) {
                                    d = dcur;
                                }
                                break;
                            default:
                                d += dcur;
                                break;
                        }
                        ++ncur;
                    }
                }
                if ((ncur == nconv) || (!complete && (ncur != 0))) {
                    if (conversion == TsAggregationType.Average) {
                        d /= ncur;
                    }
                    result[i] = d;
                }
            }
        }
        return TsDataType.of(TsPeriod.ofInternal(newfreq, nbeg), DoubleValues.ofInternal(result));
    }
}
