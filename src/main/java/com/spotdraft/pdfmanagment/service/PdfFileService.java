package com.spotdraft.pdfmanagment.service;

import com.spotdraft.pdfmanagment.model.Comment;
import com.spotdraft.pdfmanagment.model.PdfFile;
import com.spotdraft.pdfmanagment.model.User;
import com.spotdraft.pdfmanagment.repository.CommentRepository;
import com.spotdraft.pdfmanagment.repository.PdfFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing PDF files.
 */
@Service
public class PdfFileService {
    private final CommentRepository commentRepository;
    private PdfFileRepository pdfFileRepository;

    private CommentService commentService;

    public PdfFileService(PdfFileRepository pdfFileRepository, CommentRepository commentRepository, CommentService commentService) {
        this.pdfFileRepository = pdfFileRepository;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    /**
     * Retrieves all PDF files.
     *
     * @return the list of all PDF files
     */
    public List<PdfFile> getAllPdfFiles() {
        return pdfFileRepository.findAll();
    }

    /**
     * Saves a PDF file.
     *
     * @param pdfFile the PDF file to save
     * @return the saved PDF file
     */
    public PdfFile savePdfFile(PdfFile pdfFile) {
        return pdfFileRepository.save(pdfFile);
    }

    /**
     * Searches PDF files by filename.
     *
     * @param searchKeyword the search keyword to match against the filename
     * @return the list of matching PDF files
     */
    public List<PdfFile> searchPdfFilesByFileName(String searchKeyword) {
        return pdfFileRepository.findByFileNameContainingIgnoreCase(searchKeyword);
    }

    /**
     * Retrieves a PDF file by its ID.
     *
     * @param fileId the ID of the PDF file to retrieve
     * @return the PDF file, or null if not found
     */
    public PdfFile getPdfFileById(Long fileId) {
        return pdfFileRepository.findById(fileId).orElse(null);
    }

    /**
     * Deletes a PDF file.
     *
     * @param pdfFile the PDF file to delete
     */
    public void deletePdfFile(PdfFile pdfFile) {
        pdfFileRepository.delete(pdfFile);
    }

    /**
     * Retrieves PDF files by user.
     *
     * @param user the user associated with the PDF files
     * @return the list of PDF files associated with the user
     */
    public List<PdfFile> getPdfFilesByUser(User user) {
        return pdfFileRepository.findByUser(user);
    }

    /**
     * Finds a PDF file by its unique link.
     *
     * @param uniqueLink the unique link of the PDF file
     * @return the PDF file, or null if not found
     */
    public PdfFile findByUniqueLink(String uniqueLink) {
        return pdfFileRepository.findByUniqueLink(uniqueLink);
    }

    /**
     * Retrieves a PDF file by its ID.
     *
     * @param pdfFileId the ID of the PDF file to retrieve
     * @return the PDF file, or an empty optional if not found
     */
    public Optional<PdfFile> findById(Long pdfFileId) {
        return pdfFileRepository.findById(pdfFileId);
    }

    /**
     * Adds a comment to a PDF file.
     *
     * @param comment the comment to add
     * @param pdfId   the ID of the PDF file
     * @throws IllegalArgumentException if the comment is null or the PDF file is not found
     */
    public void addCommentToPDF(Comment comment, Long pdfId) {
        Optional<PdfFile> pdfFileOptional = pdfFileRepository.findById(pdfId);

        if (comment != null && pdfFileOptional.isPresent()) {
            PdfFile pdfFile = pdfFileOptional.get();
            commentRepository.save(comment);
            pdfFile.addComment(comment); // Add comment to the PDF file
        } else {
            // Handle the case when the comment is null or the PDF file is not found
            // Throw an exception, log an error, or perform any other desired action
            throw new IllegalArgumentException("PDF file not found or comment is null");
        }
    }
}
