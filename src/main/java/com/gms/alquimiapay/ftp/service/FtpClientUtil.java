package com.gms.alquimiapay.ftp.service;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.ftp.payload.data.FtpCredentials;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;


@Slf4j
public class FtpClientUtil {

    private static final FTPClient ftpClient =  new FTPClient();
    private static FtpCredentials ftpCredentials;

    private FtpClientUtil(){}

    public static void startFtpServer(FtpCredentials credentials){
        ftpCredentials = credentials;
    }

    /**
     * Saves a file to the remote FTP server in the resolved remote path.
     * @param inputStream: InputStream
     * @param directory: String
     * @return isSaved: boolean
     */
    public static boolean saveFile(@NotNull InputStream inputStream, @NonNull String directory, @NonNull String filename) throws IOException {
        connectToRemoteServer();
        boolean isDirectoryExist  = ftpClient.changeWorkingDirectory(resolveRemoteDirectory(directory));
        int replyCode = ftpClient.getReplyCode();
        if(!isDirectoryExist || replyCode == 550){
            ftpClient.makeDirectory(resolveRemoteDirectory(directory));
        }
        boolean fileStored = ftpClient.storeFile(resolveRemoteFile(directory, filename), inputStream);
        inputStream.close();
        cleanupConnectionSession();
        return fileStored;
    }

    /**
     * Connects to the remote FTP server with the provided credentials.
     */
    private static void connectToRemoteServer(){
        try {
            ftpClient.connect(ftpCredentials.getServerHostName(), ftpCredentials.getServerPort());
            showServerReply(ftpClient);
            ftpClient.login(ftpCredentials.getUsername(), ftpCredentials.getPassword());
            showServerReply(ftpClient);
            ftpClient.enterLocalPassiveMode();
            log.info("Successfully connected and logged in to remote server.");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Exception while trying to connect and login to remote server: {}", e.getMessage());
        }
    }

    private static void cleanupConnectionSession() throws IOException {
        ftpClient.logout();
        showServerReply(ftpClient);
        ftpClient.disconnect();
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("FTP SERVER: " + aReply);
            }
        }
    }

    private static String resolveRemoteDirectory(String directory){
        if(directory.endsWith(StringValues.FORWARD_SLASH))
            return directory.substring(0, directory.lastIndexOf(StringValues.FORWARD_SLASH));
        return directory;
    }

    private static String resolveRemoteFile(String parentDir, String filename){
        String directory = parentDir;
        if(!parentDir.endsWith(StringValues.FORWARD_SLASH))
            directory = parentDir.concat(StringValues.FORWARD_SLASH);
        return directory.concat(filename);
    }

}
