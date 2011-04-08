// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco
// Copyright (C) 2011, David H. Hovemeyer
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

package edu.ycp.cs.netcoder.server;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.CompileService;

public class CompileServiceImpl extends RemoteServiceServlet implements CompileService {
	private static final long serialVersionUID = 1L;

	/**
	 * Class name that we expect the client's code to define -
	 * hard-coded for now.
	 */
	private static final String CLASS_NAME = "NetCoderTest";

	// See:
	//   http://weblogs.java.net/blog/2008/12/17/how-compile-fly
	//   http://www.java2s.com/Code/Java/JDK-6/CompilingfromMemory.htm
	
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

		@Override
		public Output getJavaFileForOutput (Location location, String name, Kind kind, FileObject source) {
			Output mc = new Output(name, kind);
			this.map.put(name, mc);
			return mc;
		}
	}
	
	/*
	static class MemoryClassLoader extends ClassLoader {
	    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    private final MemoryFileManager manager = new MemoryFileManager(this.compiler);

	    public MemoryClassLoader(String classname, String filecontent) {
	        this(Collections.singletonMap(classname, filecontent));
	    }

	    public MemoryClassLoader(Map<String, String> map) {
	        List<Source> list = new ArrayList<Source>();
	        for (Map.Entry<String, String> entry : map.entrySet()) {
	            list.add(new Source(entry.getKey(), Kind.SOURCE, entry.getValue()));
	        }
	        this.compiler.getTask(null, this.manager, null, null, null, list).call();
	    }

	    @Override
	    protected Class<?> findClass(String name) throws ClassNotFoundException {
	        synchronized (this.manager) {
	            Output mc = this.manager.map.remove(name);
	            if (mc != null) {
	                byte[] array = mc.toByteArray();
	                return defineClass(name, array, 0, array.length);
	            }
	        }
	        return super.findClass(name);
	    }
	}
	*/

	@Override
	public Boolean compile(String programText) {
		System.out.println("Request to compile!");
		System.out.println("Code:");
		System.out.println(programText);
		
		Source source = new Source(CLASS_NAME, Kind.SOURCE, programText);
		
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(source);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		MemoryFileManager fileManager = new MemoryFileManager(compiler);
		CompilationTask compileTask = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
		
		Boolean success = compileTask.call();

		System.out.println("Compile " + (success ? "succeeded" : "failed"));
		
		return success;
	}

}
