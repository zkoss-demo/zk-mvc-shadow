package zk.example.foreach;

public class ConfigElement {
	private String name;
	private boolean editable;

	public ConfigElement(String name, boolean editable) {
		this.name = name;
		this.editable = editable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEditable() {
		return editable;
	}

	public String toString() {
		return "ConfigElement: " + getName();
	}
}
