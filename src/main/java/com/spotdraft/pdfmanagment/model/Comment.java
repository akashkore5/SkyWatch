package com.spotdraft.pdfmanagment.model;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PdfFile pdfFile;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "parent_id")
//     private Comment parent;


//     @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
//     private List<Comment> replies = new ArrayList<>();



    private LocalDateTime timestamp;
    public Comment() {
    }

    public Comment(PdfFile pdfFile, User user, String text) {
        this.pdfFile = pdfFile;
        this.user = user;
        this.text = text;
    }

    private String text;

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
//     public List<Comment> getReplies() {
//         return replies;
//     }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

//     public void addReply(Comment reply) {
//         replies.add(reply);
//         reply.setParent(this);
//     }

//     public void removeReply(Comment reply) {
//         replies.remove(reply);
//         reply.setParent(null);
//     }


//     public Comment getParent() {
//         return parent;
//     }

//     public void setParent(Comment parent) {
//         this.parent = parent;
//     }

//     public void setReplies(List<Comment> replies) {
//         this.replies=replies;
//     }

    // Constructors, getters, and setters
}
