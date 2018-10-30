/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.msts;

import demetra.data.DataBlock;
import demetra.data.DoubleReader;
import demetra.data.DoubleSequence;
import demetra.maths.functions.IParametersDomain;
import demetra.maths.functions.ParamValidation;

/**
 *
 * @author palatej
 */
public class VarianceParameter implements IMstsParametersBlock {

    private static final double DEF_STDE = .1;

    private double stde;
    private boolean fixed;
    private final String name;
    private final boolean nullable;

    public VarianceParameter(final String name, boolean nullable) {
        this.name = name;
        this.nullable = nullable;
        stde = DEF_STDE;
        fixed = false;
    }

    public VarianceParameter(final String name, double var, boolean fixed, boolean nullable) {
        stde = Math.sqrt(var);
        this.fixed = fixed;
        this.name = name;
        this.nullable = nullable;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isNullable() {
        return true;
    }

    @Override
    public boolean isPotentialInstability() {
        return true;
    }

    @Override
    public void fixModelParameter(DoubleReader reader) {
        stde = Math.sqrt(reader.next());
        fixed = true;
    }

    @Override
    public void free(){
        fixed=false;
    }

    public double fixStde(double e) {
        double olde = stde;
        stde = Math.abs(e);
        fixed = true;
        return olde;
    }

    public void freeStde(double e) {
        fixed = false;
        stde = e;
    }

    public double defValue() {
        return stde;
    }

    @Override
    public boolean isFixed() {
        return fixed;
    }

    @Override
    public IParametersDomain getDomain() {
        return Domain.INSTANCE;
    }

    @Override
    public int decode(DoubleReader input, double[] buffer, int pos) {
        if (!fixed) {
            double e = input.next();
            buffer[pos] = e * e;
        } else {
            buffer[pos] = stde * stde;
        }
        return pos + 1;
    }

    @Override
    public int encode(DoubleReader input, double[] buffer, int pos) {
        double v = input.next();
        if (!fixed) {
            buffer[pos] = Math.sqrt(v);
            return pos + 1;
        } else {
            return pos;
        }
    }

    @Override
    public int fillDefault(double[] buffer, int pos) {
        if (!fixed) {
            buffer[pos] = stde;
            return pos + 1;
        } else {
            return pos;
        }
    }

    static class Domain implements IParametersDomain {

        static final Domain INSTANCE = new Domain();

        @Override
        public boolean checkBoundaries(DoubleSequence inparams) {
            return true;
        }

        @Override
        public double epsilon(DoubleSequence inparams, int idx) {
            return Math.max(1e-4, Math.abs(inparams.get(0)) * 1e-4);
        }

        @Override
        public int getDim() {
            return 1;
        }

        @Override
        public double lbound(int idx) {
            return -Double.MAX_VALUE;
        }

        @Override
        public double ubound(int idx) {
            return -Double.MIN_VALUE;
        }

        @Override
        public ParamValidation validate(DataBlock ioparams) {
            return ParamValidation.Valid;
        }
    }
}
