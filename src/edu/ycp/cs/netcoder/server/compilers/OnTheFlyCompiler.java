// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.ycp.cs.netcoder.server.compilers;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

public class OnTheFlyCompiler extends SecureClassLoader
{
    private JavaCompiler compiler;
    private MemoryFileManager memoryFileManager;
    // Store the classpath separately, since it needs to be converted into 
    // foo.jar:bar.jar format.  The compile() method folds classpath into
    // the optionsList.
    private List<String> classpath;
    private List<String> optionsList;
    private Map<String, CodeSource> codeSourceMap;

    public OnTheFlyCompiler() {
        super(OnTheFlyCompiler.class.getClassLoader()); 
        this.compiler=ToolProvider.getSystemJavaCompiler();
        this.memoryFileManager=new MemoryFileManager(this.compiler);
        this.classpath=new LinkedList<String>();
        this.optionsList=new LinkedList<String>();
        this.codeSourceMap=new HashMap<String, CodeSource>();
        addToClasspath(System.getProperty("java.class.path"));

        // Will we need to hard-code some paths?  sort of like the next line
        //addToClasspath("war/WEB-INF/lib/junit.jar");
    }

    public void addToClasspath(String path) {
        classpath.add(path);
    }

    private String getClasspath() {
        StringBuffer buf=new StringBuffer();
        for (String s : classpath) {
            buf.append(s);
            buf.append(":");
        }
        if (buf.length()>0) {
            buf.replace(buf.length()-1, buf.length(), "");
        }
        return buf.toString();
    }

    public CompileResult compile(String className, String programText)
    {
        Source source = new Source(className, Kind.SOURCE, programText);

        // set compiler's classpath to be same as the runtime's
        optionsList.addAll(Arrays.asList("-classpath",getClasspath()));
        //XXX Debug
        //System.out.println("Classpath: "+getClasspath());

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(source);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        CompilationTask compileTask = compiler.getTask(null, 
                memoryFileManager, 
                diagnostics, 
                optionsList, 
                null, 
                compilationUnits);

        Boolean success = compileTask.call();

        return new CompileResult(success, diagnostics);
    }

    static class Source extends SimpleJavaFileObject {
        private final String content;

        Source(String name, Kind kind, String content) {
            super(URI.create("memo:///" + name.replace('.', '/') + kind.extension), kind);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignore) {
            return this.content;
        }
    }

    static class Output extends SimpleJavaFileObject {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Output(String name, Kind kind) {
            super(URI.create("memo:///" + name.replace('.', '/') + kind.extension), kind);
        }

        byte[] toByteArray() {
            return this.baos.toByteArray();
        }

        @Override
        public ByteArrayOutputStream openOutputStream() {
            return this.baos;
        }
    }

    static class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, Output> map = new HashMap<String, Output>();

        MemoryFileManager(JavaCompiler compiler) {
            super(compiler.getStandardFileManager(null, null, null));
        }

        boolean contains(String name) {
            return map.containsKey(name);
        }

        Output get(String name) {
            if (map.containsKey(name)) {
                return map.get(name);
            }
            return null;
        }

        @Override
        public Output getJavaFileForOutput (Location location, 
                String name, 
                Kind kind, 
                FileObject source)
        {
            Output mc = new Output(name, kind);
            this.map.put(name, mc);
            return mc;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        synchronized (memoryFileManager) {
            //System.out.println("Fly classloader is loading: "+name);
            Output mc = memoryFileManager.get(name);
            if (mc != null) {
                byte[] array = mc.toByteArray();
                /*
                if (!codeSourceMap.containsKey(name)) {
                    try {
                        codeSourceMap.put(name, 
                                new CodeSource(
                                        new URL("file:///foo.bar.bazzle"), 
                                        //new URL("file:///" + name), 
                                        //new URL("file:///" + name.replace('.', '/')), 
                                        new Certificate[0]));
                    } catch (MalformedURLException e){
                        throw new ClassNotFoundException("Bad URL", e);
                    }
                }
                //defineClass(name, array, 0, array.length, )
                //return defineClass(name, array, 0, array.length, codeSourceMap.get(name));
                PermissionCollection pc=getPermissions(codeSourceMap.get(name));
                // Add the permissions
                //Permission p=new RuntimePermission("exitVM");
                //pc.add(p);
                
                //Permission p2=new RuntimePermission("accessClassInPackage."+name+"$StudentTest");
                //Permission p2=new RuntimePermission("accessClassInPackage.edu.ycp.cs.netcoder.server.compilers");
                Permission p2=new RuntimePermission("accessClassInPackage.edu/ycp/cs/netcoder/server/compilers");
                pc.add(p2);
                
                ProtectionDomain pd=new ProtectionDomain(codeSourceMap.get(name), pc, this, null);
                return defineClass(name, array, 0, array.length, pd);
                */
                
                return defineClass(name, array, 0, array.length);
            }
        }
        //System.out.println("Asking parent classloader for "+name);
        return super.findClass(name);
    }
}
