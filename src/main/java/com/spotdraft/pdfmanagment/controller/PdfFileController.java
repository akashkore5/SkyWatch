package com.spotdraft.pdfmanagment.controller;

import com.spotdraft.pdfmanagment.model.Comment;
import com.spotdraft.pdfmanagment.model.PdfFile;
import com.spotdraft.pdfmanagment.model.User;
import com.spotdraft.pdfmanagment.repository.CommentRepository;
import com.spotdraft.pdfmanagment.repository.PdfFileRepository;
import com.spotdraft.pdfmanagment.repository.UserRepository;
import com.spotdraft.pdfmanagment.service.CommentService;
import com.spotdraft.pdfmanagment.service.PdfFileService;
import com.spotdraft.pdfmanagment.service.UserService;
import com.spotdraft.pdfmanagment.utility.Utility;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PdfFileController {
    @Autowired
    private JavaMailSender mailSender;
    private UserService userService;
    private UserRepository userRepository;
    private PdfFileRepository pdfFileRepository;
    private PdfFileService pdfFileService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final HttpServletRequest request;

    public PdfFileController(JavaMailSender mailSender, UserService userService, UserRepository userRepository, PdfFileRepository pdfFileRepository, PdfFileService pdfFileService, CommentService commentService, CommentRepository commentRepository, HttpServletRequest request) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.userRepository = userRepository;
        this.pdfFileRepository = pdfFileRepository;
        this.pdfFileService = pdfFileService;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
        this.request = request;
    }



    // handler method to handle list of users
//    @GetMapping("/dashboard")
//    public String users(Model model){
////        List<UserDto> users = userService.findAllUsers();
////        model.addAttribute("users", users);
//        return "users";
//    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal, @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
        // Get the authenticated user
        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        // Get the list of PDF files uploaded by the user
        List<PdfFile> userPdfFiles = pdfFileService.getPdfFilesByUser(user);

        // Add the list of PDF files to the model
        model.addAttribute("userPdfFiles", userPdfFiles);

        // Perform search if searchKeyword is provided
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // Perform search in all PDF files
            List<PdfFile> searchResults = pdfFileService.searchPdfFilesByFileName(searchKeyword);
