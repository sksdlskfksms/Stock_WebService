package com.stock.mapper;

import com.stock.core.anotation.Mapper;
import com.stock.vo.emoney.EMoneyResultVO;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EMoneyMapper {

    void insertEMoneyApiResult(EMoneyResultVO eMoneyResultVO);

}
