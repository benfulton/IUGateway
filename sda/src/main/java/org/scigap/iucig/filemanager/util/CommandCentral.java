package org.scigap.iucig.filemanager.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swithana on 3/24/14.
 */
public class CommandCentral {
    private static final Logger log = LoggerFactory.getLogger(CommandCentral.class);
    private List<String> result;

    public String pwd(Session session) throws Exception {

        if (!session.isConnected()) {
            return null;
        }
        result = new ArrayList<String>();
        String path = "";
        Channel channel = null;
        InputStream in = null;
        try {
            channel = session.openChannel("exec");
            String command = "pwd";
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    if (new String(tmp, 0, i).equals("\n")) break;
                    path = (new String(tmp, 0, i - 1));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    log.error("Error occured while channel connect", ee.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error occured while opening channel", e.getMessage());
            e.printStackTrace();
        } catch (JSchException e) {
            log.error("Auth failure", e.getMessage());
            e.printStackTrace();
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (!channel.isClosed()) {
                channel.disconnect();
            }
            in.close();
            session.disconnect();
        }
        return path;
    }

    public List<String> executeCommand(Session session, String command) throws Exception {
        //FIXME  validate the second part of the command
        result = new ArrayList<String>();
        log.info("COMMAND: " + command);

        if (!session.isConnected()) {
            return null;
        }

        Channel channel = null;
        InputStream in = null;
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    result.add(new String(tmp, 0, i));
                    System.out.println(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    log.error("Error occured while channel connect", ee.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error occured while opening channel", e.getMessage());
            throw new Exception(e.getMessage());
        } catch (JSchException e) {
            log.error("Auth failure", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
            in.close();
        }
        return result;
    }

    public InputStream scpFrom(Session session, String filename) throws Exception {
        result = new ArrayList<String>();
        log.info("Downloading file: " + filename);

        Channel channel = null;
        InputStream in = null;
        FileOutputStream fos = null;
        String lfile = filename;
        String command = "scp -f " + filename;
        String prefix = null;
        if (new File(lfile).isDirectory()) {
            prefix = lfile + File.separator;
        }
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            in = channel.getInputStream();
            channel.connect();
            byte[] buf = new byte[1024];
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }
                // read '0644 '
                int read = in.read(buf, 0, 5);
                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        break;
                    }
                    if (buf[0] == ' ') break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }
                String file = null;
                for (int i = 0; ; i++) {
                    int read1 = in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }
                //System.out.println("filesize="+filesize+", file="+file);

              /*  // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                fos = new FileOutputStream(prefix == null ? filename : prefix + file);
                int foo;
                while (true) {
                    if (buf.length < filesize) foo = buf.length;
                    else foo = (int) filesize;
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) break;
                }
                fos.close();
                fos = null;

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();*/
            }

        } catch (FileNotFoundException e1) {
            log.error("Unable to find the file", e1.getMessage());
            throw new Exception(e1.getMessage());
        } catch (JSchException e1) {
            log.error("Auth failure", e1.getMessage());
            throw new Exception(e1.getMessage());
        } catch (IOException e1) {
            log.error("Error occured", e1.getMessage());
            throw new Exception(e1.getMessage());
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
            fos.close();
        }
        return in;
    }

    public void scpTo(Session session, String filepath,File uploadedFile) {
        FileInputStream fis = null;
        try {

            String lfile = "";
            String rfile = filepath;

            // exec 'scp -t rfile' remotely
            String command = "scp " + " -t " + rfile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if (checkAck(in) != 0) {
                System.exit(0);
            }

            File _lfile = uploadedFile;

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = _lfile.length();
            command = "C0644 " + filesize + " ";
            if (lfile.lastIndexOf('/') > 0) {
                command += lfile.substring(lfile.lastIndexOf('/') + 1);
            } else {
                command += lfile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }

            // send a content of lfile
            fis = new FileInputStream(uploadedFile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) break;
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }
            out.close();

            channel.disconnect();
            session.disconnect();

            System.exit(0);
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fis != null) fis.close();
            } catch (Exception ee) {
            }
        }
    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }
}
