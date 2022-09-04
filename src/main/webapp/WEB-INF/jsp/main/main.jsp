<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:choose>
    <c:when test="${isJoinUser == true}">
        <iframe
                src="https://dev.robostock.co.kr/m/main.gw"
                width="100%"
                sandbox="allow-scripts allow-popups"
                id="iframeId"
                >
        </iframe>
        <script>
            // 이머니 iframe 높이 규격 세팅
            window.onmessage = function (e){
                document.getElementById('iframeId').style.height = e.data + 'px';
            }

            // mediaUserKey 전달
            window.onload = function (){
                var ssStorage = storage(StorageType.SESSION_TYEP),
                    mediaUserKey =  ssStorage.getDecodeData("mediaUserKey");

                document.getElementById('iframeId').contentWindow.postMessage(mediaUserKey, '*');
            }
        </script>
    </c:when>
    <c:otherwise>
        <div class="main_info">
            <div class="title">
                오늘의 추천종목<br />
                <span>${emoneyData.todayStock.stockName}</span>
            </div>
            <div class="revenue">
                <span>100만원</span> 투자 시 <br />
                예상 수익금
            </div>
            <div class="price"><span><fmt:formatNumber value="${emoneyData.todayStock.expectProfit}" pattern="#,###" />원</span>입니다.</div>
            <div class="info">현재가 <fmt:formatNumber value="${emoneyData.todayStock.price}" pattern="#,###" /> | 기대수익률 ${emoneyData.todayStock.targetProfit}% 기준</div>
            <div class="desc">
                이종목 언제팔지 궁금하면? <img src="/images/down.png" alt=""/>
            </div>
            <button class="btn btn-black on go_join">가입하기</button>
        </div>
        <div class="main_cont_wrap">
            <div class="ai main_cont">
                <div class="title">
                    <div class="img"><img src="/images/title_01.png" alt="" /></div>
                    <div class="text">
                        <div class="title">AI 급등주 포함</div>
                        <div class="desc">
                            매일 급등주를 포착 후 급등 사유를 분석합니다.
                        </div>
                    </div>
                </div>
                <div class="content">
                    <ul class="ai_list">
                    <c:forEach items="${emoneyData.ceiling}" var="ceilingList">
                        <li>
                            <div class="num">1</div>
                            <div class="text">
                                <div class="title">${ceilingList.stockName}</div>
                                <div class="desc">${ceilingList.content}</div>
                            </div>
                            <div class="per"><img src="/images/up_ic.png" />${ceilingList.curProfit}%</div>
                        </li>
                    </c:forEach>
                    </ul>
                    <div class="more go_join">
                        더 많은 급등주 보기 <img src="/images/more.png" alt="" />
                    </div>
                </div>
            </div>
            <div class="report main_cont">
                <div class="title">
                    <div class="img"><img src="/images/title_02.png" alt="" /></div>
                    <div class="text">
                        <div class="title">증권가 1% 리포트</div>
                        <div class="desc">VIP 대상 유망 종목 상세분석 리포트입니다.</div>
                    </div>
                </div>
                <div class="content">
                    <c:forEach items="${emoneyData.report}" var="reportList" varStatus="status">
                        <c:if test="${status.index == 0}">
                            <div class="title">
                        </c:if>
                        <c:if test="${status.index != 0}">
                            <div class="title" style="padding-top: 20px;">
                        </c:if>
                        <span>
                          <img src="/images/check.png" alt="" />
                          ${reportList.title}
                        </span>
                        </div>
                        <ul class="report_list">
                            <li>
                                <div class="title">증권사</div>
                                <div class="text">${reportList.company}</div>
                            </li>
                            <li>
                                <div class="title">종목명</div>
                                <div class="text">${reportList.stockName}</div>
                            </li>
                        </ul>
                    </c:forEach>
                    <div class="more go_join">
                        리포트 상세 보기 <img src="/images/more.png" alt="" />
                    </div>
                </div>
            </div>
            <div class="realtime main_cont">
                <div class="title">
                    <div class="img"><img src="/images/title_03.png" alt="" /></div>
                    <div class="text">
                        <div class="title">실시간 수익 현황</div>
                        <div class="desc">최근 실제 추천한 종목의 매도 수익률입니다.</div>
                    </div>
                </div>
                <div class="content">
                    <table class="realtime_table">
                        <thead>
                        <tr>
                            <th>종목</th>
                            <th>수익률</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${emoneyData.profit}" var="profitList">
                            <tr>
                                <td>${profitList.stockName}</td>
                                <td class="per"><img src="/images/up_ic.png" />${profitList.curProfit}%</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="realtime_info">(<fmt:formatDate value="${emoneyData.date}" pattern="yyyy.MM.dd" /> 기준)</div>
                    <div class="title">
                        매일 기대 수익률 20% 이상 종목의
                        <span>매매 신호 알림을 받고 싶다면?</span>
                    </div>
                    <div class="click_button go_join">
                        <img src="/images/click.png" alt="" />
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<script language='javascript'>
    $(document).ready(function() {
        var paramManager = ParamManager(),
            ssStorage = storage(StorageType.SESSION_TYEP),
            mediaKey = paramManager.get("mediaKey"),
            mediaUserKey = paramManager.get("mediaUserKey");

        if (mediaKey) ssStorage.setEncodeData("mediaKey", mediaKey);
        if (mediaUserKey) ssStorage.setEncodeData("mediaUserKey", mediaUserKey);

        history.replaceState({}, null, location.pathname);

        $('.go_join').click(function (){
            if (${isSnsLoginOk}){
                PopupUtil.open('login_popup');
            }else{
                location.href='/user/join';
            }
        });
    });

    window.addEventListener( 'message', (e) => {
        if( e.data.functionName === 'goCancelPage' ){
            location.href='/user/cancel';
        }
    });
</script>
<%-- SNS 간편로그인 팝업 --%>
<script id="login_popup"  type="text/mustache-tmpl" >
    <div class="popup_wrap" name="login_popup">
      <div class="popup_cont">
        <div class="popup_close" name="__CANCEL_BTN__"></div>
        <div class="title">
          이용을 위해서는<br />로그인이 필요합니다.
        </div>
        <div
          class="btn_login btn_kakao"
          onclick="javascript:location.href='${kakaoAuthUrl}';"
        >
          <img src="/images/kakao.png" alt="" />
          카카오계정으로 로그인
        </div>
        <div
          class="btn_login btn_naver"
          onclick="javascript:location.href='${naverAuthUrl}';"
        >
          <img src="/images/naver.png" alt="" />
          네이버아이디로 로그인
        </div>
      </div>
    </div>
</script>
