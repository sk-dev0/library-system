package library.model;

public class Book {
    private final int id;
    private String title;
    private String author;
    private int genreId;
    private int totalCopies;

    public Book(int id, String title, String author, int genreId, int totalCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genreId = genreId;
        this.totalCopies = totalCopies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
}