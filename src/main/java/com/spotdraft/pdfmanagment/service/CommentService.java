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

    /**
     * Saves a comment.
     *
     * @param comment The comment to be saved.
     * @return The saved comment.
     */
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    /**
     * Deletes a comment.
     *
     * @param comment The comment to be deleted.
     */
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    /**
     * Retrieves a comment by its ID.
     *
     * @param commentId The ID of the comment.
     * @return The comment if found, otherwise null.
     */
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    /**
     * Retrieves the replies for a comment by its ID.
     *
     * @param commentId The ID of the comment.
     * @return Optional containing the list of replies if found, otherwise an empty optional.
     */
    public Optional<Comment> getRepliesForComment(Long commentId) {
        // Retrieve the replies for the given commentId from the database or any other data source
        // Return the list of replies
        // Here's a sample implementation using a fictional repository class

        return commentRepository.findById(commentId);
    }
}
