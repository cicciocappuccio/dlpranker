package dataset;

public class ITreeNode {

	private String label;
	private int level;
	
	public ITreeNode(String label, int level) {
		this.setLabel(label);
		this.setLevel(level);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "Node [label=" + label + ", level=" + level + "]";
	}
		
}
