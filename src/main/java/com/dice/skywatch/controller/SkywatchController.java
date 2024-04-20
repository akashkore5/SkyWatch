package com.dice.skywatch.controller;

import com.dice.skywatch.model.Weather;
import com.dice.skywatch.repository.UserRepository;
import com.dice.skywatch.service.SkywatchService;
import com.dice.skywatch.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.util.*;

@Controller
public class SkywatchController {
    @Autowired
    private JavaMailSender mailSender;
    private UserService userService;
    private SkywatchService skywatchService;
    private UserRepository userRepository;
    private final HttpServletRequest request;

    public SkywatchController(JavaMailSender mailSender, UserService userService,SkywatchService skywatchService, UserRepository userRepository, HttpServletRequest request) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.skywatchService = skywatchService;
        this.userRepository = userRepository;
        this.request = request;
    }
    @GetMapping("/forecast")
    public String getForecastSummary(@RequestParam String location) {
        // Token authorizationnot needed as user already logged in
        return skywatchService.RapidApiGetForecastSummaryByLocationName(location);
    }


    @GetMapping("/hourlyForecast")
    public String getHourlyForecast(@RequestParam  String location){
        return skywatchService.RapidApiGetHourlyForecastByLocationName(location);
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal, @RequestParam(value = "searchKeyword", required = false) String searchKeyword) throws JSONException {

        if(searchKeyword==null || searchKeyword==""){
            return "dashboard";
        }
        try{
            String forecastSummary = skywatchService.RapidApiGetForecastSummaryByLocationName(searchKeyword);
            String hourlyForecastSummary = skywatchService.RapidApiGetHourlyForecastByLocationName(searchKeyword);

            JSONObject jsonObject = new JSONObject(forecastSummary);
            JSONArray forecastArray = jsonObject.getJSONObject("forecast").getJSONArray("items");

            List<Weather> forecastDays = new ArrayList<>();
            for (int i = 0; i < Math.min(forecastArray.length(), 7); i++) { // Loop through the first 7 days
                JSONObject dayObject = forecastArray.getJSONObject(i);

                String date = dayObject.getString("date");
                int sunHours = dayObject.getInt("sunHours");
                int rainHours = dayObject.optInt("rainHours", 0); // Using optInt to handle null
                int temperatureMin = dayObject.getJSONObject("temperature").getInt("min");
                int temperatureMax = dayObject.getJSONObject("temperature").getInt("max");
                Weather weather=new Weather(date, sunHours, rainHours, temperatureMin, temperatureMax);
                forecastDays.add(weather);
            }
            model.addAttribute("forecastSummary", forecastDays);
            model.addAttribute("hourlyForecast", hourlyForecastSummary);
            model.addAttribute("location", searchKeyword);
        }catch(HttpClientErrorException.NotFound ex){
            model.addAttribute("error","Location Data Not Found");
        }catch(Exception ex){
            model.addAttribute("error","API Rate Limit Exceeded");
        }
        return "dashboard";
    }

}