//            System.out.println("Search result size: " + searchResults.size());

            // Add the search results to the model
            model.addAttribute("searchResults", searchResults);
        }

        // Return the dashboard view
        return "dashboard";
    }




    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Principal principal, RedirectAttributes redirectAttributes) {
        // Validate that the uploaded file is a PDF
        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            // Handle the error, return an error message, or redirect the user to an error page
            redirectAttributes.addFlashAttribute("error", "Please upload a PDF file.");

            return "redirect:/dashboard";
        }

        // Get the authenticated user
        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        try {
            // Create a new PdfFile entity and set its properties
            PdfFile uploadedFile = new PdfFile();
            uploadedFile.setFileName(file.getOriginalFilename());
            uploadedFile.setFileType(file.getContentType());
            uploadedFile.setData(file.getBytes());

            // Generate a unique link
            String uniqueLink = generateUniqueLink();
            uploadedFile.setUniqueLink(uniqueLink);
            uploadedFile.setUser(user);

            // Save the PdfFile entity to the database
            pdfFileRepository.save(uploadedFile);

            // Handle success, return a success message, or redirect the user to a success page
        } catch (IOException e) {
            // Handle the error, return an error message, or redirect the user to an error page

            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading the file.");

            // Return the dashboard view with the error message
            return "dashboard";
        }

        return "redirect:/dashboard";
    }
    public String generateUniqueLink() {
        // Generate a unique random UUID
        UUID uuid = UUID.randomUUID();

        // Convert the UUID to a string
        String uniqueLink = uuid.toString();

        return uniqueLink;
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId, Principal principal) {
        // Get the authenticated user
        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        // Find the file by its ID and check if the user is authorized to download it
        Optional<PdfFile> optionalFile = pdfFileRepository.findById(fileId);
//        if (optionalFile.isEmpty() || !optionalFile.get().getUser().equals(user)) {
//            // Handle the error, return an error message, or redirect the user to an error page
//        }

        // Create a ByteArrayResource with the file data
        PdfFile file = optionalFile.get();
        ByteArrayResource resource = new ByteArrayResource(file.getData());

        // Set the Content-Disposition header to force the browser to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName());

        // Return the file as a ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.getData().length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

//    @GetMapping("/shared/{uniqueLink}")
//    public String viewSharedPdfFile(@PathVariable("uniqueLink") String uniqueLink, Model model) {
//        // Find the PDF file based on the unique link
//        PdfFile pdfFile = pdfFileRepository.findByUniqueLink(uniqueLink);
//
//        // Check if the PDF file exists and is accessible
//        if (pdfFile != null) {
//            // Add the PDF file to the model
//            model.addAttribute("pdfFile", pdfFile);
//
//            // Return the view for viewing the shared file
//            return "viewSharedFile";
//        } else {
//            // Return an error view or redirect to an error page
//            return "error";
//        }
//    }

    @GetMapping("/shared/{uniqueCode}")
    public String viewSharedPdfFile(@PathVariable String uniqueCode, HttpServletRequest request,Model model) {
        HttpSession session = request.getSession();
        String targetUrl = "/shared/" + uniqueCode; // Set the target URL
        session.setAttribute("targetUrl", targetUrl);
        // Check if the user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            // Redirect the user to the login or registration page
//            return "redirect:/login"; // Replace with the appropriate URL
            String loginUrl = "/login?targetUrl=" + UriUtils.encodePath(targetUrl, StandardCharsets.UTF_8);
            return "redirect:" + loginUrl;
        }

        // Get the authenticated user's information
//        String userEmail = authentication.getName(); // Assuming the email is used as the username

        // Retrieve the PDF file by unique code
        PdfFile pdfFile = pdfFileService.findByUniqueLink(uniqueCode);
        if (pdfFile == null) {
            // Handle invalid unique code or PDF file not found

            model.addAttribute("error", "The requested PDF file is not present");
            model.addAttribute("message", "please select valid PDF file from list");
            return "error"; // Replace with the appropriate error page or redirect
        }

        String base64Data = Base64.getEncoder().encodeToString(pdfFile.getData());

        // Pass the base64Data to the viewpdffile.html template
        model.addAttribute("base64Data", base64Data);
        // Add the PDF file to the model
        model.addAttribute("pdfFile", pdfFile);

        // Return the view PDF file template
        return "viewpdffile"; // Replace with the appropriate template name
    }

    public void sendShareEmail(String recipientEmail, String link)
            throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        link = Utility.getSiteURL(request) + "/shared/" + link;

        helper.setFrom("support@spotdraft.com", "PDF Sharing");
        helper.setTo(recipientEmail);

        String subject = "Shared PDF File";

        String content = "<!DOCTYPE html>\n" +
                "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "\t<title></title>\n" +
                "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]--><!--[if !mso]><!-->\n" +
                "\t<link href=\"https://fonts.googleapis.com/css?family=Cabin\" rel=\"stylesheet\" type=\"text/css\"><!--<![endif]-->\n" +
                "\t<style>\n" +
                "\t\t* {\n" +
                "\t\t\tbox-sizing: border-box;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tbody {\n" +
                "\t\t\tmargin: 0;\n" +
                "\t\t\tpadding: 0;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\ta[x-apple-data-detectors] {\n" +
                "\t\t\tcolor: inherit !important;\n" +
                "\t\t\ttext-decoration: inherit !important;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t#MessageViewBody a {\n" +
                "\t\t\tcolor: inherit;\n" +
                "\t\t\ttext-decoration: none;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tp {\n" +
                "\t\t\tline-height: inherit\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t.desktop_hide,\n" +
                "\t\t.desktop_hide table {\n" +
                "\t\t\tmso-hide: all;\n" +
                "\t\t\tdisplay: none;\n" +
                "\t\t\tmax-height: 0px;\n" +
                "\t\t\toverflow: hidden;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t.image_block img+div {\n" +
                "\t\t\tdisplay: none;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t@media (max-width:670px) {\n" +
                "\t\t\t.desktop_hide table.icons-inner {\n" +
                "\t\t\t\tdisplay: inline-block !important;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.icons-inner {\n" +
                "\t\t\t\ttext-align: center;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.icons-inner td {\n" +
                "\t\t\t\tmargin: 0 auto;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.row-content {\n" +
                "\t\t\t\twidth: 100% !important;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.mobile_hide {\n" +
                "\t\t\t\tdisplay: none;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.stack .column {\n" +
                "\t\t\t\twidth: 100%;\n" +
                "\t\t\t\tdisplay: block;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.mobile_hide {\n" +
                "\t\t\t\tmin-height: 0;\n" +
                "\t\t\t\tmax-height: 0;\n" +
                "\t\t\t\tmax-width: 0;\n" +
                "\t\t\t\toverflow: hidden;\n" +
                "\t\t\t\tfont-size: 0px;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.desktop_hide,\n" +
                "\t\t\t.desktop_hide table {\n" +
                "\t\t\t\tdisplay: table !important;\n" +
                "\t\t\t\tmax-height: none !important;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"background-color: #000000; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n" +
                "\t<table class=\"nl-container\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #000000;\">\n" +
                "\t\t<tbody>\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td>\n" +
                "\t\t\t\t\t<table class=\"row row-1\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f3e6f8;\">\n" +
                "\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t<td>\n" +
                "\t\t\t\t\t\t\t\t\t<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #ffffff; background-image: url('https://d1oco4z2z1fhwp.cloudfront.net/templates/default/2971/ResetPassword_BG_2.png'); background-position: center top; background-repeat: no-repeat; color: #000000; width: 650px;\" width=\"650\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-top: 45px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"divider_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"20\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"alignment\" align=\"center\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"divider_inner\" style=\"font-size: 1px; line-height: 1px; border-top: 0px solid #BBBBBB;\"><span>&#8202;</span></td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"image_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"20\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"alignment\" align=\"center\" style=\"line-height:10px\"><img src=\"https://6b0de258b8.imgdist.com/public/users/Integrators/BeeProAgency/1002177_986991/child-s-artwork-in-picture-frame-6941688.jpg\" style=\"display: block; height: auto; border: 0; width: 260px; max-width: 100%;\" width=\"260\" alt=\"Forgot your password?\" title=\"Forgot your password?\"></div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"heading_block block-3\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\" style=\"padding-top:35px;text-align:center;width:100%;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<h1 style=\"margin: 0; color: #8412c0; direction: ltr; font-family: 'Cabin', Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 28px; font-weight: 400; letter-spacing: normal; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">You have received a PDF file.</h1>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"divider_block block-4\" width=\"100%\" border=\"0\" cellpadding=\"20\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"alignment\" align=\"center\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"80%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"divider_inner\" style=\"font-size: 1px; line-height: 1px; border-top: 1px solid #E1B4FC;\"><span>&#8202;</span></td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"text_block block-5\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\" style=\"padding-bottom:10px;padding-left:45px;padding-right:45px;padding-top:10px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div style=\"font-family: Arial, sans-serif\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class style=\"font-size: 12px; font-family: 'Cabin', Arial, 'Helvetica Neue', Helvetica, sans-serif; mso-line-height-alt: 18px; color: #0068a5; line-height: 1.5;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p style=\"margin: 0; text-align: center; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">Click the link below to access the file</span></p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"button_block block-6\" width=\"100%\" border=\"0\" cellpadding=\"10\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"alignment\" align=\"center\"><!--[if mso]><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\""+link+"\" style=\"height:50px;width:223px;v-text-anchor:middle;\" arcsize=\"0%\" strokeweight=\"0.75pt\" strokecolor=\"#8412c0\" fillcolor=\"#8412c0\"><w:anchorlock/><v:textbox inset=\"0px,0px,0px,0px\"><center style=\"color:#ffffff; font-family:Arial, sans-serif; font-size:14px\"><![endif]--><a href=\""+link+"\" target=\"_blank\" style=\"text-decoration:none;display:inline-block;color:#ffffff;background-color:#8412c0;border-radius:0px;width:auto;border-top:1px solid transparent;font-weight:400;border-right:1px solid transparent;border-bottom:1px solid transparent;border-left:1px solid transparent;padding-top:10px;padding-bottom:10px;font-family:'Cabin', Arial, 'Helvetica Neue', Helvetica, sans-serif;font-size:14px;text-align:center;mso-border-alt:none;word-break:keep-all;\"><span style=\"padding-left:40px;padding-right:40px;font-size:14px;display:inline-block;letter-spacing:normal;\"><span style=\"word-break:break-word;\"><span style=\"line-height: 28px;\" data-mce-style>View PDF File</span></span></span></a><!--[if mso]></center></v:textbox></v:roundrect><![endif]--></div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"text_block block-7\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\" style=\"padding-bottom:20px;padding-left:10px;padding-right:10px;padding-top:10px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div style=\"font-family: sans-serif\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class style=\"font-size: 12px; font-family: Arial, Helvetica Neue, Helvetica, sans-serif; mso-line-height-alt: 14.399999999999999px; color: #8412c0; line-height: 1.2;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 16.8px;\"><span style=\"color:#8a3b8f;\">PDF Managment System&nbsp; © ·</span><span style> </span><span style><a href=\""+link+"\" target=\"_blank\" style=\"text-decoration: underline; color: #8412c0;\" rel=\"noopener\">Unsuscribe</a></span></p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t<table class=\"row row-2\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t<td>\n" +
                "\t\t\t\t\t\t\t\t\t<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000; width: 650px;\" width=\"650\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-bottom: 5px; padding-top: 5px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"icons_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\" style=\"vertical-align: middle; color: #9d9d9d; font-family: inherit; font-size: 15px; padding-bottom: 5px; padding-top: 5px; text-align: center;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"alignment\" style=\"vertical-align: middle; text-align: center;\"><!--[if vml]><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"display:inline-block;padding-left:0px;padding-right:0px;mso-table-lspace: 0pt;mso-table-rspace: 0pt;\"><![endif]-->\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!--[if !vml]><!-->\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"icons-inner\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block; margin-right: -4px; padding-left: 0px; padding-right: 0px;\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"><!--<![endif]-->\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t</table>\n" +
                "\t\t\t\t</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t</tbody>\n" +
                "\t</table><!-- End -->\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @PostMapping("/share")
    public String shareFile(@RequestParam("email") String email,
                            @RequestParam("pdfFileId") Long pdfFileId) throws MessagingException, UnsupportedEncodingException {
        // Validate the email and PDF file ID
        if (email.isEmpty()) {
            // Handle empty email
        }


        Optional<PdfFile> pdfFile = pdfFileService.findById(pdfFileId);
        if (pdfFile == null) {
            // Handle invalid PDF file ID

        }

        // Generate a unique link for sharing
        String uniqueLink = pdfFile.get().getUniqueLink();


        // Send an email to the provided email address with the unique link
        sendShareEmail(email, uniqueLink);

        // Redirect to the Dashboard page or a success message page
        return "redirect:/view/" + pdfFileId;
    }

    @GetMapping("/view/{uniqueCode}")
    public String viewPDF(@PathVariable("uniqueCode") String uniqueCode, Model model) {
        // Retrieve the PdfFile object
//        Optional<PdfFile> pdfFileOptional = pdfFileService.findById(fileId);

        PdfFile pdfFile = pdfFileService.findByUniqueLink(uniqueCode);

        // Check if the PdfFile exists
        if (pdfFile != null) {

            // Convert the byte array to Base64 string
            String base64Data = Base64.getEncoder().encodeToString(pdfFile.getData());

            // Pass the base64Data to the viewpdffile.html template
            model.addAttribute("base64Data", base64Data);
            model.addAttribute("pdfFile", pdfFile);
            return "viewpdffile";
        } else {
            // Handle the case when the PdfFile does not exist


            model.addAttribute("error", "The requested PDF file is not present");
            model.addAttribute("message", "please select valid PDF file from list");
            return "error"; // You can create an error.html template to display an error message
        }
    }

