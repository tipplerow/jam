
package jam.hla;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jam.io.LineReader;
import jam.lang.JamException;
import jam.tcga.PatientID;
import jam.util.RegexUtil;

final class GenotypeReader {
    private final LineReader reader;
    private final Map<PatientID, Genotype> genotypes = new HashMap<PatientID, Genotype>();

    private GenotypeReader(LineReader reader) {
        this.reader = reader;
    }

    static Map<PatientID, Genotype> read(String fileName) {
        return read(LineReader.open(fileName));
    }

    static Map<PatientID, Genotype> read(File file) {
        return read(LineReader.open(file));
    }
        
    static Map<PatientID, Genotype> read(LineReader reader) {
        return new GenotypeReader(reader).read();
    }

    private Map<PatientID, Genotype> read() {
        try {
            skipHeader();

            for (String line : reader)
                parseLine(line);
        }
        finally {
            reader.close();
        }

        return genotypes;
    }

    private void skipHeader() {
        //
        // Read but ignore the header line...
        //
        reader.next();
    }

    private void parseLine(String line) {
        String[] columns = RegexUtil.split(GenotypeFile.PATIENT_GENOTYPE_DELIM, line, 2);

        PatientID patientID = PatientID.instance(columns[0]);
        Genotype  genotype  = Genotype.parse(columns[1], GenotypeFile.ALLELE_ALELE_DELIM);

        if (genotypes.containsKey(patientID))
            throw JamException.runtime("Duplicate patient ID: [%s]", patientID.getKey());

        genotypes.put(patientID, genotype);
    }
}
