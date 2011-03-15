package edu.ycp.cs.netcoder.client;

public enum AceEditorMode {
	JAVA("java");
	
	private final String name;
	
	private AceEditorMode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
