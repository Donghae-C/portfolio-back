package com.portfolio.portfolioback.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusListResponse {

    // 최상위 "response" 객체
    private ResponseDTO response;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDTO {

        // 빈 문자열로 오는 경우도 있으므로 String 처리
        private String comMsgHeader;

        // 응답 헤더 정보
        private MsgHeader msgHeader;

        // 실제 데이터 본문
        private MsgBody msgBody;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MsgHeader {

        // "2026-03-25 11:14:05.051" 형태라 String으로 받는 게 편함
        // 필요하면 나중에 LocalDateTime으로 변환 가능함
        private String queryTime;

        private int resultCode;
        private String resultMessage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MsgBody {

        // 도착 예정 버스 목록
        private List<BusArrivalItem> busArrivalList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusArrivalItem {

        private int crowded1;
        private int crowded2;
        private String flag;

        private int locationNo1;
        private int locationNo2;

        private int lowPlate1;
        private int lowPlate2;

        private String plateNo1;
        private String plateNo2;

        private int predictTime1;
        private int predictTime2;

        private int remainSeatCnt1;
        private int remainSeatCnt2;

        private long routeDestId;
        private String routeDestName;

        private long routeId;

        // 숫자(51, 422, 101) + 문자열("7-1", "720-2", "33-1") 혼합이라 String으로 받는 게 안전함
        private String routeName;

        private int routeTypeCd;
        private int staOrder;
        private long stationId;

        private String stationNm1;
        private String stationNm2;

        private int taglessCd1;
        private int taglessCd2;

        private int turnSeq;

        private long vehId1;
        private long vehId2;

        private int predictTimeSec1;
        private int predictTimeSec2;

        private int stateCd1;
        private int stateCd2;
    }
}