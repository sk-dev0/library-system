package library.repository;

import java.util.*;
import java.io.IOException;

import library.model.Book;
import library.util.TsvIO;

public class BookRepository {
    private List<Book> books = new ArrayList<>();
    private String filePath;

    public BookRepository(String filePath) {
        this.filePath = filePath;
    }

    public void loadFromFile() throws IOException {
        books.clear();
        List<String> lines = TsvIO.readLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\t");
            int id = Integer.parseInt(parts[0]);
            String title = parts[1];
            String author = parts[2];
            int genreId = Integer.parseInt(parts[3]);
            int totalCopies = Integer.parseInt(parts[4]);

            Book book = new Book(id, title, author, genreId, totalCopies);
            books.add(book);
        }
    }

    public void saveToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            String line = b.getId() + "\t"
                        + b.getTitle() + "\t"
                        + b.getAuthor() + "\t"
                        + b.getGenreId() + "\t"
                        + b.getTotalCopies();
            lines.add(line);
        }
        TsvIO.writeLines(filePath, lines);
    }

    public void add(Book book) {
        books.add(book);
    }

    public List<Book> findAll() {
        return books;
    }

    public Book findById(int id) {
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            if (b.getId() == id) {
                return b;
            }
        } 
        return null;
    }

    public List<Book> findByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            if (b.getTitle().contains(title)) {
                result.add(b);
            }
        } 
        return result;
    }

    public List<Book> findByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            if (b.getAuthor().contains(author)) {
                result.add(b);
            }
        } 
        return result;
    }

    public void delete(int id) {
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            if (b.getId() == id) {
                books.remove(i);
                return;
            }
        } 
    }

    public void update(Book book) {
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            if (b.getId() == book.getId()) {
                books.set(i, book);
                return;
            }
        }
    }
}