
package jam.hugo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.ensembl.EnsemblGene;
import jam.io.TableReader;
import jam.util.RegexUtil;

final class HugoLoader {
    private final File file;
    private final TableReader reader;
    private final List<HugoRecord> records;

    private int hugoColumn;
    private int aliasColumn;
    private int ensemblColumn;

    private HugoLoader(File file, TableReader reader) {
        this.file = file;
        this.reader = reader;
        this.records = new ArrayList<HugoRecord>();
    }

    static Collection<HugoRecord> load(File file) {
        TableReader reader = TableReader.open(file);
        HugoLoader  loader = new HugoLoader(file, reader);

        try {
            loader.load();
        }
        finally {
            reader.close();
        }

        return loader.records;
    }

    private void load() {
        hugoColumn = reader.requireColumn(HugoRecord.HUGO_SYMBOL_NAME);
        aliasColumn = reader.requireColumn(HugoRecord.ALIAS_SYMBOLS_NAME);
        ensemblColumn = reader.requireColumn(HugoRecord.ENSEMBL_GENE_NAME);

        for (List<String> fields : reader)
            parseFields(fields);
    }

    private void parseFields(List<String> fields) {
        HugoSymbol hugoSymbol = HugoSymbol.instance(fields.get(hugoColumn));
        EnsemblGene ensemblGene = EnsemblGene.instance(fields.get(ensemblColumn));
        Set<HugoSymbol> aliasSymbols = parseAliases(fields.get(aliasColumn));

        records.add(new HugoRecord(hugoSymbol, ensemblGene, aliasSymbols));
    }

    private Set<HugoSymbol> parseAliases(String aliasField) {
        if (aliasField.length() < 1)
            return Collections.emptySet();

        String[] aliasKeys = RegexUtil.split(HugoRecord.ALIAS_DELIMITER, aliasField);

        if (aliasKeys.length < 1)
            return Collections.emptySet();

        Set<HugoSymbol> aliasSet = new TreeSet<HugoSymbol>();

        for (String aliasKey : aliasKeys)
            aliasSet.add(HugoSymbol.instance(aliasKey));

        return aliasSet;
    }
}
