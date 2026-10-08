package library.model;

public class Genre {
    private final int id;
    private String genreName;
    private String genrePrefix;

    public Genre(int id, String genreName, String genrePrefix) {
        this.id = id;
        this.genreName = genreName;
        this.genrePrefix = genrePrefix;
    }

    public int getId() {
        return id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getGenrePrefix() {
        return genrePrefix;
    }

    public void setGenrePrefix(String genrePrefix) {
        this.genrePrefix = genrePrefix;
    }

    public String toString() {
        return genreName;
    }
}