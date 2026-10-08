package library.repository;

import java.util.*;
import java.io.IOException;

import library.model.Genre;
import library.util.TsvIO;;

public class GenreRepository {
    private List<Genre> genres = new ArrayList<>();
    private String filePath;

    public GenreRepository(String filePath) {
        this.filePath = filePath;
    }

    public void loadFromFile() throws IOException {
        genres.clear();
        List<String> lines = TsvIO.readLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\t");
            int id = Integer.parseInt(parts[0]);
            String genreName = parts[1];
            String genrePrefix = parts[2];

            Genre genre = new Genre(id, genreName, genrePrefix);
            genres.add(genre);
        }
    }

    public void saveToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < genres.size(); i++) {
            Genre g = genres.get(i);
            String line = g.getId() + "\t"
                        + g.getGenreName() + "\t"
                        + g.getGenrePrefix();
            lines.add(line);
        }
        TsvIO.writeLines(filePath, lines);
    }

    public void add(Genre genre) {
        genres.add(genre);
    }

    public List<Genre> findAll() {
        return genres;
    }

    public Genre findById(int id) {
        for (int i = 0; i < genres.size(); i++) {
            Genre g = genres.get(i);
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    public List<Genre> findByName(String genre) {
        List<Genre> result = new ArrayList<>();
        for (int i = 0; i < genres.size(); i++) {
            Genre g = genres.get(i);
            if (g.getGenreName().contains(genre)) {
                result.add(g);
            }
        } 
        return result;
    }

    public Genre findByPrefix(String prefix) {
        for (int i = 0; i < genres.size(); i++) {
            Genre g = genres.get(i);
            if (g.getGenrePrefix().equals(prefix)) {
                return g;
            }
        }
        return null;
    }

    public void delete(int id) {
        for (int i = 0; i < genres.size(); i++) {
            Genre g = genres.get(i);
            if (g.getId() == id) {
                genres.remove(i);
                return;
            }
        }
    }

    public void update(Genre genre) {
        for (int i = 0; i < genres.size(); i++) {
            Genre g = genres.get(i);
            if (g.getId() == genre.getId()) {
                genres.set(i, genre);
                return;
            }
        }
    }
}
