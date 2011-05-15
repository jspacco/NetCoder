package edu.ycp.cs.netcoder.server.compilers;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class JythonTest
{
    String body;
    
    @Before
    public void setup() 
    throws IOException
    {
        body=IOUtils.toString(new FileInputStream("test-files/sq.py"));
    }
    
    @Test
    public void testInterpreter() throws Exception {
        PythonInterpreter terp=new PythonInterpreter();
        //PyCode pyCode=terp.compile(body);
        terp.exec(body);
        terp.exec("res=sq(4)");
        PyObject res=terp.get("res");
        System.out.println(res);
        
    }
}
