package edu.ycp.cs.netcoder.server.compilers;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.ProtectionDomain;
import java.security.SecurityPermission;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class ProtectionDomainTest
{

    //@Test
    //public void testProtectionDomain() throws Exception {
    public static void main(String[] args) throws Exception {
        String name="edu.ycp.cs.netcoder.server.compilers.Run";
        String s=IOUtils.toString(new FileInputStream("junit/"+name.replace('.','/')+".java"));
        OnTheFlyCompiler flyCompiler=new OnTheFlyCompiler();
        CompileResult res=flyCompiler.compile(name, s);
        if (!res.success) {
            throw new CompilationException(res);
        }
        Class<?> theClass=flyCompiler.findClass(name);
        
        System.out.println("Classloader: "+theClass.getClassLoader());
        ProtectionDomain pd=theClass.getProtectionDomain();
        
        System.out.println("protection domain: "+pd);
        System.out.println("code source: "+pd.getCodeSource());
        System.out.println("permissions: "+pd.getPermissions());
        
        System.out.println("Can we System.exit()? "+
                pd.implies(new RuntimePermission("exitVM")));
        System.out.println("Can we foobar? "+
                pd.implies(new RuntimePermission("foobar")));
        System.out.println("Can we create access control context? "+
                pd.implies(new SecurityPermission("createAccessControlContext")));
        
        if (pd.getPermissions()!=null) {
            Enumeration<Permission> en=pd.getPermissions().elements();

            while (en.hasMoreElements()) {
                Permission p=en.nextElement();
                System.out.println("Permission: "+p);
            }
        }
        
        AccessController.checkPermission(new RuntimePermission("exitVM"));
        
        Object o=theClass.newInstance();
        Method m=theClass.getMethod("testtest1");
        m.invoke(o);
        
        
    }
}
