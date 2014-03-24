package org.scigap.iucig.filemanager;

import com.jcraft.jsch.Session;
import org.scigap.iucig.filemanager.util.CommandCentral;
import org.scigap.iucig.filemanager.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);


    private KerberosConnector kerberosConnector;
    private CommandCentral commandCentral;
    private StringUtils stringUtils;
    private List<String> result;
    private static List<String> path;
    private List<String> commandList;
    private static String workingDirectory;

    public CommandExecutor() {
        kerberosConnector = new KerberosConnector();
        commandCentral = new CommandCentral();
        stringUtils = new StringUtils();

        //get the current working directory
        pwd();
    }

    public void executeCommand(String command) {

        Session session = kerberosConnector.getSession();

        commandList = stringUtils.deconstructCommand(command);

        if (commandList.get(0).equals("cd")) {
            workingDirectory = workingDirectory + "/" + commandList.get(1);
            command = "ls " + workingDirectory;
            log.info("COMMAND: "+command);
            setResult(commandCentral.executeCommand(session, command));
        }
    }
    public void pwd() {
        Session session = kerberosConnector.getSession();
        String path = commandCentral.pwd(session);
        workingDirectory = stringUtils.constructPathString(stringUtils.deconstructPath(path));
        log.info("CURRENT WORKING DIR: "+workingDirectory);
        log.info("CURRENT PATH: "+path.toString());
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
       this.path = path;
    }
}
