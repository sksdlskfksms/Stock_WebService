package com.stock.mapper;

import com.stock.core.anotation.Mapper;
import com.stock.vo.ReserveVO;
import com.stock.vo.allat.AllatApproveReqVO;
import com.stock.vo.payco.PaycoReserveInfoReturnVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ReserveMapper {
    Long insertReserve(ReserveVO reserveVO);

    ReserveVO selectReserveById(Long id);

    ReserveVO selectReserveByUserCid(String userCid);

    List<ReserveVO> selectReserveInfoList(String status);

    void savePaycoReserveInfo(PaycoReserveInfoReturnVO paycoReserveInfoReturnVO);

    void saveAllatReserveInfo(AllatApproveReqVO allatApproveReqVO);

}
