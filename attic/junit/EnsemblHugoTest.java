
package jam.junit;

import jam.ensembl.EnsemblHugo;
import jam.hugo.HugoSymbol;

import org.junit.*;
import static org.junit.Assert.*;

public class EnsemblHugoTest {
    private static final String HEADER_LINE =
        "ENSP00000495858.1 pep chromosome:GRCh38:7:140833972:140924742:-1 gene:ENSG00000157764.13"
        + " transcript:ENST00000469930.2 gene_biotype:protein_coding transcript_biotype:protein_coding"
        + " gene_symbol:BRAF description:B-Raf proto-oncogene, serine/threonine kinase [Source:HGNC Symbol;Acc:HGNC:1097]";

    @Test public void testParseHeader() {
        HugoSymbol hugoID = EnsemblHugo.parseHeader(HEADER_LINE);
        assertEquals("BRAF", hugoID.getKey());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EnsemblHugoTest");
    }
}
