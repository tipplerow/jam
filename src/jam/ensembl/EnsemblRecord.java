
package jam.ensembl;

import jam.fasta.FastaRecord;
import jam.peptide.HugoSymbol;
import jam.peptide.Peptide;

public final class EnsemblRecord {
    private final Peptide peptide;
    private final HugoSymbol hugoKey;
    private final EnsemblGene geneKey;
    private final EnsemblProtein proteinKey;
    private final EnsemblTranscript transcriptKey;
    private final TranscriptBiotype transcriptBiotype;

    private EnsemblRecord(Peptide peptide,
                          HugoSymbol hugoKey,
                          EnsemblGene geneKey,
                          EnsemblProtein proteinKey,
                          EnsemblTranscript transcriptKey,
                          TranscriptBiotype transcriptBiotype) {
        this.peptide = peptide;
        this.hugoKey = hugoKey;
        this.geneKey = geneKey;
        this.proteinKey = proteinKey;
        this.transcriptKey = transcriptKey;
        this.transcriptBiotype = transcriptBiotype;
    }

    public static EnsemblRecord parse(FastaRecord fastaRecord) {
        String fastaKey = fastaRecord.getKey();
        String headerLine = fastaRecord.getComment();

        HugoSymbol hugoKey = EnsemblHugo.parseHeader(headerLine).hugoSymbol();
        EnsemblGene geneKey = EnsemblGene.parseHeader(headerLine);
        EnsemblProtein proteinKey = EnsemblProtein.parseKey(fastaKey);
        EnsemblTranscript transcriptKey = EnsemblTranscript.parseHeader(headerLine);
        TranscriptBiotype transcriptBiotype = TranscriptBiotype.parseHeader(headerLine);

        return new EnsemblRecord(fastaRecord.getPeptide(), hugoKey, geneKey,
                                 proteinKey, transcriptKey, transcriptBiotype);
    }

    public EnsemblGene getEnsemblGene() {
        return geneKey;
    }

    public EnsemblProtein getEnsemblProtein() {
        return proteinKey;
    }

    public EnsemblTranscript getEnsemblTranscript() {
        return transcriptKey;
    }

    public HugoSymbol getHugoSymbol() {
        return hugoKey;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public TranscriptBiotype getTranscriptBiotype() {
        return transcriptBiotype;
    }
}
