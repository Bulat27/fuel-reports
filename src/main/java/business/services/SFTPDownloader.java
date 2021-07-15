package business.services;

import com.jcraft.jsch.*;

import java.io.File;
import java.util.Vector;

public abstract class SFTPDownloader {

    private static final String REMOTE_HOST = "fe.ddns.protal.biz";
    private static final String USERNAME = "sftpuser";
    private static final String PASSWORD = "hyperpass";
    private static final String SFTP_WORKING_DIR = "/xml-data";
    private static final String KNOWN_HOSTS = "C:/Users/Dragon/known_hosts";
    private static final String LOCAL_DIRECTORY = "src/main/resources/data";
    private static final int PORT = 22;
    private static final String PATH_SEPARATOR = "/";

    private SFTPDownloader(){}

    private static Session setupJsch() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(KNOWN_HOSTS);
        Session jschSession = jsch.getSession(USERNAME, REMOTE_HOST, PORT);
        jschSession.setPassword(PASSWORD);
        jschSession.connect();
        return jschSession;
    }

    public static void downloadFiles(){
        new File(LOCAL_DIRECTORY).mkdir();

        try {
            Session jschSession = setupJsch();
            ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(SFTP_WORKING_DIR);
            downloadFromFolder(channelSftp, SFTP_WORKING_DIR);
            channelSftp.exit();
            jschSession.disconnect();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
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
