package com.portfolio.portfolioback.service;

import java.util.List;
import java.util.Map;

public interface BusService {
    Map<String, String> getBusInfo(String stationCode, String busCode);
    Map<String, Long> getBusList(String stationCode);
}
