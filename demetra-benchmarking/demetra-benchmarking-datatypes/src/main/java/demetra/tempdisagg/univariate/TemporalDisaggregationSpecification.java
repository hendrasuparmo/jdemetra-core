/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.tempdisagg.univariate;

import demetra.algorithms.AlgorithmDescriptor;
import demetra.data.AggregationType;
import demetra.data.Parameter;
import demetra.timeseries.TimeSelector;
import java.util.Objects;
import demetra.processing.ProcSpecification;

/**
 *
 * @author Jean
 */
@lombok.Data
@lombok.NoArgsConstructor
public class TemporalDisaggregationSpecification implements ProcSpecification {

    public static final AlgorithmDescriptor ALGORITHM = new AlgorithmDescriptor("temporaldisaggregation", "generic", null);

    @Override
    public AlgorithmDescriptor getAlgorithmDescriptor() {
        return ALGORITHM;
    }

    @Override
    public TemporalDisaggregationSpecification makeCopy() {
        TemporalDisaggregationSpecification spec=new TemporalDisaggregationSpecification();
        spec.constant=constant;
        spec.trend=trend;
        spec.log=log;
        spec.aggregationType=aggregationType;
        spec.residualsModel=residualsModel;
        spec.parameter=parameter.clone();
        spec.diffuseRegressors=diffuseRegressors;
        spec.span=span.clone();
        spec.estimationPrecision=estimationPrecision;
        spec.maximumLikelihood=maximumLikelihood;
        spec.truncatedParameter=truncatedParameter;
        spec.zeroInitialization=zeroInitialization;
        return spec;
    }

    public static enum Model {

        Wn,
        Ar1,
        Rw,
        RwAr1,
        I2, I3;

        public boolean hasParameter() {
            return this == Ar1 || this == RwAr1;
        }

        public boolean isStationary() {
            return this == Ar1 || this == Wn;
        }

        public int getParametersCount() {
            return (this == Ar1 || this == RwAr1) ? 1 : 0;
        }

        public int getDifferencingOrder() {
            switch (this) {
                case Rw:
                case RwAr1:
                    return 1;
                case I2:
                    return 2;
                case I3:
                    return 3;
                default:
                    return 0;
            }
        }
    }

    public static final double DEF_EPS = 1e-5;
    private @lombok.NonNull Model residualsModel = Model.Ar1;
    private boolean constant = true, trend;
    private boolean log, diffuseRegressors;
    private @lombok.NonNull AggregationType aggregationType = AggregationType.Sum;
    private @lombok.NonNull Parameter parameter = new Parameter();
    private double truncatedParameter = 0;
    private @lombok.NonNull TimeSelector span = TimeSelector.all();
    private boolean zeroInitialization, maximumLikelihood = true;
    private double estimationPrecision = DEF_EPS;

    public void reset() {
        residualsModel = Model.Ar1;
        constant = true;
        trend = false;
        log = false;
        diffuseRegressors = false;
        zeroInitialization = false;
        maximumLikelihood = true;
        parameter = new Parameter();
        aggregationType = AggregationType.Sum;
        estimationPrecision = DEF_EPS;
        span = TimeSelector.all();
    }

}