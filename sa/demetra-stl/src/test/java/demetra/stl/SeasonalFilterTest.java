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
package demetra.stl;

import demetra.stl.IDataSelector;
import demetra.stl.SeasonalFilter;
import demetra.stl.LoessSpecification;
import demetra.stl.IDataGetter;
import data.Data;
import org.junit.Test;
import static org.junit.Assert.*;
import demetra.data.Doubles;

/**
 *
 * @author Jean Palate
 */
public class SeasonalFilterTest {
    
    public SeasonalFilterTest() {
    }

    @Test
    public void testSomeMethod() {
        Doubles s=Data.X;
        double[] d = s.toArray();
        LoessSpecification spec = LoessSpecification.of(7, 0);
        LoessSpecification lspec = LoessSpecification.of(13, 1);
        SeasonalFilter filter=new SeasonalFilter(spec, lspec, 12);
        double[] sd=new double[d.length];
        filter.filter(IDataGetter.of(d), null, false, IDataSelector.of(sd));
//        System.out.println(DataBlock.of(d));
//        System.out.println(DataBlock.of(sd));
    }
}