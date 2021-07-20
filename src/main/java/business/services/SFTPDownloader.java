package business.services;

import com.jcraft.jsch.*;
import properties.PropertiesCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

public abstract class SFTPDownloader {

    private static final String REMOTE_HOST;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final String SFTP_WORKING_DIR;
    private static final String KNOWN_HOSTS;
    public static final String LOCAL_DIRECTORY;
    private static final int PORT;
    private static final String PATH_SEPARATOR;

    static{
        PropertiesCache properties = PropertiesCache.getInstance();
        REMOTE_HOST = properties.getProperty("remoteHost");
        USERNAME = properties.getProperty("serverUserName");
        PASSWORD = properties.getProperty("serverPassword");
        SFTP_WORKING_DIR = properties.getProperty("sftpWorkingDir");
        KNOWN_HOSTS = properties.getProperty("knownHosts");
        //TODO: Remove this!
        LOCAL_DIRECTORY = properties.getProperty("localDirectory");
        PORT = Integer.parseInt(properties.getProperty("port"));
        PATH_SEPARATOR = properties.getProperty("pathSeparator");
    }

    private SFTPDownloader(){}

    private static Session setupJsch() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(KNOWN_HOSTS);
        Session jschSession = jsch.getSession(USERNAME, REMOTE_HOST, PORT);
        jschSession.setPassword(PASSWORD);
        jschSession.connect();
        return jschSession;
    }

    public static void downloadFiles(String localDirectoryPath) throws FileNotFoundException, JSchException, SftpException {
            makeLocalDirectory(localDirectoryPath);
            Session jschSession = setupJsch();
            ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(SFTP_WORKING_DIR);
            downloadFromFolder(channelSftp, SFTP_WORKING_DIR, localDirectoryPath);
            channelSftp.exit();
            jschSession.disconnect();
    }

    private static void makeLocalDirectory(String path) throws FileNotFoundException {
        if(new File(path).exists()) return;

        boolean properlyMade = new File(path).mkdir();
        if(!properlyMade) throw new FileNotFoundException();
    }

    private static void downloadFromFolder(ChannelSftp channelSftp, String folder, String localDirectoryPath) throws SftpException {
        Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(folder);
        System.out.println(entries);

        for (ChannelSftp.LsEntry en : entries){
            if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                continue;
            }
            System.out.println(en.getFilename());
            channelSftp.get(folder + PATH_SEPARATOR + en.getFilename(), localDirectoryPath + PATH_SEPARATOR + en.getFilename());
        }
    }
}
