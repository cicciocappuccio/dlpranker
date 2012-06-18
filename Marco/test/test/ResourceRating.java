package test;

public class ResourceRating {
	private String uri;
	private int rating;

	public ResourceRating(String uri, int rating) {
		this.uri = uri;
		this.rating = rating;
	}
	public ResourceRating() {

	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getUri() {
		return uri;
	}

	public int getRating() {
		return rating;
	}
}
