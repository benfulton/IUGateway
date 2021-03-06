package org.scigap.iucig.filemanager.util;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;


public class StringUtilsTester {

    private static StringUtils utils;

    @BeforeClass
    public static void setUp() {
        utils = new StringUtils();
    }

    @Test
    public void testDeconstructPath() {
        Assert.assertEquals("[home, swithana, sachith withana]", utils.deconstructPath("/home/swithana/sachith withana").toString());
    }

    @Test
    public void testContructPathString() {
        Assert.assertEquals("/home/swithana",utils.constructPathString(utils.deconstructPath("/home/swithana/")));
    }

    @Test
    public void testDeconstructCommand() {
        Assert.assertEquals("[cd, temp dir]", utils.deconstructCommand("cd temp dir").toString());
        Assert.assertEquals("[cd, temp_dir]", utils.deconstructCommand("cd temp_dir").toString());
        Assert.assertEquals("[mv, temp_dir, txt.txt]", utils.deconstructCommand("mv temp_dir*txt.txt").toString());
        Assert.assertEquals("[mv, temp dir, txt.txt]", utils.deconstructCommand("mv temp dir*txt.txt").toString());
    }


    @Test
    public void testCategorizeResult() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("-rw-rw-r-- 1 swithana swithana     214 Feb 24 10:14 HelloWorld.java");
        list.add("drwxrwxr-x 2 swithana swithana    4096 Mar 24 03:53 ssd");
        list.add("drwxrwxr-x 2 swithana swithana    4096 Mar 24 03:53 reed");
        list.add("drwxrwxr-x 2 swithana swithana    4096 Mar 24 03:53 sdsd");
        list.add("drwxrwxr-x 2 swithana swithana    4096 Mar 24 03:53 blabla");

        System.out.println(utils.categorizeResult(list));

    }
    @Test
    public void testGetResultItemList() {
        ArrayList<String> list = new ArrayList<String>();

        String input = "-rw-rw-r-- 1 swithana swithana 20000140 Feb 24 10:14 HelloWorld.java\n" +
                "drwxrwxr-x 2 swithana swithana 4096 Mar 24 03:53 ssd\n" +
                "drwxrwxr-x 2 swithana swithana 6 Mar 24 03:53 ssd\n" +
                "drwxrwxr-x 2 swithana swithana 69898834834343 Mar 24 03:53 ssd\n" +
                "drwxrwxr-x 2 swithana swithana 4096000 Mar 24 03:53 reed\n";
        list.add(input);
        
        System.out.println(utils.getResultsList(list));

    }
}
