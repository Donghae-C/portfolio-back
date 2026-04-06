package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.common.exception.ErrorCode;
import com.portfolio.portfolioback.common.exception.MyPortFolioException;
import com.portfolio.portfolioback.dto.BusListResponse;
import com.portfolio.portfolioback.dto.BusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BusServiceImpl implements BusService {

    private final String API_URL;
    private final String OPENAPI_KEY;
    private final String STA_ORDER = "19";
    private final String FORMAT = "json";
    private final RestClient restClient;

    public BusServiceImpl(@Value("${spring.bus.api.url}") String apiUrl, @Value("${spring.bus.openapi.key}") String apiKey, RestClient restClient){
        this.API_URL = apiUrl;
        this.OPENAPI_KEY = apiKey;
        this.restClient = restClient;
    }

    @Override
    public Map<String, String> getBusInfo(String stationCode, String busCode) {
        Map<String, String> busInfo = new HashMap<>();
        BusResponse data = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("apis.data.go.kr")
                        .path(API_URL)
                        .queryParam("format", FORMAT)
                        .queryParam("serviceKey", OPENAPI_KEY)
                        .queryParam("stationId", stationCode)
                        .queryParam("routeId", busCode)
                        .queryParam("staOrder", STA_ORDER)
                        .build())
                .retrieve()
                .body(BusResponse.class);
        if(data == null){
            throw new MyPortFolioException(ErrorCode.DB_ERROR);
        }
        int minute = data.getResponse().getMsgBody().getBusArrivalItem().getPredictTime1();
        int minute2 =  data.getResponse().getMsgBody().getBusArrivalItem().getPredictTime2();
        String busName = data.getResponse().getMsgBody().getBusArrivalItem().getRouteName();
        String message = busName + "번 버스 도착까지 " + minute + "분 남음";
        String message2 = busName + "번 버스 도착까지 " + minute2 + "분 남음";
        busInfo.put("message", message);
        busInfo.put("message2", message2);
        return busInfo;
    }

    @Override
    public Map<String, Long> getBusList(String stationCode) {
        BusListResponse busListResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("apis.data.go.kr")
                        .path("/6410000/busarrivalservice/v2/getBusArrivalListv2")
                        .queryParam("format", FORMAT)
                        .queryParam("serviceKey", OPENAPI_KEY)
                        .queryParam("stationId", stationCode)
                        .build())
                .retrieve()
                .body(BusListResponse.class);
        List<BusListResponse.BusArrivalItem> busList = busListResponse.getResponse().getMsgBody().getBusArrivalList();
        Map<String, Long> busInfo = new HashMap<>();
        busList.forEach(n -> busInfo.put(n.getRouteName(), n.getRouteId()));
        return busInfo;
    }
}
