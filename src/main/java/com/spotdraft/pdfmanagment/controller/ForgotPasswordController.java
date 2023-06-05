//package com.spotdraft.pdfmanagment.controller;
//
//import com.spotdraft.pdfmanagment.exception.UserNotFoundException;
//import com.spotdraft.pdfmanagment.model.User;
//import com.spotdraft.pdfmanagment.service.UserService;
//import com.spotdraft.pdfmanagment.utility.Utility;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.repository.query.Param;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import java.io.UnsupportedEncodingException;
//import java.util.UUID;
//
//@Controller
//public class ForgotPasswordController {
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/forgot_password")
//    public String showForgotPasswordForm() {
//        return "forgot_password_form";
//
//    }
//
//    @PostMapping("/forgot_password")
//    public String processForgotPassword(HttpServletRequest request, Model model) {
//        String email = request.getParameter("email");
//        UUID uuid = UUID.randomUUID();
//        String token = uuid.toString().replaceAll("-", "").substring(0, 30);
//
//        try {
//            userService.updateResetPasswordToken(token, email);
//            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
//            sendEmail(email, resetPasswordLink);
//            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
//
//        } catch (UserNotFoundException ex) {
//            model.addAttribute("error", ex.getMessage());
//        } catch (UnsupportedEncodingException | MessagingException e) {
//            model.addAttribute("error", "Error while sending email");
//        }
//
//        return "forgot_password_form";
//    }
//
//    public void sendEmail(String recipientEmail, String link)
//            throws MessagingException, UnsupportedEncodingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//
//        helper.setFrom("contact@shopme.com", "Shopme Support");
//        helper.setTo(recipientEmail);
//
//        String subject = "Here's the link to reset your password";
//
//        String content = "<p>Hello,</p>"
//                + "<p>You have requested to reset your password.</p>"
//                + "<p>Click the link below to change your password:</p>"
//                + "<p><a href=\"" + link + "\">Change my password</a></p>"
//                + "<br>"
//                + "<p>Ignore this email if you do remember your password, "
//                + "or you have not made the request.</p>";
//
//        helper.setSubject(subject);
//
//        helper.setText(content, true);
//
//        mailSender.send(message);
//    }
//
//
//    @GetMapping("/reset_password")
//    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
//        User user = userService.getByResetPasswordToken(token);
//        model.addAttribute("token", token);
//
//        if (user == null) {
//            model.addAttribute("message", "Invalid Token");
//            return "message";
//        }
//
//        return "reset_password_form";
//    }
//
//    @PostMapping("/reset_password")
//    public String processResetPassword(HttpServletRequest request, Model model) {
//        String token = request.getParameter("token");
//        String password = request.getParameter("password");
//
//        User customer = userService.getByResetPasswordToken(token);
//        model.addAttribute("title", "Reset your password");
//
//        if (customer == null) {
//            model.addAttribute("message", "Invalid Token");
//            return "message";
//        } else {
//            userService.updatePassword(customer, password);
//
//            model.addAttribute("message", "You have successfully changed your password.");
//        }
//
//        return "message";
//    }
//}