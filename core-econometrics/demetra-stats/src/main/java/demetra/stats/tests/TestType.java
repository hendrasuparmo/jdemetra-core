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
package demetra.stats.tests;

import demetra.design.Development;

/**
 * An enumeration representing the way tests are conducted
 * @author Jean Palate
 */
@Development(status = Development.Status.Release)
public enum TestType {
    /**
     *
     */
    Undefined,
    /** test_Lower : test that a statistic is below some value */
    Lower,
    /** test_Upper : test that a statistic is above some value */
    Upper,
    /**
     * test_TwoSided : test that a statistic is below some value and above
     * another value
     */
    TwoSided;
}
