package com.spotdraft.pdfmanagment.service;

import com.spotdraft.pdfmanagment.model.Comment;
import com.spotdraft.pdfmanagment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

//    public Comment getCommentById(Long commentId) {
//        return commentRepository.findById(commentId)
//                .orElseThrow(() -> new NotFoundException("Comment not found"));
//    }

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public Optional<Comment> getRepliesForComment(Long commentId) {
        // Retrieve the replies for the given commentId from the database or any other data source
        // Return the list of replies
        // Here's a sample implementation using a fictional repository class

        return commentRepository.findById(commentId);
    }

}
