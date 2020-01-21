
package jam.maf;

import java.util.ArrayList;
import java.util.List;

import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoSymbol;
import jam.io.IOUtil;
import jam.io.TableReader;
import jam.peptide.ProteinChange;
import jam.tcga.TumorBarcode;

/**
 * Reads MAF files and extracts the missense mutation records.
 */
public final class MissenseParser {
    private final String mafFile;
    private final List<MissenseRecord> records = new ArrayList<MissenseRecord>();

    private TableReader reader;

    private int tumorBarcodeIndex;
    private int hugoSymbolIndex;
    private int transcriptIndex;
    private int classificationIndex;
    private int variantTypeIndex;
    private int proteinChangeIndex;

    private List<String> fields;

    private HugoSymbol hugoSymbol;
    private VariantType variantType;
    private TumorBarcode tumorBarcode;
    private ProteinChange proteinChange;
    private EnsemblTranscript transcriptID;
    private VariantClassification classification;

    private MissenseParser(String mafFile) {
        this.mafFile = mafFile;
    }

    /**
     * Reads a MAF file and extracts the missense mutations.
     *
     * @param mafFile the name of the MAF file to process.
     *
     * @return all missense mutation records in the specified MAF
     * file.
     *
     * @throws RuntimeException if any I/O or parsing errors occur.
     */
    public static List<MissenseRecord> parse(String mafFile) {
        MissenseParser parser = new MissenseParser(mafFile);
        return parser.parse();
    }

    private List<MissenseRecord> parse() {
        reader = TableReader.open(mafFile);

        try {
            tumorBarcodeIndex   = reader.requireColumn(MAFProperties.resolveTumorBarcodeColumnName());
            hugoSymbolIndex     = reader.requireColumn(MAFProperties.resolveHugoSymbolColumnName());
            transcriptIndex     = reader.requireColumn(MAFProperties.resolveTranscriptColumnName());
            classificationIndex = reader.requireColumn(MAFProperties.resolveClassificationColumnName());
            variantTypeIndex    = reader.requireColumn(MAFProperties.resolveVariantTypeColumnName());
            proteinChangeIndex  = reader.requireColumn(MAFProperties.resolveProteinChangeColumnName());

            while (reader.hasNext())
                processLine();
        }
        finally {
            IOUtil.close(reader);
        }

        return records;
    }

    private void processLine() {
        List<String> fields = reader.next();

        VariantType variantType = VariantType.valueOf(fields.get(variantTypeIndex));
        VariantClassification classification = VariantClassification.requireCode(fields.get(classificationIndex));

        if (!variantType.equals(VariantType.SNP))
            return;

        if (!classification.equals(VariantClassification.MISSENSE))
            return;

        HugoSymbol hugoSymbol = HugoSymbol.instance(fields.get(hugoSymbolIndex));
        TumorBarcode tumorBarcode = TumorBarcode.instance(fields.get(tumorBarcodeIndex));
        ProteinChange proteinChange = ProteinChange.parse(fields.get(proteinChangeIndex));
        EnsemblTranscript transcriptID = EnsemblTranscript.instance(fields.get(transcriptIndex));

        records.add(new MissenseRecord(tumorBarcode, transcriptID, hugoSymbol, proteinChange));
    }
}
