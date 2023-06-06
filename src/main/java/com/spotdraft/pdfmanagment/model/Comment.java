package com.spotdraft.pdfmanagment.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PdfFile pdfFile;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "text")
    private String text;

    public Comment() {
    }

    /**
     * Creates a new Comment with the provided PdfFile, User, and text.
     *
     * @param pdfFile The PdfFile associated with the comment.
     * @param user    The User who made the comment.
     * @param text    The content of the comment.
     */
    public Comment(PdfFile pdfFile, User user, String text) {
        this.pdfFile = pdfFile;
        this.user = user;
        this.text = text;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PdfFile getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(PdfFile pdfFile) {
        this.pdfFile = pdfFile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
