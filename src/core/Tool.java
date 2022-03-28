package core;

public class Tool {

	public enum ToolMode {Select, Move, Angle};
	public ToolMode toolMode;
	public int actionKey;
	
	public Tool(ToolMode toolMode, int actionKey) {
		this.toolMode = toolMode;
		this.actionKey = actionKey;
	}
}
