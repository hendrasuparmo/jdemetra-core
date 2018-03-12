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

package demetra.stats.tests.seasonal;

import demetra.design.Development;

/**
 * @author Jean Palate
 */
@Development(status = Development.Status.Alpha)
class Item implements Comparable<Item> {
    int pos;
    double rank;
    double val;

    Item(int pos, double val) {
	this.pos = pos;
	this.val = val;
    }

    @Override
    public int compareTo(Item other) {
	return Double.compare(val, other.val);
    }
}