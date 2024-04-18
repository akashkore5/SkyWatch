package com.dice.skywatch.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Weather
{
    private String date;
    private int sunHours;
    private int rainHours;
    private int temperatureMin;
    private int temperatureMax;
}