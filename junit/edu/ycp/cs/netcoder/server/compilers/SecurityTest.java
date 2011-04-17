package edu.ycp.cs.netcoder.server.compilers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SecurityTest {
    public static void main(String[] args)
        throws FileNotFoundException {
        //Is there a SecurityManger registered?
        System.out.println("SecurityManager: " +
            System.getSecurityManager());

        //Checking if we can open a file for reading
        FileInputStream fis = new FileInputStream("test.txt");
        System.out.println("File successfully opened");

        //Checking if we can access a vm property
        System.out.println(System.getProperty("file.encoding"));
    }
}
