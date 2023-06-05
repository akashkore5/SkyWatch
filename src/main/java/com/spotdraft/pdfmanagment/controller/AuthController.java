package com.spotdraft.pdfmanagment.controller;

import com.spotdraft.pdfmanagment.dto.UserDto;
import com.spotdraft.pdfmanagment.exception.UserNotFoundException;
import com.spotdraft.pdfmanagment.model.User;
import com.spotdraft.pdfmanagment.service.UserService;
import com.spotdraft.pdfmanagment.utility.Utility;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@Controller
public class AuthController {
    @Autowired
    private JavaMailSender mailSender;


    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // handler method to handle home page request
    @GetMapping("/index")
    public String home(){
        return "login";
    }

    // handler method to handle login request
    @GetMapping("/login")
    public String login(@RequestParam(value = "targetUrl", required = false) String targetUrl, Model model, HttpServletRequest request) {
        if (targetUrl != null && !targetUrl.isEmpty()) {
            // Pass the targetUrl as a model attribute to the login page

            HttpSession session = request.getSession();
            session.setAttribute("targetUrl", targetUrl);
            model.addAttribute("targetUrl", targetUrl);
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam(value = "targetUrl", required = false) String targetUrl, Model model,HttpServletRequest request) {
        // Perform the login logic here


        HttpSession session = request.getSession();

        targetUrl = (String) session.getAttribute("targetUrl");
//        if (targetUrl == null && targetUrl.isEmpty()) {
//            // Redirect to the target URL if it is provided
//            targetUrl = (String) session.getAttribute("targetUrl");
//        }

        if (targetUrl != null && !targetUrl.isEmpty()) {
            // Redirect to the target URL if it is provided

            session.removeAttribute("targetUrl");
            return "redirect:" + targetUrl;
        } else {
            // Redirect to the default page after successful login
            return "redirect:/dashboard";
        }
    }



    // handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        // create model object to store form data
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model){
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    // handler method to handle list of users
    @GetMapping("/users")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password_form";

    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = UUID.randomUUID().toString();

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (UserNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "forgot_password_form";
    }

    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("support@spotdraft.com", "Password Reset");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }


    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "reset_password_form";
        }

        return "reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (customer == null) {
            model.addAttribute("message", "Invalid Token");
            return "reset_password_form";
        } else {
            userService.updatePassword(customer, password);
            model.addAttribute("message", "You have successfully changed your password.");
            return "reset_password_form";
        }
    }

}