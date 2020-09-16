
package jam.app;

import java.io.File;

/**
 * Locates the top-level directory of the {@code JAM} library.
 */
public final class JamHome {
    private JamHome() {}

    /**
     * The name of the top-level directory of the {@code JAM} library.
     */
    public static final String NAME = System.getenv("JAM_HOME");

    /**
     * The top-level directory of the {@code JAM} library.
     */
    public static final File DIR = new File(NAME);
}
