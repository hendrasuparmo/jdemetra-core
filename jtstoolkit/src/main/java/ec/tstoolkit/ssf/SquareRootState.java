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

package ec.tstoolkit.ssf;

import ec.tstoolkit.maths.matrices.Matrix;

/**
 *
 * @author Jean Palate
 */
public class SquareRootState extends State {

    /**
     *
     */
    public double sfi;

    /**
     *
     */
    public Matrix Li;

    /**
     * 
     * @param n
     * @param hasdata
     */
    public SquareRootState(final int n, final boolean hasdata)
    {
 	super(n, hasdata);
	Li = new Matrix(n, n+1);
    }

    /**
     * 
     * @param state
     */
    public void copy(final SquareRootState state)
    {
	super.copy(state);
	sfi = state.sfi;
	Li = state.Li.clone();
    }

}
