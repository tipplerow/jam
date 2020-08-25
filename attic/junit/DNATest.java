
package jam.junit;

import jam.chr.Chromosome;
import jam.nucleic.DNA;
import jam.nucleic.Nucleotide;

import org.junit.*;
import static org.junit.Assert.*;

public class DNATest {
    private static final Nucleotide A = Nucleotide.A;
    private static final Nucleotide T = Nucleotide.T;
    private static final Nucleotide G = Nucleotide.G;
    private static final Nucleotide C = Nucleotide.C;
    private static final Nucleotide U = Nucleotide.U;

    @Test public void testChr21() {
        Chromosome chr = Chromosome.Chr21;
        
        if (!chr.ensemblFile().exists())
            return;

        DNA dna = chr.dna();
        System.out.println(dna.length());

        System.out.println(dna.at(39817346));
        System.out.println(dna.at(39817347));
        System.out.println(dna.at(39817348));
        System.out.println(dna.at(39817349));
        System.out.println(dna.at(39817350));
        System.out.println();
        System.out.println(dna.at(34127593));
        System.out.println(dna.at(34127594));
        System.out.println(dna.at(34127595));
        System.out.println(dna.at(34127596));
        System.out.println(dna.at(34127597));
        System.out.println();
        System.out.println(dna.at(46687508));
        System.out.println(dna.at(46687509));
        System.out.println(dna.at(46687510));
        System.out.println(dna.at(46687511));
        System.out.println(dna.at(46687512));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DNATest");
    }
}
