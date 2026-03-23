package com.portfolio.portfolioback.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BusResponse {

    private Response response; // 최상위 response 객체

    @Getter
    @Setter
    @ToString
    public static class Response {

        private String comMsgHeader; // 공통 메시지 헤더
        private MsgHeader msgHeader; // 결과 코드/시간 정보
        private MsgBody msgBody;     // 실제 바디
    }

    @Getter
    @Setter
    @ToString
    public static class MsgHeader {

        private String queryTime;      // 조회 시간
        private int resultCode;        // 결과 코드
        private String resultMessage;  // 결과 메시지
    }

    @Getter
    @Setter
    @ToString
    public static class MsgBody {

        private BusArrivalItem busArrivalItem; // 도착 정보
    }

    @Getter
    @Setter
    @ToString
    public static class BusArrivalItem {

        private int crowded1;          // 첫 번째 차량 혼잡도
        private int crowded2;          // 두 번째 차량 혼잡도
        private String flag;           // 상태값
        private int locationNo1;       // 첫 번째 차량 남은 정류장 수
        private int locationNo2;       // 두 번째 차량 남은 정류장 수
        private int lowPlate1;         // 첫 번째 저상버스 여부
        private int lowPlate2;         // 두 번째 저상버스 여부
        private String plateNo1;       // 첫 번째 차량 번호
        private String plateNo2;       // 두 번째 차량 번호
        private int predictTime1;      // 첫 번째 도착예정 분
        private int predictTime2;      // 두 번째 도착예정 분
        private int remainSeatCnt1;    // 첫 번째 잔여좌석 수
        private int remainSeatCnt2;    // 두 번째 잔여좌석 수
        private int routeDestId;       // 종점 ID
        private String routeDestName;  // 종점 이름
        private int routeId;           // 노선 ID
        private String routeName;      // 노선 번호
        private int routeTypeCd;       // 노선 타입 코드
        private int staOrder;          // 정류소 순번
        private int stationId;         // 정류소 ID
        private String stationNm1;     // 첫 번째 차량 현재 위치 정류장명
        private String stationNm2;     // 두 번째 차량 현재 위치 정류장명
        private int taglessCd1;        // 첫 번째 태그리스 여부
        private int taglessCd2;        // 두 번째 태그리스 여부
        private int turnSeq;           // 회차 순번
        private int vehId1;            // 첫 번째 차량 ID
        private int vehId2;            // 두 번째 차량 ID
        private int predictTimeSec1;   // 첫 번째 도착예정 초
        private int predictTimeSec2;   // 두 번째 도착예정 초
    }
}