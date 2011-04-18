package edu.ycp.cs.netcoder.shared.problems;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.assertEquals;
import edu.ycp.cs.netcoder.server.problems.StudentCodeSecurityManager;
public class Run {
    private static class StudentTest {
        public int sq(int x) { if (x==1) {System.out.println("return 17"); return 17; }if (x==2) {System.out.println("throw exception"); throw new RuntimeException(); }if (x==3) {System.out.println("Infinite loop!"); while (true);}if (x==4) {System.out.println("Try to spawn thread"); new Thread() {public void run() {while(true);}}.start();}if (x==5) {System.out.println("Try to call System.exit(1)"); System.exit(1);}if (x==6) { return (int)Math.pow(x, 2); }if (x==7) {System.setSecurityManager(null);}if (x==8) {StudentCodeSecurityManager sman=(StudentCodeSecurityManager)System.getSecurityManager(); }System.out.println("Message in output");return x*x; }
    }
    @Test
    public void testtest1() throws Exception {
        SecurityManager originalSecurityManager=System.getSecurityManager();
        StudentCodeSecurityManager.SandboxBooleanContainer container=new StudentCodeSecurityManager.SandboxBooleanContainer();
        StudentCodeSecurityManager sman=new StudentCodeSecurityManager(container);
        container.enableSandbox();System.setSecurityManager(sman);
        try {
            StudentTest theInstance=new StudentTest();
            assertEquals("input:<8>", 64, theInstance.sq(8));
        } finally {
            container.disableSandbox();
            System.setSecurityManager(originalSecurityManager);
        }
    }
}

