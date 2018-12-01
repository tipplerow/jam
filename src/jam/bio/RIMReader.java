
package jam.bio;

import java.io.File;

import jam.app.JamHome;
import jam.io.LineReader;
import jam.lang.JamException;
import jam.matrix.JamMatrix;
import jam.util.RegexUtil;

final class RIMReader {
    private final File file;
    private final JamMatrix matrix;

    private RIMReader(File file) {
        this.file = file;
        this.matrix = JamMatrix.square(Residue.countNative());
    }

    static JamMatrix readSparse(File file) {
        RIMReader reader = new RIMReader(file);
        return reader.readSparse();
    }

    private JamMatrix readSparse() {
        LineReader reader = LineReader.open(file);

        try {
            for (String line : reader)
                parseLine(line);
        }
        finally {
            reader.close();
        }

        return matrix;
    }

    private void parseLine(String line) {
        line = line.trim();

        if (line.isEmpty())
            return;

        String[] fields = RegexUtil.split(RegexUtil.COMMA, line, 3);

        Residue res1 = Residue.valueOf(fields[0]);
        Residue res2 = Residue.valueOf(fields[1]);
        double  bind = Double.parseDouble(fields[2]);

        int index1 = res1.ordinal();
        int index2 = res2.ordinal();

        matrix.set(index1, index2, bind);
        matrix.set(index2, index1, bind);
    }        
}
