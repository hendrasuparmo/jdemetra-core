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
package demetra.regarima.ami;

import demetra.design.Development;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
@Development(status = Development.Status.Preliminary)
@lombok.Data
public class RegArimaContext {
    
    private ModelDescription description;
    private ModelEstimation estimation;


//    public ModellingContext() {
//        processingLog = new ArrayList<>();
//    }
//
//    public ModellingContext(boolean log) {
//        if (log) {
//            processingLog = new ArrayList<>();
//        } else {
//            processingLog = null;
//        }
//    }
//
//    public PreprocessingModel tmpModel() {
//        PreprocessingModel model = new PreprocessingModel(description, estimation);
//        model.info_ = information;
//        return model;
//    }
//
//    public PreprocessingModel current(boolean update) {
//        if (!update) {
//            PreprocessingModel model = new PreprocessingModel(description.clone(), estimation);
//            model.info_ = information.clone();
//            return model;
//
//        } else {
//            PreprocessingModel model = new PreprocessingModel(description.clone(), estimation);
//            model.updateModel();
//            model.info_ = information.clone();
//            return model;
//        }
//    }
//    public final InformationSet information = new InformationSet();
//    @Deprecated
//    public boolean verbose = false;
//    public final List<ProcessingInformation> processingLog;
//    public boolean automodelling = false;
//    public boolean outliers = false;
//    public boolean hasseas;
//    public int originalSeasonalityTest;
}