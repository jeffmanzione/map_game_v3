package parsers;

public class Module {
	public String id;
	public enum Type {
		INT, DOUBLE, BOOL, RGB, STRING, MOD, VP
	}
	
	public Type type;
	
	public Object ref;
	
	public Module (String id, Type type, Object ref) {
		this.id = id;
		this.type = type;
		this.ref = ref;
	}
}
