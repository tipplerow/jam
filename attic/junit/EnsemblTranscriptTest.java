
package jam.junit;

import jam.ensembl.EnsemblTranscript;

import org.junit.*;
import static org.junit.Assert.*;

public class EnsemblTranscriptTest {
    private static final String HEADER_LINE =
        "ENSP00000495858.1 pep chromosome:GRCh38:7:140833972:140924742:-1 gene:ENSG00000157764.13"
        + " transcript:ENST00000469930.2 gene_biotype:protein_coding transcript_biotype:protein_coding"
        + " gene_symbol:BRAF description:B-Raf proto-oncogene, serine/threonine kinase [Source:HGNC Symbol;Acc:HGNC:1097]";

    @Test public void testParseHeader() {
        EnsemblTranscript transcriptID = EnsemblTranscript.parseHeader(HEADER_LINE);
        assertEquals("ENST00000469930", transcriptID.getKey());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EnsemblTranscriptTest");
    }
}
