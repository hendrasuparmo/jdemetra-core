/*
 * Copyright 2016 National Bank copyOf Belgium
 *  
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions copyOf the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy copyOf the Licence at:
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
import demetra.ssf.ISsfDynamics;

/**
 *
 * @author Jean Palate
 */
public class ConstantDynamics implements ISsfDynamics {

    private final int dim;

    public ConstantDynamics(final int dim) {
        this.dim = dim;
    }

    @Override
    public int getStateDim() {
        return dim;
    }

    @Override
    public boolean isTimeInvariant() {
        return true;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int getInnovationsDim() {
        return 0;
    }

    @Override
    public void V(int pos, Matrix qm) {
    }

    @Override
    public boolean hasInnovations(int pos) {
        return false;
    }

    @Override
    public void S(int pos, Matrix sm) {
    }

//    @Override
//    public void addSX(int pos, DataBlock x, DataBlock y) {
//        y.add(x);
//    }
//
    @Override
    public void T(int pos, Matrix tr) {
        tr.diagonal().set(1);
    }

    @Override
    public boolean isDiffuse() {
        return true;
    }

    @Override
    public int getNonStationaryDim() {
        return dim;
    }

    @Override
    public void diffuseConstraints(Matrix b) {
        b.diagonal().set(1);
    }

    @Override
    public boolean a0(DataBlock a0) {
        return true;
    }

    @Override
    public boolean Pf0(Matrix pf0) {
        return true;
    }

    @Override
    public void Pi0(Matrix p) {
        p.diagonal().set(1);
    }

    @Override
    public void TX(int pos, DataBlock x) {
    }

    @Override
    public void XS(int pos, DataBlock x, DataBlock sx) {
    }

    @Override
    public void addSU(int pos, DataBlock x, DataBlock sx) {
    }

    @Override
    public void XT(int pos, DataBlock x) {
    }

    @Override
    public void TVT(int pos, Matrix v) {
    }

    @Override
    public void addV(int pos, Matrix p) {
    }

}
