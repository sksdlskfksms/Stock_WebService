package com.stock.vo;

import com.stock.util.Const;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.text.ParseException;
import java.util.Date;

@Getter
@Setter
@ToString
public class UserVO {

    private Long   id;
    private String name;
    private String phoneNum;
    private String mediaKey;
    private String mediaUserKey;
    private String userCid;
    private Const.JoinStatus status;
    private Long reserveId;
    private String joinDate;
    private String cancelDate;
    private String encodeData;

    public String getCancelDate(){
        return this.cancelDate == null ? "000000000000" : this.cancelDate;
    }

    public Date getJoinDateToDateFormat() throws ParseException {
        return Const.yyyyMMddHHmm_FORMAT.parse(this.joinDate);
    }

    public Date getCancelDateToDateFormat() throws ParseException {
        return Const.yyyyMMddHHmm_FORMAT.parse(this.cancelDate);
    }

    public String getDateToStringFormat(String param){
        String year = param.substring(0, 4);
        String month = param.substring(4, 6);
        String date = param.substring(6, 8);
        String hour = param.substring(8, 10);
        String min = param.substring(10);

        return String.format("%s년 %s월 %s일 %s시 %s분", year, month, date, hour, min);
    }

}


