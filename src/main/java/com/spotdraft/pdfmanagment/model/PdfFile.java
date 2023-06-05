package com.spotdraft.pdfmanagment.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pdf_files")
public class PdfFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    private byte[] data;

    @Column(unique = true)
    private String uniqueLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "pdfFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Constructors, getters, and setters

    public PdfFile() {
    }

    public PdfFile(String fileName, String fileType, byte[] data, User user) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUniqueLink() {
        return uniqueLink;
    }

    public void setUniqueLink(String uniqueLink) {
        this.uniqueLink = uniqueLink;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPdfFile(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPdfFile(null);
    }

    // Other methods

    @Override
    public String toString() {
        return "PdfFile{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", data=" + Arrays.toString(data) +
                ", user=" + user +
                '}';
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null || getClass() != obj.getClass()) {
//            return false;
//        }
//        PdfFile pdfFile = (PdfFile) obj;
//        return Objects.equals(id, pdfFile.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
}
