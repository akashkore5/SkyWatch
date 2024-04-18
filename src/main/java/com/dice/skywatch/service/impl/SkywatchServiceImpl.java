package com.dice.skywatch.service.impl;

import com.dice.skywatch.service.SkywatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SkywatchServiceImpl implements SkywatchService {


    @Value("${rapidApi.url.forecastSummary}")
    private String forecastSummaryUrl;

    @Value("${rapidApi.url.hourlyForecast}")
    private String hourlyForecastUrl;

    @Value("${openWeatherApi.Key}")
    private String openWeatherkey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders headers;

    @Override
    public String RapidApiGetForecastSummaryByLocationName(String location) {

        String url = forecastSummaryUrl+location+"/summary/";
        System.out.println(url);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> rs = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return rs.getBody();
    }

    @Override
    public String RapidApiGetHourlyForecastByLocationName(String location) {

        String url =  hourlyForecastUrl+location+"&appid="+openWeatherkey;
//        String url = hourlyForecastUrl.replace("${cityName}", location);
        System.out.println(url);
        return restTemplate.getForObject(url , String.class);
    }
}