//    @PostMapping("/comments/add")
//    public String addComment(@RequestParam("pdfFileId") Long pdfFileId, @RequestParam("commentText") String commentText, Principal principal) {
//
//        String email = principal.getName();
//        User user = userRepository.findByEmail(email);
//
//        commentRepository.addComment(pdfFileId, commentText, user);
//
//        return "redirect:/view/" + pdfFileId;
//    }

    @PostMapping("/comments/add")
    public String addCommentToPDF(@RequestParam("pdfFileId") Long pdfFileId, @RequestParam("commentText") String commentText, Principal principal) {
        try {
            String email = principal.getName();
            User user = userRepository.findByEmail(email);
            PdfFile pdfFile= pdfFileService.getPdfFileById(pdfFileId);
            String uniqueLink=pdfFile.getUniqueLink();
            Comment comment = new Comment(pdfFile,user,commentText); // Create a new comment

            pdfFileService.addCommentToPDF(comment, pdfFileId);
            return "redirect:/view/" + uniqueLink;
        } catch (Exception e) {
            return "error";
        }
    }

//    @PostMapping("/comments/add")
//    public String addCommentToPDF(@RequestParam("pdfFileId") Long pdfFileId,
//                                  @RequestParam("commentText") String commentText,
//                                  @RequestParam(value = "parentId", required = false) Long parentId,
//                                  Principal principal) {
//        try {
//            String email = principal.getName();
//            User user = userRepository.findByEmail(email);
//            PdfFile pdfFile = pdfFileService.getPdfFileById(pdfFileId);
//
//            if (parentId != null) {
//                // If parentId is provided, it means it's a reply
//                Comment parentComment = commentService.getCommentById(parentId);
//                if (parentComment == null) {
//                    // Handle case when parent comment is not found
//                    return "error";
//                }
//
//                Comment reply = new Comment(pdfFile, user, commentText); // Create a new reply
//                parentComment.addReply(reply); // Add the reply to the parent comment
//
//                pdfFileService.addCommentToPDF(reply, pdfFileId);
//            } else {
//                // It's a new comment
//                Comment comment = new Comment(pdfFile, user, commentText); // Create a new comment
//                pdfFileService.addCommentToPDF(comment, pdfFileId);
//            }
//
//            return "redirect:/view/" + pdfFileId;
//        } catch (Exception e) {
//            return "error";
//        }
//    }





}
