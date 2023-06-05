package com.spotdraft.pdfmanagment.service;

import com.spotdraft.pdfmanagment.model.Comment;
import com.spotdraft.pdfmanagment.model.PdfFile;
import com.spotdraft.pdfmanagment.model.User;
import com.spotdraft.pdfmanagment.repository.CommentRepository;
import com.spotdraft.pdfmanagment.repository.PdfFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PdfFileService {
    private final CommentRepository commentRepository;
    private PdfFileRepository pdfFileRepository;

    private CommentService commentService;

    public PdfFileService(PdfFileRepository pdfFileRepository, CommentRepository commentRepository, CommentService commentService) {
        this.pdfFileRepository = pdfFileRepository;
        this.commentRepository=commentRepository;
        this.commentService = commentService;
    }

    public List<PdfFile> getAllPdfFiles() {
        return pdfFileRepository.findAll();
    }

    public PdfFile savePdfFile(PdfFile pdfFile) {
        return pdfFileRepository.save(pdfFile);
    }
    public List<PdfFile> searchPdfFilesByFileName(String searchKeyword) {
        return pdfFileRepository.findByFileNameContainingIgnoreCase(searchKeyword);
    }

    public PdfFile getPdfFileById(Long fileId) {
        PdfFile pdfFile = pdfFileRepository.findById(fileId).orElse(null);

//        if (pdfFile != null) {
//            List<Comment> comments = pdfFile.getComments();
//
//            for (Comment comment : comments) {
//                Optional<Comment> replyOptional = commentService.getRepliesForComment(comment.getId());
//
//                if (replyOptional.isPresent()) {
//                    Comment reply = replyOptional.get();
//                    comment.setReplies(Collections.singletonList(reply));
//                } else {
//                    comment.setReplies(Collections.emptyList());
//                }
//            }
//        }

        return pdfFile;
    }




//    public Optional<PdfFile> getPdfFileById(Long id) {
//        return pdfFileRepository.findById(id);
//    }

//    public List<PdfFile> searchPdfFilesByName(String searchKeyword) {
//        return pdfFileRepository.findByFileNameContainingIgnoreCase(searchKeyword);
//    }
    public void deletePdfFile(PdfFile pdfFile) {
        pdfFileRepository.delete(pdfFile);
    }

    public List<PdfFile> getPdfFilesByUser(User user) {
        return pdfFileRepository.findByUser(user);
    }

    public PdfFile findByUniqueLink(String uniqueLink) {
        return pdfFileRepository.findByUniqueLink(uniqueLink);
    }

    public Optional<PdfFile> findById(Long pdfFileId) {
        return pdfFileRepository.findById(pdfFileId);
    }

    public void addCommentToPDF(Comment comment, Long pdfId) {
        Optional<PdfFile> pdfFileOptional = pdfFileRepository.findById(pdfId);

        if (comment != null && pdfFileOptional.isPresent()) {
            PdfFile pdfFile = pdfFileOptional.get();
            commentRepository.save(comment);
            pdfFile.addComment(comment); // Add comment to the PDF file
        } else {
            // Handle the case when the comment is null or the PDF file is not found
            // throw an exception, log an error, or perform any other desired action
            throw new IllegalArgumentException("PDF file not found or comment is null");
        }
    }


}
