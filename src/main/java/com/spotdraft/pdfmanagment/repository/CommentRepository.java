package com.spotdraft.pdfmanagment.repository;

import com.spotdraft.pdfmanagment.model.Comment;
import com.spotdraft.pdfmanagment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment save(Comment comment);

}

