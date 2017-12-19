package adviser.movies;

public class Movie {
	
	private String title;
	private String category;
	private String producer;
	
	public Movie() {
		// TODO Auto-generated constructor stub
	}

	public Movie(String title, String category, String producer) {
		this.title = title;
		this.category = category;
		this.producer = producer;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", category=" + category + ", producer=" + producer + "]";
	}
	
	

}
