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
        USERNAME = properties.getProperty("userName");
        PASSWORD = properties.getProperty("password");
        SFTP_WORKING_DIR = properties.getProperty("sftpWorkingDir");
        KNOWN_HOSTS = properties.getProperty("knownHosts");
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

    public static void downloadFiles() throws FileNotFoundException, JSchException, SftpException {
            makeLocalDirectory();
            Session jschSession = setupJsch();
            ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(SFTP_WORKING_DIR);
            downloadFromFolder(channelSftp, SFTP_WORKING_DIR);
            channelSftp.exit();
            jschSession.disconnect();
    }

    private static void makeLocalDirectory() throws FileNotFoundException {
        if(new File(LOCAL_DIRECTORY).exists()) return;

        boolean properlyMade = new File(LOCAL_DIRECTORY).mkdir();
        if(!properlyMade) throw new FileNotFoundException();
    }

    private static void downloadFromFolder(ChannelSftp channelSftp, String folder) throws SftpException {
        Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(folder);
        System.out.println(entries);

        for (ChannelSftp.LsEntry en : entries){
            if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                continue;
            }
            System.out.println(en.getFilename());
            channelSftp.get(folder + PATH_SEPARATOR + en.getFilename(), LOCAL_DIRECTORY + PATH_SEPARATOR + en.getFilename());
        }
    }
}
