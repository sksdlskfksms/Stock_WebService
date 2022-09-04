<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="com.stock.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="cancel" value="<%=Const.JoinStatus.CANCEL.name()%>" />
<header>
    <div class="back" onclick="javascript:history.back();"></div>
    가입해지
</header>
<section>
    <div class="main">
        <img src="/images/main.png" alt="" />
    </div>
    <div class="real_name">
        <div class="main_title">서비스 가입 해지</div>
        <div class="title">실명 인증 하기</div>
        <div class="sub_title red">휴대전화 본인인증이 필요합니다.</div>
        <div class="text">
            실명인증으로 가입해지를 진행하고 있습니다.
        </div>
        <button id="req_auth" class="btn btn-black on">휴대전화 본인인증</button>
    </div>
    <div class="service">
        <form action="" style="margin-top: 0">
            <button id="btn" class="btn btn-gray" style="margin-top: 0">
                서비스 가입해지하기
            </button>
            <div class="notice">
                선 결제된 해지월 요금은 일할 계산 되어 부분 취소 청구됩니다.
            </div>
        </form>
    </div>
</section>
<section>
    <!-- 본인인증 서비스 팝업을 호출용 -->
    <form name="form_auth" method="post">
        <input type="hidden" name="m" value="checkplusService">
        <input type="hidden" name="EncodeData" value="">
    </form>

    <!-- 본인인증 결과 -->
    <form id="form" name="form_auth_result" action="/user/cancel" method="post">
        <input type = "hidden" name="name" id="name">
        <input type = "hidden" name="phoneNum" id="phoneNum">
        <input type = "hidden" name="userCid" id="userCid">
        <input type = "hidden" name="encodeData" id="encodeData">
    </form>
</section>
<script language='javascript'>
    $(document).ready(function() {
        // 결제 중 에러 발생했을 시 팝업창 종료를 위함
        var paramManager = ParamManager();
        if(paramManager.get("error")){
            window.close();
        }

        // 본인인증을 위한 암호화 요청
        $('#req_auth').click(function (){
            $.ajax({
                type: 'GET',
                url: '/auth/request/encdata?type=${cancel}',
                contentType: 'application/json',
                async: false,
                success: function (data) {
                    $('input[name=EncodeData]').attr('value', data);
                    fnPopup();
                },
                error : function(request) {
                    if(request.responseText === undefined){
                        console.log("암호화 연동 실패");
                        alert("잠시 후 다시 이용해주세요.");
                    } else if(request.responseText === '99') {
                        alert("본인인증 중 오류가 발생했습니다. 잠시 후 다시 이용해주세요.");
                    }
                }
            });
        });

        // 가입해지하기 버튼
        $('#btn').click(function (){
            alert("실명인증 후 해지가 가능합니다.");
            return false;
        });
    });

    // 본인인증 팝업
    function fnPopup(){
        window.open('', 'popupChk', 'width=500, height=550, top=100, left=100, fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no');
        document.form_auth.action = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb";
        document.form_auth.target = "popupChk";
        document.form_auth.submit();
    }
</script>

