package com.stock.vo.emoney;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class EMoneyMainDataVO {
    private String resultCode;                // 처리 결과
    public TodayStock todayStock;             // 오늘의 추천종목
    public List<Ceiling> ceiling;             // AI급등주 포착기
    public List<Report> report;               // 증권가1%리포트
    public List<Profit> profit;               // 실시간 수익현황
    public Date date;                         // 조회 기준 시간

    @Getter
    @Setter
    public static class TodayStock{
        private String stockName;             // 종목명
        private String targetProfit;          // 기대수익률
        private String price;                 // 현재가
        private int expectProfit;             // 예상수익금

        public void setExpectProfit(){
            if(this.targetProfit == null) this.expectProfit = 0;
            else {
                this.expectProfit = (int)(10000 * Double.parseDouble(this.targetProfit));
            }
        }
    }

    @Getter
    @Setter
    public static class Ceiling{
        private String stockName;             // 종목명
        private String content;               // 내용
        private String curProfit;             // 수익률
    }

    @Getter
    @Setter
    public static class Report{
        private String title;                 // 제목
        private String company;               // 증권사
        private String stockName;             // 종목명
    }

    @Getter
    @Setter
    public static class Profit{
        private String stockName;             // 종목명
        private String curProfit;             // 수익률
    }

}


