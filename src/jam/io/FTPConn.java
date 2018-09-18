
package jam.io;

import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import jam.app.JamLogger;
import jam.app.JamProperties;

/**
 * Creates an FTP connection and provides methods to transfer files.
 */
public final class FTPConn {
    private final FTPClient client;

    private FTPConn(FTPClient client) {
        this.client = client;
    }

    /**
     * Name of the system property that defines the email address to
     * submit for anonymous FTP login.
     */
    public static final String EMAIL_ADDRESS_PROPERTY = "jam.ftp.email";

    /**
     * Creates a new anonymous FTP connection with a remote server.
     *
     * @param serverAddr the address of the remote server.
     *
     * @return the open FTP connection.
     *
     * @throws IOException unless the connection can be established.
     */
    public static FTPConn anonymous(String serverAddr) throws IOException {
        return open(serverAddr, "anonymous", resolveEmail());
    }

    private static String resolveEmail() {
        return JamProperties.getOptional(EMAIL_ADDRESS_PROPERTY, getDefaultEmail());
    }

    private static String getDefaultEmail() {
        return System.getProperty("user.name");
    }

    /**
     * Creates a new FTP connection with a remote server.
     *
     * @param serverAddr the address of the remote server.
     *
     * @param userName the user name required for login.
     *
     * @param password the password required for login.
     *
     * @return the open FTP connection.
     *
     * @throws IOException unless the connection can be established.
     */
    public static FTPConn open(String serverAddr, String userName, String password) throws IOException {
        FTPClient client = new FTPClient();

        client.connect(serverAddr);
        JamLogger.info(client.getReplyString());

        int reply = client.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new IOException("FTP server refused connection.");
        }

        client.login(userName, password);
        JamLogger.info(client.getReplyString());
            
        return new FTPConn(client);
    }

    /**
     * Closes the FTP connection.
     */
    public void close() {
        try {
            if (client.isConnected())
                client.disconnect();
        }
        catch (IOException ex) {
            JamLogger.warn("Failed to disconnect FTP client.");
        }
    }

    /**
     * Downloads a file over this connection.
     *
     * @param remoteFile the name of the remote file to retrieve.
     *
     * @param localFile the name of the local file to create.
     *
     * @throws IOException unless the download is successful.
     */
    public void download(String remoteFile, String localFile) throws IOException {
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));

        client.enterLocalPassiveMode();
        client.retrieveFile(remoteFile, outputStream);

        JamLogger.info(client.getReplyString());
        outputStream.close();
    }

    public static void main(String[] args) throws IOException {
        FTPConn conn = anonymous("ftp.uniprot.org");

        try {
            conn.download("/pub/databases/uniprot/current_release/README", "README");
        }
        finally {
            conn.close();
        }
    }
}
