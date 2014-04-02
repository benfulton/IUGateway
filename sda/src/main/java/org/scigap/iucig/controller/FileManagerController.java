package org.scigap.iucig.controller;

import org.apache.commons.io.IOUtils;
import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Item;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping(value = "/filemanager/")
public class FileManagerController {

    private static CommandExecutor commandExecutor;

    /**
     * Returns the result of a command using a Item list
     */
    @ResponseBody
    @RequestMapping(value = "/command/{command}", method = RequestMethod.GET)
    public List<Item> executeCommand(@PathVariable(value = "command") final String command, @QueryParam("user") String user) {
        if (commandExecutor == null) {
            commandExecutor = new CommandExecutor(user);
        }
        commandExecutor.executeCommand(command);
        return commandExecutor.getResultItemList();
    }

    /**
     * Download a file
     */
    @ResponseBody
    @RequestMapping(value = "/download/{filename}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "filename") final String filename, @QueryParam("user") String user, HttpServletResponse response) {
        if (commandExecutor == null) {
            commandExecutor = new CommandExecutor(user);
        }
        InputStream in = commandExecutor.downloadFile(filename);
        try {
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
