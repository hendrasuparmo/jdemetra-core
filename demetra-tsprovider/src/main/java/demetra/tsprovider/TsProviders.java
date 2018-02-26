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
package demetra.tsprovider;

import demetra.tsprovider.util.IConfig;
import demetra.utilities.TreeTraverser;
import ioutil.IO;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.function.Function;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Utility class that simplify the use of Ts providers.
 *
 * @author Philippe Charles
 * @since 1.0.0
 */
@lombok.experimental.UtilityClass
public class TsProviders {

    public void prettyPrintTree(
            @Nonnull DataSourceProvider provider,
            @Nonnull DataSource dataSource,
            @Nonnegative int maxLevel,
            @Nonnull PrintStream printStream,
            boolean displayName) throws IOException {

        Function<IConfig, String> formatter = displayName
                ? o -> o instanceof DataSource ? provider.getDisplayName((DataSource) o) : " " + provider.getDisplayNodeName((DataSet) o)
                : o -> o instanceof DataSource ? provider.toMoniker((DataSource) o).getId() : " " + provider.toMoniker((DataSet) o).getId();

        IO.Function<IConfig, Iterable<? extends IConfig>> children = o -> {
            return o instanceof DataSource
                    ? provider.children((DataSource) o)
                    : ((DataSet) o).getKind() == DataSet.Kind.COLLECTION ? provider.children((DataSet) o) : Collections.emptyList();
        };

        try {
            TreeTraverser.of(dataSource, children.asUnchecked()).prettyPrintTo(printStream, maxLevel, formatter);
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }
}
