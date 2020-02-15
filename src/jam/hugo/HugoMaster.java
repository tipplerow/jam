
package jam.hugo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamEnv;
import jam.app.JamProperties;
import jam.ensembl.EnsemblGene;
import jam.lang.JamException;

/**
 * Maintains mappings between HUGO symbols and Ensembl genes.
 */
public final class HugoMaster {
    private final Map<HugoSymbol, HugoRecord> records;
    private final Multimap<HugoSymbol, HugoSymbol> aliases;

    private static HugoMaster global = null;

    /**
     * Name of the environment variable that defines the absolute path
     * name for the Hugo master symbol table.  If the system property
     * {@code jam.hugo.masterFile} is also defined, it will override
     * the environment variable.
     */
    public static final String MASTER_FILE_ENV = "HUGO_MASTER_FILE";

    /**
     * Name of the system property that defines the absolute path
     * name for the Hugo master symbol table. If not defined, the
     * environment variable {@code HUGO_MASTER_FILE} will be used 
     * by default.
     */
    public static final String MASTER_FILE_PROPERTY = "jam.hugo.masterFile";

    /**
     * Creates a new HUGO master from a collection of records.
     *
     * @param collection the individual HUGO records.
     *
     * @throws RuntimeException unless all HUGO symbols are unique.
     */
    public HugoMaster(Collection<HugoRecord> collection) {
        this.aliases = HashMultimap.create();
        this.records = new HashMap<HugoSymbol, HugoRecord>(collection.size());

        addRecords(collection);
        addAliases(collection);
    }

    private void addRecords(Collection<HugoRecord> collection) {
        for (HugoRecord record : collection)
            addRecord(record);
    }

    private void addRecord(HugoRecord record) {
        addUnique(record.getHugoSymbol(), record);

        for (HugoSymbol alias : record.viewAliases())
            addUnique(alias, record);
    }

    private void addUnique(HugoSymbol key, HugoRecord record) {
        if (records.containsKey(key))
            throw JamException.runtime("Duplicate HUGO symbol: [%s].", key.getKey());
        else
            records.put(key, record);
    }

    private void addAliases(Collection<HugoRecord> collection) {
        for (HugoRecord record : collection)
            if (record.hasAliases())
                addAliases(record);
    }

    private void addAliases(HugoRecord record) {
        List<HugoSymbol> symbols = new ArrayList<HugoSymbol>();

        symbols.add(record.getHugoSymbol());
        symbols.addAll(record.viewAliases());

        for (int j = 0; j < symbols.size(); ++j)
            for (int k = 0; k < symbols.size(); ++k)
                if (j != k)
                    aliases.put(symbols.get(j), symbols.get(k));
    }

    /**
     * Returns the global master table defined by system properties or
     * environment variables.
     *
     * @return the global master table defined by system properties or
     * environment variables.
     */
    public static HugoMaster global() {
        if (global == null)
            global = load(resolveMasterFile());

        return global;
    }

    private static String resolveMasterFile() {
        if (JamProperties.isSet(MASTER_FILE_PROPERTY))
            return JamProperties.getRequired(MASTER_FILE_PROPERTY);
        else
            return JamEnv.getRequired(MASTER_FILE_ENV);
    }

    /**
     * Loads the HUGO symbol mappings from a data file.
     *
     * @param masterFile the master file to load.
     *
     * @return the master table of HUGO symbol mappings.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static HugoMaster load(File masterFile) {
        return new HugoMaster(HugoLoader.load(masterFile));
    }

    /**
     * Loads the HUGO symbol mappings from a data file.
     *
     * @param masterFileName the name of the master file to load.
     *
     * @return the master table of HUGO symbol mappings.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static HugoMaster load(String masterFileName) {
        return load(new File(masterFileName));
    }

    /**
     * Identifies HUGO symbols contained in this table.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return {@code true} iff this table contains the specified HUGO
     * symbol.
     */
    public boolean contains(HugoSymbol symbol) {
        return records.containsKey(symbol);
    }

    /**
     * Returns the known aliases for a given HUGO symbol.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return a read-only view of the aliases for the specified
     * symbol (an empty collection if this table does not contain
     * the symbol or if it has no aliases).
     */
    public Collection<HugoSymbol> getAliases(HugoSymbol symbol) {
        return Collections.unmodifiableCollection(aliases.get(symbol));
    }

    /**
     * Returns the Ensembl gene identifier corresponding to a given
     * HUGO symbol.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return the Ensembl gene identifier corresponding to the
     * specified symbol (or {@code null} if this table does not
     * contain the symbol).
     */
    public EnsemblGene getGene(HugoSymbol symbol) {
        HugoRecord record = lookup(symbol);

        if (record != null)
            return record.getEnsemblGene();
        else
            return null;
    }

    /**
     * Returns the record for a given HUGO symbol.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return the record corresponding to the specified symbol
     * ({@code null} if there is no matching record).
     */
    public HugoRecord lookup(HugoSymbol symbol) {
        return records.get(symbol);
    }
}
