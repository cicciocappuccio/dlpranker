package dataset;

import org.dllearner.core.owl.Individual;

public class Tupla {
	
	private Individual film;
	private Individual rating;
	private Individual user;
	private Integer value;

	public Tupla(Individual film, Individual rating, Individual user, Integer value) {
		super();
		this.film = film;
		this.rating = rating;
		this.user = user;
		this.value = value;
	}

	public Individual getFilm() {
		return film;
	}

	public void setFilm(Individual film) {
		this.film = film;
	}

	public Individual getRating() {
		return rating;
	}

	public void setRating(Individual rating) {
		this.rating = rating;
	}

	public Individual getUser() {
		return user;
	}

	public void setUser(Individual user) {
		this.user = user;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
