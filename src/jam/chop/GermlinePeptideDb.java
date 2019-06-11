
package jam.chop;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableListMultimap;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.peptide.HugoSymbol;
import jam.peptide.Peptide;

/**
 * Maintains an in-memory store of peptide fragments generated by
 * proteasomal cleavage of germline-encoded protein structures.
 */
public final class GermlinePeptideDb {
    private final ImmutableListMultimap<HugoSymbol, Peptide> map;

    private static GermlinePeptideDb global = null;

    private GermlinePeptideDb(ImmutableListMultimap<HugoSymbol, Peptide> map) {
        this.map = map;
    }

    /**
     * Name of the system property with the full path name to the file
     * containing the global germline peptide mappings.
     */
    public static final String PEPTIDE_FILE_PROPERTY = "jam.chop.germlinePeptideFile";

    /**
     * Returns the global germline peptide database defined via system
     * properties.
     *
     * @return the global germline peptide database defined via system
     * properties.
     *
     * @throws RuntimeException unless the file defined by the system
     * property {@code PEPTIDE_FILE_PROPERTY} can be opened for
     * reading and contains
     */
    public static GermlinePeptideDb global() {
        if (global == null)
            global = load(resolvePeptideFile());

        return global;
    }

    private static String resolvePeptideFile() {
        return JamProperties.getRequired(PEPTIDE_FILE_PROPERTY);
    }

    /**
     * Loads a database from an input file.
     *
     * @param file the file to load.
     *
     * @return a new database containing the gene-peptide mappings
     * contained in the input file.
     *
     * @throws RuntimeException unless the input file can be opened
     * for reading and contains valid gene-peptide mappings.
     */
    public static GermlinePeptideDb load(File file) {
        Loader loader = new Loader(file);
        return loader.load();
    }

    /**
     * Loads a database from an input file.
     *
     * @param fileName the name of the file to load.
     *
     * @return a new database containing the gene-peptide mappings
     * contained in the input file.
     *
     * @throws RuntimeException unless the input file can be opened
     * for reading and contains valid gene-peptide mappings.
     */
    public static GermlinePeptideDb load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Identifies HUGO symbols contained in this database.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return {@code true} iff this database contains one or more
     * peptides mapped to the specified symbol.
     */
    public boolean contains(HugoSymbol symbol) {
        return map.containsKey(symbol);
    }

    /**
     * Returns all peptides mapped to a given HUGO symbol.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return an immutable list containing all peptides mapped to the
     * specified HUGO symbol (an empty list if this database does not
     * contain the symbol).
     */
    public List<Peptide> get(HugoSymbol symbol) {
        return map.get(symbol);
    }

    /**
     * Returns all HUGO symbols contained in this database.
     *
     * @return an immutable set containing all HUGO symbols contained
     * in this database.
     */
    public Set<HugoSymbol> keySet() {
        return map.keySet();
    }

    /**
     * Returns the number of gene-peptide mappings in this database.
     *
     * @return the number of gene-peptide mappings in this database.
     */
    public int size() {
        return map.size();
    }

    private static final class Loader {
        private final File file;
        private final TableReader reader;
        private final ImmutableListMultimap.Builder<HugoSymbol, Peptide> builder =
            ImmutableListMultimap.builder();

        private Loader(File file) {
            this.file = file;
            this.reader = TableReader.open(file);
        }

        private GermlinePeptideDb load() {
            try {
                validateColumnKeys();

                for (List<String> line : reader)
                    processLine(line);
            }
            finally {
                reader.close();
            }

            ImmutableListMultimap<HugoSymbol, Peptide> map = builder.build();
            JamLogger.info("GermlinePeptideDb: Loaded [%d] records.", map.size());

            return new GermlinePeptideDb(map);
        }

        private void validateColumnKeys() {
            if (reader.columnKeys().size() != 2)
                throw JamException.runtime("Input file [%s] must contain exactly two columns.", file);
        }

        private void processLine(List<String> line) {
            builder.put(HugoSymbol.instance(line.get(0)), Peptide.parse(line.get(1)));
        }
    }
}