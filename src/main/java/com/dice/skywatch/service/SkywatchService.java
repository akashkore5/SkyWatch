package com.dice.skywatch.service;

import com.dice.skywatch.dto.UserDto;
import com.dice.skywatch.exception.UserNotFoundException;
import com.dice.skywatch.model.User;

import java.util.List;

public interface SkywatchService {


    public String RapidApiGetForecastSummaryByLocationName(String location);

    public  String RapidApiGetHourlyForecastByLocationName(String location);
}