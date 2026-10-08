package library.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import library.model.*;
import library.repository.*;
import library.util.IdFormatter;

public class LibraryService {
    private BookRepository bookRepo;
    private MemberRepository memberRepo;
    private LoanRepository loanRepo;
    private GenreRepository genreRepo;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LibraryService(BookRepository bookRepo, MemberRepository memberRepo, LoanRepository loanRepo,
            GenreRepository genreRepo) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
        this.loanRepo = loanRepo;
        this.genreRepo = genreRepo;
    }

    public void lendBook(int bookId, int memberId) {
        Book book = bookRepo.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }
        Member member = memberRepo.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }
        int leftCopies = book.getTotalCopies() - countLoanedCopies(bookId);
        if (leftCopies <= 0) {
            throw new IllegalStateException("No copies available");
        }
        int loanId = nextLoanId();

        Loan loan = new Loan(loanId, bookId, memberId, LocalDateTime.now(), null);
        loanRepo.add(loan);
    }

    public void returnBook(int loanId) {
        Loan loan = loanRepo.findById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found");
        }
        if (loan.getReturnedAt() != null) {
            throw new IllegalStateException("Book is already returned");
        }
        loan.setReturnedAt(LocalDateTime.now());
        loanRepo.update(loan);
    }

    public int countLoanedCopies(int bookId) {
        int count = 0;
        List<Loan> loans = loanRepo.findByBookId(bookId);
        for (Loan l : loans) {
            if (l.getReturnedAt() == null) {
                count++;
            }
        }
        return count;
    }

    public void deleteBook(int bookId) {
        Book book = bookRepo.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }
        List<Loan> loans = loanRepo.findActive();
        for (Loan l : loans) {
            if (l.getBookId() == bookId) {
                throw new IllegalStateException("Book is currently loaned");
            }
        }
        if (!loanRepo.findByBookId(bookId).isEmpty()) {
            throw new IllegalStateException("Book has loan history");
        }
        bookRepo.delete(bookId);
    }

    public void deleteMember(int memberId) {
        Member member = memberRepo.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }
        List<Loan> loans = loanRepo.findByMemberId(memberId);
        for (Loan l : loans) {
            if (l.getReturnedAt() == null) {
                throw new IllegalStateException("Member has active loans");
            }
        }
        if (!loans.isEmpty()) {
            throw new IllegalStateException("Member has loan history");
        }
        memberRepo.delete(memberId);
    }

    public void deleteGenre(int genreId) {
        Genre genre = genreRepo.findById(genreId);
        if (genre == null) {
            throw new IllegalArgumentException("Genre not found");
        }
        List<Book> books = bookRepo.findAll();
        for (Book b : books) {
            if (b.getGenreId() == genreId) {
                throw new IllegalStateException("Genre is in use");
            }
        }
        genreRepo.delete(genreId);
    }

    public int nextBookId() {
        int max = 0;
        for (Book b : bookRepo.findAll()) {
            if (b.getId() > max) {
                max = b.getId();
            }
        }
        return max + 1;
    }

    public int nextMemberId() {
        int max = 0;
        for (Member m : memberRepo.findAll()) {
            if (m.getId() > max) {
                max = m.getId();
            }
        }
        return max + 1;
    }

    public int nextGenreId() {
        int max = 0;
        for (Genre g : genreRepo.findAll()) {
            if (g.getId() > max) {
                max = g.getId();
            }
        }
        return max + 1;
    }

    private int nextLoanId() {
        int max = 0;
        for (Loan l : loanRepo.findAll()) {
            if (l.getId() > max) {
                max = l.getId();
            }
        }
        return max + 1;
    }

    public void saveAll() throws IOException {
        bookRepo.saveToFile();
        memberRepo.saveToFile();
        genreRepo.saveToFile();
        loanRepo.saveToFile();
    }

    public String[] getBookRowString(int bookId) {
        Book book = bookRepo.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }
        Genre genre = genreRepo.findById(book.getGenreId());

        String displayId = IdFormatter.formatBookId(bookId, genre.getGenrePrefix());
        String[] row = {
                displayId,
                book.getTitle(),
                book.getAuthor(),
                genre.getGenreName(),
                String.valueOf(book.getTotalCopies()),
                String.valueOf(countLoanedCopies(bookId))
        };
        return row;
    }

    public List<String[]> getAllBookRows() {
        List<String[]> rows = new ArrayList<>();
        List<Book> books = bookRepo.findAll();
        for (Book b : books) {
            rows.add(getBookRowString(b.getId()));
        }
        return rows;
    }

    public Book findBookById(int id) {
        Book book = bookRepo.findById(id);
        return book;
    }

    public List<Book> findAllBook() {
        List<Book> books = bookRepo.findAll();
        return books;
    }

    public List<String[]> searchBookRows(String title, String author, Genre genre) {
        List<String[]> rows = new ArrayList<>();
        List<Book> books = bookRepo.findAll();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(title.toLowerCase())
                    && b.getAuthor().toLowerCase().contains(author.toLowerCase())
                    && (genre == null || b.getGenreId() == genre.getId())) {
                rows.add(getBookRowString(b.getId()));
            }
        }
        return rows;
    }

    public void addBook(Book book) {
        bookRepo.add(book);
    }

    public void updateBook(Book book) {
        bookRepo.update(book);
    }

    public List<String[]> getAllGenreRows() {
        List<String[]> rows = new ArrayList<>();
        List<Genre> genres = genreRepo.findAll();
        for (Genre g : genres) {
            String[] row = {
                    String.valueOf(g.getId()),
                    g.getGenreName(),
                    g.getGenrePrefix()
            };
            rows.add(row);
        }
        return rows;
    }

    public Genre findGenreById(int id) {
        Genre genre = genreRepo.findById(id);
        return genre;
    }

    public Genre findGenreByPrefix(String prefix) {
        Genre genre = genreRepo.findByPrefix(prefix);
        return genre;
    }

    public Genre findGenreByExactName(String name) {
        List<Genre> genres = genreRepo.findAll();
        for (Genre g : genres) {
            if (g.getGenreName().equals(name)) {
                return g;
            }
        }
        return null;
    }

    public List<Genre> findAllGenre() {
        List<Genre> genres = genreRepo.findAll();
        return genres;
    }

    public void addGenre(Genre genre) {
        genreRepo.add(genre);
    }

    public void updateGenre(Genre genre) {
        genreRepo.update(genre);
    }

    public String[] getMemberRowString(int memberId) {
        Member member = memberRepo.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }

        String displayId = IdFormatter.formatMemberId(memberId);
        String[] row = {
                displayId,
                member.getName()
        };
        return row;
    }

    public List<String[]> getAllMemberRows() {
        List<String[]> rows = new ArrayList<>();
        List<Member> members = memberRepo.findAll();
        for (Member m : members) {
            String[] row = {
                    IdFormatter.formatMemberId(m.getId()),
                    m.getName()
            };
            rows.add(row);
        }
        return rows;
    }

    public Member findMemberById(int id) {
        Member member = memberRepo.findById(id);
        return member;
    }

    public List<Member> findAllMember() {
        List<Member> member = memberRepo.findAll();
        return member;
    }

    public String[] getLoanRowString(int loanId) {
        Loan loan = loanRepo.findById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found");
        }

        String displayId = IdFormatter.formatLoanId(loanId);
        Book book = bookRepo.findById(loan.getBookId());
        Genre bookGenre = genreRepo.findById(book.getGenreId());
        String bookDisplay = book.getTitle() + " (" + IdFormatter.formatBookId(book.getId(), bookGenre.getGenrePrefix())
                + ")";

        Member member = memberRepo.findById(loan.getMemberId());
        String memberDisplay = member.getName() + " (" + IdFormatter.formatMemberId(member.getId()) + ")";

        String loanedAtStr = loan.getLoanedAt().format(DATE_FMT);
        String returnedAtStr = loan.getReturnedAt() == null ? "" : loan.getReturnedAt().format(DATE_FMT);

        String[] row = {
                displayId,
                bookDisplay,
                memberDisplay,
                loanedAtStr,
                returnedAtStr
        };
        return row;
    }

    public List<String[]> searchLoanRows(String title, String name, boolean activeOnly) {
        List<String[]> rows = new ArrayList<>();
        List<Loan> loans = loanRepo.findAll();
        for (Loan l : loans) {
            if (activeOnly && l.getReturnedAt() != null) {
                continue;
            }
            Book book = bookRepo.findById(l.getBookId());
            Member member = memberRepo.findById(l.getMemberId());
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())
                    && member.getName().toLowerCase().contains(name.toLowerCase())) {
                rows.add(getLoanRowString(l.getId()));
            }
        }
        return rows;
    }

    public void addMember(Member member) {
        memberRepo.add(member);
    }

    public void updateMember(Member member) {
        memberRepo.update(member);
    }

    public List<String[]> getAllLoanRows() {
        List<String[]> rows = new ArrayList<>();
        List<Loan> loans = loanRepo.findAll();
        for (Loan l : loans) {
            Book book = bookRepo.findById(l.getBookId());
            Genre bookGenre = genreRepo.findById(book.getGenreId());
            String bookDisplay = book.getTitle() + " ("
                    + IdFormatter.formatBookId(book.getId(), bookGenre.getGenrePrefix()) + ")";

            Member member = memberRepo.findById(l.getMemberId());
            String memberDisplay = member.getName() + " (" + IdFormatter.formatMemberId(member.getId()) + ")";

            String loanedAtStr = l.getLoanedAt().format(DATE_FMT);
            String returnedAtStr = l.getReturnedAt() == null ? "" : l.getReturnedAt().format(DATE_FMT);
            String[] row = {
                    IdFormatter.formatLoanId(l.getId()),
                    bookDisplay,
                    memberDisplay,
                    loanedAtStr,
                    returnedAtStr
            };
            rows.add(row);
        }
        return rows;
    }

    public List<String[]> searchMemberRows(String name) {
        List<String[]> rows = new ArrayList<>();
        List<Member> members = memberRepo.findAll();
        for (Member m : members) {
            if (m.getName().toLowerCase().contains(name.toLowerCase())) {
                rows.add(getMemberRowString(m.getId()));
            }
        }
        return rows;
    }

    public Loan findLoanById(int id) {
        Loan loan = loanRepo.findById(id);
        return loan;
    }
}