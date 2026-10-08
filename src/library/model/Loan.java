package library.model;

import java.time.LocalDateTime;

public class Loan {
    private final int id;
    private final int bookId;
    private final int memberId;
    private final LocalDateTime loanedAt;
    private LocalDateTime returnedAt;

    public Loan(int id, int bookId, int memberId, LocalDateTime loanedAt, LocalDateTime returnedAt) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanedAt = loanedAt;
        this.returnedAt = returnedAt;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDateTime getLoanedAt() {
        return loanedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }
}