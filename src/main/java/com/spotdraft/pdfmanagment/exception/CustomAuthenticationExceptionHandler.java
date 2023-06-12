package com.spotdraft.pdfmanagment.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomAuthenticationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotVerifiedException.class)
    public ModelAndView handleUserNotVerifiedException(UserNotVerifiedException ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.setViewName("login"); // Customize the view name according to your project setup
        return modelAndView;
    }

    // Other exception handlers...

}
