<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="com.stock.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="cancel" value="<%=Const.JoinStatus.CANCEL.name()%>" />
<c:set var="PAYCO" value="<%=Const.PG.PAYCO.name()%>" />
<c:set var="ALLAT" value="<%=Const.PG.ALLAT.name()%>" />
<script language=JavaScript charset='euc-kr' src="https://tx.allatpay.com/common/NonAllatPayREPlus.js"></script>
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
        <div class="sub_title yellow">
            휴대전화 본인인증이 완료되었습니다.<br />( 성명 : ${name} )
        </div>
        <div class="text">
            실명인증으로 가입해지를 진행하고 있습니다.
        </div>
        <button id="req_auth" class="btn btn-black on">휴대전화 본인 재인증</button>
        <div id="phoneFormat" class="sub_title">인증 휴대폰 번호 ${phoneNum}</div>
    </div>
    <div class="service">
    <button id="btn" class="btn btn-black" style="margin-top: 0">
        서비스 가입해지하기
    </button>
    <div class="notice">
        선 결제된 해지월 요금은 일할 계산 되어 부분 취소 청구됩니다.
    </div>
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

    <!-- 올앳 데이터 -->
    <form name="form_allat" method="post">
        <%--  필수 정보   --%>
        <input type="hidden" name="allat_shop_id"      value="">
        <input type="hidden" name="allat_fix_key"      value="">
        <input type="hidden" name="shop_receive_url"   value="">
        <input type="hidden" name="allat_enc_data"     value="">
        <input type=hidden   name="allat_opt_pin"      value="NOVIEW">
        <input type=hidden   name="allat_opt_mod"      value="WEB">
        <%--  옵션 정보   --%>
        <input type="hidden" name="allat_fix_type"     value="FIX">
        <input type="hidden" name="allat_test_yn"      value="">
    </form>
</section>
<script language='javascript'>
    phoneFormat();

    $(document).ready(function() {
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
            var ssStorage = storage(StorageType.SESSION_TYEP),
                sMediaKey = ssStorage.getDecodeData("mediaKey");

            $.ajax({
                type: "GET",
                url: "/payment/pg?mediaKey=" + sMediaKey,
                contentType: 'application/json',
                success:function(data){
                    var pg = data;
                    switch (pg) {
                        case '${PAYCO}':
                            autoPayment_cancel();
                            break;
                        case '${ALLAT}':
                            allat_autoPayment_cancel();
                            break;
                    }
                },
                error: function() {
                    alert("해지 처리 중 오류가 발생했습니다.");
                }
            });
        });
    });

    // 올앳 자동결제 해지
    function allat_autoPayment_cancel(){
        $.ajax({
            type: "POST",
            url: "/payment/allat/cancel/request",
            data : JSON.stringify({ userCid : '${userCid}'}),
            contentType: 'application/json',
            success:function(data){
                $('input[name=allat_shop_id]').attr('value',data.allat_shop_id);
                $('input[name=allat_fix_key]').attr('value',data.allat_fix_key);
                $('input[name=shop_receive_url]').attr('value',data.shop_receive_url);
                $('input[name=allat_test_yn]').attr('value',data.allat_test_yn);

                // 암호화 데이터 생성 (NonAllatPayRE.js)
                Allat_Plus_Fix_Cancel(form_allat);
            },
            error: function() {
                alert("해지 처리 중 오류가 발생했습니다.");
            }
        });

    }

    // 올앳 인증 결과값 반환( allat-receive.jsp 페이지에서 호출 )
    function allat_result_submit(result_cd,result_msg,enc_data) {
        Allat_Plus_Close();

        if (result_cd === '0000') {
            form_allat.allat_enc_data.value = enc_data;
            autoPayment_cancel();
        } else {
            alert("해지 처리 중 오류가 발생했습니다.");
        }
    }

    // 자동결제 해지
    function autoPayment_cancel(){
        var param = {
            userCid : '${userCid}',
            phoneNum : '${phoneNum}',
            encodeData : $('input[name=allat_enc_data]').val()
        };

        $.ajax({
            type: "POST",
            url: "/payment/cancel",
            data : JSON.stringify(param),
            contentType: 'application/json',
            success:function(){
                location.href='/user/cancel/result?userCid=' + encodeURIComponent('${userCid}');
            },
            error: function(request) {
                if(request.responseJSON === undefined){
                    alert("해지 처리 중 오류가 발생했습니다. 고객센터로 문의주세요.");
                } else if(request.responseJSON.code === '99') {
                    alert(request.responseJSON.msg);
                }
            }
        });

    }

    // 본인인증 팝업
    function fnPopup(){
        window.open('', 'popupChk', 'width=500, height=550, top=100, left=100, fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no');
        document.form_auth.action = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb";
        document.form_auth.target = "popupChk";
        document.form_auth.submit();
    }

</script>
