package com.urly.urlyservices.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RateLimiterInfo {

    private String key;

    @EqualsAndHashCode.Exclude
    private double permitsPerSecond;

    @EqualsAndHashCode.Exclude
    private int permits;

    @EqualsAndHashCode.Exclude
    private int period;
}
