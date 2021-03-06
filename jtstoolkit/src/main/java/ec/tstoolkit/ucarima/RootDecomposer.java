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
package ec.tstoolkit.ucarima;

import ec.tstoolkit.arima.ArimaModel;
import ec.tstoolkit.design.Development;
import ec.tstoolkit.maths.linearfilters.BackFilter;
import ec.tstoolkit.maths.linearfilters.SymmetricFrequencyResponse;
import ec.tstoolkit.maths.linearfilters.SymmetricFilter;
import ec.tstoolkit.maths.matrices.Householder;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.maths.matrices.MatrixException;
import ec.tstoolkit.maths.polynomials.AllSelector;
import ec.tstoolkit.maths.polynomials.IRootSelector;
import ec.tstoolkit.maths.polynomials.Polynomial;

/**
 *
 * @author Jean Palate
 */
@Development(status = Development.Status.Alpha)
public class RootDecomposer extends SimpleModelDecomposer {

    private IRootSelector m_selector;

    private BackFilter m_sur, m_nur;

    private BackFilter m_sar, m_nar;

    private SymmetricFilter m_sma, m_sfds, m_sfdn, m_sfcs, m_sfcn;

    /**
     *
     */
    public RootDecomposer() {
	m_selector = new AllSelector();
    }

    /**
     * 
     * @param sel
     */
    public RootDecomposer(final IRootSelector sel) {
	m_selector = sel;
    }

    /**
     *
     */
    @Override
    protected void calc() {
	if (m_model.isNull()) {
	    m_s = new ArimaModel(null, null, null, 0);
	    m_n = new ArimaModel(null, null, null, 0);
	    return;
	}

	if (m_selector == null) {
	    m_s = new ArimaModel(null, null, null, 0);
	    m_n = m_model;
	    return;
	}

	splitRoots();
	simplifyMA();

	if (!checkSpecialCases())
	    return;

	double[] n = m_sma.getCoefficients();
	double[] ds = m_sfds.getCoefficients();
	double[] dn = m_sfdn == null ? Polynomial.ONE.getCoefficients() : m_sfdn.getCoefficients();

	int q = n.length - 1;
	int p = ds.length + dn.length - 2;
	int ps = ds.length - 1, pn = dn.length - 1, qs = ps > 0 ? ps - 1 : 0, qn = pn > 0 ? pn - 1
		: 0;
	if (q >= p) // qn > 0 and pn > 0
	    qn = q - ps;

	int xs = qs + 1, xn = qn + 1, x = xs + xn;

	Matrix m = new Matrix(x, x);

	// modify the arrays to get the frequency response (and not the agf)

	n[0] /= 2;
	ds[0] /= 2;
	dn[0] /= 2;

	// cos j * cos k =0.5 * ( cos (j+k) + cos (j-k) )
	// j identifies de coeff of ns (nn) and k de coeff of dn (ds).
	// j is in [0, qs] ([0, qn]) and k in [0, pn] ([0, ps])

	// cos (j+k)

	for (int j = 0; j <= qs; ++j)
	    for (int k = 0; k <= pn; ++k)
		m.set(j + k, j, dn[k]);
	for (int j = 0; j <= qn; ++j)
	    for (int k = 0; k <= ps; ++k)
		m.set(j + k, j + xs, ds[k]);
	for (int j = 0; j <= qs; ++j)
	    for (int k = 0; k <= pn; ++k) {
		int i = j - k;
		if (i < 0)
		    i = -i;
		m.set(i, j, m.get(i, j) + dn[k]);
	    }
	for (int j = 0; j <= qn; ++j)
	    for (int k = 0; k <= ps; ++k) {
		int i = j - k;
		if (i < 0)
		    i = -i;
		m.set(i, j + xs, m.get(i, j + xs) + ds[k]);
	    }

	Householder qr = new Householder(false);
	// qr.Epsilon = 1e-30;
	qr.decompose(m);
	double[] sq = new double[x];
	for (int i = 0; i <= q; ++i)
	    sq[i] = n[i];

	try {
	    sq = qr.solve(sq);
	} catch (MatrixException e) {
	    throw new RuntimeException(e);
	}

	double[] rcs = new double[xs];
	double[] rcn = new double[xn];

	for (int i = 0; i < rcs.length; ++i)
	    rcs[i] = sq[i];
	for (int i = 0; i < rcn.length; ++i)
	    rcn[i] = sq[xs + i];

	rcs[0] *= 2;
	rcn[0] *= 2;
	SymmetricFilter sma = SymmetricFilter.of(rcs);
	SymmetricFilter nma = SymmetricFilter.of(rcn);

	m_s = new ArimaModel(m_sar, m_sur, null, null, 0,
		m_sfcs != null ? m_sfcs.times(sma) : sma);
	m_n = new ArimaModel(m_nar, m_nur, null, null, 0,
		m_sfcn != null ? m_sfcn.times(nma) : nma);
	//ArimaModel check = m_model.minus(m_n.plus(m_s));
    }

