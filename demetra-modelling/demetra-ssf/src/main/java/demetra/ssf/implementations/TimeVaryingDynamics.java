/*
 * Copyright 2016 National Bank of Belgium
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
package demetra.ssf.implementations;

import demetra.data.DataBlock;
import demetra.maths.matrices.Matrix;
import demetra.maths.matrices.SymmetricMatrix;
import demetra.ssf.ISsfDynamics;
import demetra.data.DoubleSequence;

/**
 * Dynamics for time varying coefficients
 * @author Jean Palate
 */
public class TimeVaryingDynamics {

    public static ISsfDynamics of(int n, double var) {
        return new TimeVaryingCDiag(n, var);
    }

    public static ISsfDynamics of(DoubleSequence dvar) {
        return new TimeVaryingDiag(dvar);
    }

    public static ISsfDynamics of(Matrix var) {
        return new TimeVaryingFull(var);
    }

        static class TimeVaryingCDiag implements ISsfDynamics {

        private final int n;
        private final double var, std;

        TimeVaryingCDiag(final int n, final double var) {
            this.n=n;
            this.var = var;
            this.std =Math.sqrt(var);
        }

        @Override
        public boolean isTimeInvariant() {
            return true;
        }

        @Override
        public boolean areInnovationsTimeInvariant() {
            return true;
        }

        @Override
        public int getInnovationsDim() {
            return n;
        }

        @Override
        public void V(int pos, Matrix qm) {
            qm.diagonal().set(var);
        }

        @Override
        public boolean hasInnovations(int pos) {
            return true;
        }

        @Override
        public void S(int pos, Matrix sm) {
            sm.diagonal().set(std);
        }

        @Override
        public void addSU(int pos, DataBlock x, DataBlock u) {
            int n = x.length();
            for (int i = 0; i < n; ++i) {
                x.add(i, u.get(i) * std);
            }
        }

        @Override

        public void XS(int pos, DataBlock x, DataBlock xs) {
            int n = x.length();
            for (int i = 0; i < n; ++i) {
                xs.set(i, x.get(i) * std);
            }
        }

        @Override
        public void T(int pos, Matrix tr) {
            tr.diagonal().set(1);
        }

        @Override
        public void TX(int pos, DataBlock x) {
        }

        @Override
        public void XT(int pos, DataBlock x) {
        }

        @Override
        public void TVT(int pos, Matrix v) {
        }

        @Override
        public void addV(int pos, Matrix p) {
            p.diagonal().add(var);
        }
    }

    
    static class TimeVaryingDiag implements ISsfDynamics {

        private final DataBlock var, std;

        TimeVaryingDiag(final double[] var) {
            this.var = DataBlock.copyOf(var);
            this.std = DataBlock.copyOf(var);
            std.apply(x -> Math.sqrt(x));
        }

        TimeVaryingDiag(final DoubleSequence var) {
            this.var = DataBlock.of(var);
            this.std = DataBlock.of(var);
            std.apply(x -> Math.sqrt(x));
        }

        @Override
        public boolean isTimeInvariant() {
            return true;
        }

        @Override
        public boolean areInnovationsTimeInvariant() {
            return true;
        }

        @Override
        public int getInnovationsDim() {
            return var.length();
        }

        @Override
        public void V(int pos, Matrix qm) {
            qm.diagonal().copy(var);
        }

        @Override
        public boolean hasInnovations(int pos) {
            return true;
        }

        @Override
        public void S(int pos, Matrix sm) {
            sm.diagonal().copy(std);
        }

        @Override
        public void addSU(int pos, DataBlock x, DataBlock u) {
            int n = x.length();
            for (int i = 0; i < n; ++i) {
                x.add(i, u.get(i) * std.get(i));
            }
        }

        @Override

        public void XS(int pos, DataBlock x, DataBlock xs) {
            int n = x.length();
            for (int i = 0; i < n; ++i) {
                xs.set(i, x.get(i) * std.get(i));
            }
        }

        @Override
        public void T(int pos, Matrix tr) {
            tr.diagonal().set(1);
        }

        @Override
        public void TX(int pos, DataBlock x) {
        }

        @Override
        public void XT(int pos, DataBlock x) {
        }

        @Override
        public void TVT(int pos, Matrix v) {
        }

        @Override
        public void addV(int pos, Matrix p) {
            p.diagonal().add(var);
        }
    }

    static class TimeVaryingFull implements ISsfDynamics {

        private final Matrix var, s;

        TimeVaryingFull(final Matrix var) {
            this.var = var;
            s = var.deepClone();
            SymmetricMatrix.lcholesky(s, 1e-9);
        }

        TimeVaryingFull(final Matrix var, final Matrix s) {
            this.var = var;
            this.s = s;
        }

        @Override
        public boolean isTimeInvariant() {
            return true;
        }

        @Override
        public boolean areInnovationsTimeInvariant() {
            return true;
        }
        @Override
        public int getInnovationsDim() {
            return var.getColumnsCount();
        }

        @Override
        public void V(int pos, Matrix qm) {
            qm.copy(var);
        }

        @Override
        public boolean hasInnovations(int pos) {
            return true;
        }

        @Override
        public void S(int pos, Matrix sm) {
            sm.copy(s);
        }

        @Override
        public void addSU(int pos, DataBlock x, DataBlock u) {
            x.addProduct(s.rowsIterator(), u);
        }

        @Override
        public void XS(int pos, DataBlock x, DataBlock xs) {
            xs.product(x, s.columnsIterator());
        }

        @Override
        public void T(int pos, Matrix tr) {
            tr.diagonal().set(1);
        }

        @Override
        public void TX(int pos, DataBlock x) {
        }

        @Override
        public void XT(int pos, DataBlock x) {
        }

        @Override
        public void TVT(int pos, Matrix v) {
        }

        @Override
        public void addV(int pos, Matrix p) {
            p.add(var);
        }
    }
}