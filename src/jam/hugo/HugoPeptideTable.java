
package jam.hugo;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableListMultimap;

import jam.app.JamLogger;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.peptide.Peptide;

/**
 * Maps HUGO symbols to peptides derived from the corresponding
 * proteins (e.g., by proteasomal cleavage).
 */
public final class HugoPeptideTable {
    private final ImmutableListMultimap<HugoSymbol, Peptide> map;

    private HugoPeptideTable(ImmutableListMultimap<HugoSymbol, Peptide> map) {
        this.map = map;
    }

    /**
     * Loads a table from an input file.
     *
     * <p>The file must contain two columns separated by a comma, tab,
     * or pipe delimiter. The first line must be header line (although
     * the column keys are ignored); all subsequent lines must contain
     * a Hugo symbol in the first column and a peptide in the second.
     *
     * @param file the file to load.
     *
     * @return a new table containing the gene-peptide mappings
     * contained in the input file.
     *
     * @throws RuntimeException unless the input file can be opened
     * for reading and contains valid gene-peptide mappings.
     */
    public static HugoPeptideTable load(File file) {
        Loader loader = new Loader(file);
        return loader.load();
    }

    /**
     * Loads a table from an input file.
     *
     * <p>The file must contain two columns separated by a comma, tab,
     * or pipe delimiter. The first line must be header line (although
     * the column keys are ignored); all subsequent lines must contain
     * a Hugo symbol in the first column and a peptide in the second.
     *
     * @param fileName the name of the file to load.
     *
     * @return a new table containing the gene-peptide mappings
     * contained in the input file.
     *
     * @throws RuntimeException unless the input file can be opened
     * for reading and contains valid gene-peptide mappings.
     */
    public static HugoPeptideTable load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Returns a set containing all unique peptides contained in this
     * table.
     *
     * @return a set containing all unique peptides contained in this
     * table.
     */
    public Set<Peptide> collectUniquePeptides() {
        return new HashSet<Peptide>(map.values());
    }

    /**
     * Identifies HUGO symbols contained in this table.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return {@code true} iff this table contains one or more
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
     * @return an immutable list containing all peptides mapped to
     * the specified symbol (an empty list if this table does not
     * contain the symbol).
     */
    public List<Peptide> get(HugoSymbol symbol) {
        return map.get(symbol);
    }

    /**
     * Returns all HUGO symbols contained in this table.
     *
     * @return an immutable set containing all HUGO symbols contained
     * in this table.
     */
    public Set<HugoSymbol> keySet() {
        return map.keySet();
    }

    /**
     * Returns the number of gene-peptide mappings in this table.
     *
     * @return the number of gene-peptide mappings in this table.
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

        private HugoPeptideTable load() {
            try {
                validateColumnKeys();

                for (List<String> line : reader)
                    processLine(line);
            }
            finally {
                reader.close();
            }

            ImmutableListMultimap<HugoSymbol, Peptide> map = builder.build();
            JamLogger.info("HugoPeptideTable: Loaded [%d] records.", map.size());

            return new HugoPeptideTable(map);
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
