package com.stock.vo;

import com.stock.util.Const;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MediaVO {
    private String partnerPg;
    private String snsUseFlg;

    public String getPartnerPg(){
        return this.partnerPg == null ? Const.PG.ALLAT.name() : this.partnerPg;
    }

    public Boolean getSnsUserFlg(){
        return this.snsUseFlg.equals("Y");
    }
}