    private boolean checkSpecialCases() {
	// No selected roots:
	if (m_sfds == null || m_sfds.getLength() <= 1) {
	    m_s = new ArimaModel(null, m_sur, null, 0);
	    m_n = new ArimaModel(m_nar, m_nur, m_sfcn != null ? m_sma
		    .times(m_sfcn) : m_sma);
	    return false;
	} else
	    return true;

    }

    /**
     *
     */
    @Override
    protected void clear() {
	super.clear();
	m_sur = null;
	m_nur = null;
	m_sar = null;
	m_nar = null;
	m_sfds = null;
	m_sfdn = null;
	m_sfcs = null;
	m_sfcn = null;
	m_sma = null;
    }

    /**
     * 
     * @return
     */
    public IRootSelector getSelector() {
	return m_selector;
    }

    /**
     * 
     * @param value
     */
    public void setSelector(final IRootSelector value) {
	m_selector = value;
	clear();
    }

    // search for possible common UR in MA and AR
    private void simplifyMA() {
	m_sma = m_model.sma();
	//SymmetricFrequencyResponse.SimplifyingTool smp = new SymmetricFrequencyResponse.SimplifyingTool();
	SymmetricFrequencyResponse sfma = new SymmetricFrequencyResponse(m_sma);
	if (m_sur != null){
//	     if (smp.simplify(sfma, m_sur))
//	     {
//	     sfma = smp.getLeft();
//	     m_sfcs = smp.getCommon().toSymmetriicFilter();
//	     if (smp.getRight() != null)
//	     m_sfds = smp.getRight().toSymmetriicFilter();
//	     }
//	     else
	    m_sfds = SymmetricFilter.createFromFilter(m_sur);
        }
	if (m_nur != null){
//	     if (smp.simplify(sfma, m_nur))
//	     {
//	     sfma = smp.getLeft();
//	     m_sfcn = smp.getCommon().toSymmetriicFilter();
//	     if (smp.getRight() != null)
//	     m_sfdn = smp.getRight().toSymmetriicFilter();
//	     }
//	     else
	    m_sfdn = SymmetricFilter.createFromFilter(m_nur);
        }
	if (m_sfcs != null || m_sfcn != null)
	    m_sma = sfma.toSymmetricFilter();
	if (m_sar != null)
	    if (m_sfds != null)
		m_sfds = m_sfds.times(SymmetricFilter.createFromFilter(m_sar));
	    else
		m_sfds = SymmetricFilter.createFromFilter(m_sar);
	if (m_nar != null)
	    if (m_sfdn != null)
		m_sfdn = m_sfdn.times(SymmetricFilter.createFromFilter(m_nar));
	    else
		m_sfdn = SymmetricFilter.createFromFilter(m_nar);
    }

    private void splitRoots() {
	m_selector.select(m_model.getStationaryAR().getPolynomial());
	if (m_selector.getSelection() != null)
	    m_sar = new BackFilter(m_selector.getSelection());
	else
	    m_sar = BackFilter.ONE;
	if (m_selector.getOutofSelection() != null)
	    m_nar = new BackFilter(m_selector.getOutofSelection());
	else
	    m_nar = BackFilter.ONE;

	m_selector.selectUnitRoots(m_model.getNonStationaryAR().getPolynomial());
	if (m_selector.getSelection() != null)
	    m_sur = new BackFilter(m_selector.getSelection());
	else
	    m_sur = BackFilter.ONE;
	if (m_selector.getOutofSelection() != null)
	    m_nur = new BackFilter(m_selector.getOutofSelection());
	else
	    m_nur = BackFilter.ONE;
    }
}
