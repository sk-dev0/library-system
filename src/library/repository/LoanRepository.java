package library.repository;

import java.util.*;

import java.io.IOException;
import java.time.LocalDateTime;

import library.model.Loan;
import library.util.TsvIO;

public class LoanRepository {
    private List<Loan> loans = new ArrayList<>();
    private String filePath;

    public LoanRepository(String filePath) {
        this.filePath = filePath;
    }

    public void loadFromFile() throws IOException {
        loans.clear();
        List<String> lines = TsvIO.readLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\t", -1);
            int id = Integer.parseInt(parts[0]);
            int bookId = Integer.parseInt(parts[1]);
            int memberId = Integer.parseInt(parts[2]);
            LocalDateTime loanedAt = LocalDateTime.parse(parts[3]);
            LocalDateTime returnedAt;
            if (parts.length < 5 || parts[4].isEmpty()) {
                returnedAt = null;
            } else {
                returnedAt = LocalDateTime.parse(parts[4]);
            }

            Loan loan = new Loan(id, bookId, memberId, loanedAt, returnedAt);
            loans.add(loan);
        }
    }

    public void saveToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            String returnedAtStr = l.getReturnedAt() == null ? "" : l.getReturnedAt().toString();
            String line = l.getId() + "\t"
                    + l.getBookId() + "\t"
                    + l.getMemberId() + "\t"
                    + l.getLoanedAt().toString() + "\t"
                    + returnedAtStr;
            lines.add(line);
        }
        TsvIO.writeLines(filePath, lines);
    }

    public void add(Loan loan) {
        loans.add(loan);
    }

    public List<Loan> findAll() {
        return loans;
    }

    public Loan findById(int id) {
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public List<Loan> findByBookId(int bookId) {
        List<Loan> result = new ArrayList<>();
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.getBookId() == bookId) {
                result.add(l);
            }
        }
        return result;
    }

    public List<Loan> findByMemberId(int memberId) {
        List<Loan> result = new ArrayList<>();
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.getMemberId() == memberId) {
                result.add(l);
            }
        }
        return result;
    }

    public List<Loan> findActive() {
        List<Loan> result = new ArrayList<>();
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.getReturnedAt() == null) {
                result.add(l);
            }
        }
        return result;
    }

    public void delete(int id) {
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.getId() == id) {
                loans.remove(i);
                return;
            }
        }
    }

    public void update(Loan loan) {
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.getId() == loan.getId()) {
                loans.set(i, loan);
                return;
            }
        }
    }
}
