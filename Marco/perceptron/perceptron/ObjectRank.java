package perceptron;

public class ObjectRank<T> {
	
	public T object;
	public int rank;
	
	public ObjectRank(T object, int rank) {
		this.object = object;
		this.rank = rank;
	}
	
	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}