
package jam.peptide;

abstract class AbstractPeptide implements Peptide {
    private int hashCode = UNSET_HASH_CODE;

    private static final int UNSET_HASH_CODE = 0;
    
    @Override public boolean equals(Object obj) {
        return (obj instanceof Peptide) && equalsPeptide((Peptide) obj);
    }

    private boolean equalsPeptide(Peptide that) {
        int thisLength = this.length();
        int thatLength = that.length();

        if (thisLength != thatLength)
            return false;

        for (int k = 0; k < thisLength; ++k)
            if (!this.at(k).equals(that.at(k)))
                return false;

        return true;
    }

    @Override public int hashCode() {
        if (hashCode == UNSET_HASH_CODE)
            hashCode = viewResidues().hashCode();

        return hashCode;
    }

    @Override public String toString() {
        return "Peptide(" + formatString() + ")";
    }
}
