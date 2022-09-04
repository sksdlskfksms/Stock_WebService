<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="com.stock.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="join" value="<%=Const.JoinStatus.JOIN.name()%>" />
<c:set var="SERVICE" value="<%=Const.TermType.SERVICE.name()%>" />
<c:set var="PERSONAL_INFO" value="<%=Const.TermType.PERSONAL_INFO.name()%>" />
<c:set var="PERSONAL_INFO_SUPPLY" value="<%=Const.TermType.PERSONAL_INFO_SUPPLY.name()%>" />
<c:set var="PAYCO" value="<%=Const.PG.PAYCO.name()%>" />
<c:set var="ALLAT" value="<%=Const.PG.ALLAT.name()%>" />
<script language=JavaScript charset='euc-kr' src="https://tx.allatpay.com/common/NonAllatPayREPlus.js"></script>
<header>
    <div class="back" onclick="javascript:history.back();"></div>
    가입하기
</header>
<section>
    <div class="main">
        <img src="/images/main.png" alt="" />
    </div>
    <div class="real_name">
        <div class="title">실명 인증 하기</div>
        <div class="sub_title yellow">
            휴대전화 본인인증이 완료되었습니다.<br />( 성명 : ${name} )
        </div>
        <div class="text">
            실명인증으로 가입을 진행하고 있습니다.<br />입력하신 개인
            정보는 가입완료 전까지 저장되지 않습니다.
        </div>
        <button id="req_auth" class="btn btn-black on">휴대전화 본인 재인증</button>
        <div id="phoneFormat" class="sub_title">휴대폰 번호 ${phoneNum}</div>
    </div>
    <div class="service">
        <div class="title">서비스가입 약관 동의</div>
        <div class="form_row">
            <div class="check_box">
                <input type="checkbox" class="check" id="check1" name="selectall" onclick="selectAll(this)" />
                <label for="check1">전체동의</label>
            </div>
            <div class="check_box">
                <input type="checkbox" class="check" id="check2" name="term" onclick="checkSelectAll(this)"/>
                <label for="check2">서비스 약관</label>
                <span onclick="location.href='/term/${SERVICE}'">보기</span>
            </div>
        </div>
        <div class="form_row">
            <div class="check_box">
                <input type="checkbox" class="check" id="check3" name="term" onclick="checkSelectAll(this)"/>
                <label for="check3">개인정보 수집/이용</label>
                <span onclick="location.href='/term/${PERSONAL_INFO}'">보기</span>
            </div>
            <div class="check_box">
                <input type="checkbox" class="check" id="check4" name="term" onclick="checkSelectAll(this)"/>
                <label for="check4">개인정보 제3자제공</label>
                <span onclick="location.href='/term/${PERSONAL_INFO_SUPPLY}'">보기</span>
            </div>
        </div>
        <button id="btn" class="btn btn-black on">서비스 가입(결제) 하기</button>
        <div class="notice">
            매월 부가세 별도 15,000원이 부과됩니다. 가입 첫 달 or 두 번째 달은
            무료체험 기간을 반영하여 일한 계산됩니다. 매월 1일~말일까지 서비스
            이용금액을 전월 20일 선 결제로 진행됩니다.
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
    <form id="form" name="form_auth_result" action="/user/join" method="post">
        <input type = "hidden" name="name" id="name">
        <input type = "hidden" name="phoneNum" id="phoneNum">
        <input type = "hidden" name="userCid" id="userCid">
        <input type = "hidden" name="encodeData" id="encodeData">
    </form>

    <!-- 올앳 결제창에 절달하는 데이터 -->
    <form name="form_allat" method="post">
        <%--  필수 정보   --%>
        <input type="hidden" name="allat_encode_type"  value="U">
        <input type="hidden" name="allat_shop_id"      value="">
        <input type="hidden" name="allat_order_no"     value="">
        <input type="hidden" name="shop_receive_url"   value="">
        <input type="hidden" name="allat_enc_data"     value="">
        <input type="hidden" name="name"               value="${name}">
        <input type="hidden" name="phoneNum"           value="${phoneNum}">
        <input type="hidden" name="userCid"            value="${userCid}">
        <input type="hidden" name="reserveId"          value="">
        <input type="hidden" name="mediaKey"           value="">
        <input type="hidden" name="mediaUserKey"       value="">
        <%--  옵션 정보   --%>
        <input type="hidden" name="allat_amt"          value="">
        <input type="hidden" name="allat_product_nm"   value="">
        <input type="hidden" name="allat_test_yn"      value="">
    </form>
</section>
<script language='javascript'>
    var ssStorage = storage(StorageType.SESSION_TYEP),
        sMediaKey = ssStorage.getDecodeData("mediaKey"),
        sMediaUserKey =  ssStorage.getDecodeData("mediaUserKey");

    $(document).ready(function() {
        phoneFormat();

        // 본인인증을 위한 암호화 요청
        $('#req_auth').click(function (){
            $.ajax({
                type: 'GET',
                url: '/auth/request/encdata?type=${join}',
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

        // 가입(결제) 하기 버튼
        $('#btn').click(function (){
            if(isSelectAll()) {
                // 제휴 PG사 확인
                $.ajax({
                    type: "GET",
                    url: "/payment/pg?mediaKey=" + sMediaKey,
                    contentType: 'application/json',
                    success:function(pg){
                        // localhost 로 테스트 시 크로스 도메인 문제로 발생하는 오류
                        $.support.cors = true;

                        var param = {
                            mediaKey : sMediaKey,
                            mediaUserKey : sMediaUserKey,
                            userCid : '${userCid}',
                            name : '${name}',
                            phoneNum : '${phoneNum}',
                            encodeData : '${encodeData}'
                        };

                        $.ajax({
                            type: "POST",
                            url: "/payment/" + pg + "/regist",
                            data : JSON.stringify(param),
                            contentType: 'application/json',
                            success:function(data){
                                switch (pg) {
                                    case '${PAYCO}':
                                        window.open(data, 'popupPayco', 'top=100, left=300, width=727px, height=512px, resizble=no, scrollbars=yes');
                                        break;
                                    case '${ALLAT}':
                                        $('input[name=allat_shop_id]').attr('value',data.allat_shop_id);
                                        $('input[name=allat_order_no]').attr('value',data.allat_order_no);
                                        $('input[name=shop_receive_url]').attr('value',data.shop_receive_url);
                                        $('input[name=allat_amt]').attr('value',data.allat_amt);
                                        $('input[name=allat_test_yn]').attr('value',data.allat_test_yn);
                                        $('input[name=reserveId]').attr('value',data.reserveId);

                                        // 결제페이지 호출 (NonAllatPayRE.js)
                                        Allat_Plus_Fix(form_allat, "0", "0");
                                        break;
                                }
                            },
                            error: function(request){
                                if(request.responseJSON === undefined){
                                    alert("가입 진행 중 에러가 발생했습니다.");
                                } else if(request.responseJSON.code === '99') {
                                    alert(request.responseJSON.msg);
                                }
                            }
                        });
                    },
                    error: function(){
                        alert("올바른 경로로 다시 접속해주세요.");
                        location.href = '/main';
                    }
                });
            } else{
                alert("서비스 가입 약관 전체 동의 해주세요.");
                return false;
            }
        });
    });


    // 올앳 인증 결과값 반환( allat-receive.jsp 페이지에서 호출 )
    function allat_result_submit(result_cd, result_msg, enc_data) {
        Allat_Plus_Close();

        if(result_cd === '0000') {
            form_allat.allat_enc_data.value = enc_data;
            form_allat.mediaKey.value = sMediaKey;
            form_allat.mediaUserKey.value = sMediaUserKey;
            form_allat.action = "/payment/allat";
            form_allat.method = "post";
            form_allat.target = "_self";
            form_allat.submit();
        } else if(result_cd !== '9998'){
            alert("가입 진행 중 오류가 발생했습니다.");
        }
    }

    // 서비스 약관 전체동의 해제
    function checkSelectAll(checkbox)  {
        const selectall = document.querySelector('input[name="selectall"]');

        if(checkbox.checked === false)  {
            selectall.checked = false;
        }
    }

    // 서비스 약관 전체동의 활성화
    function selectAll(selectAll)  {
        const checkboxes = document.getElementsByName('term');

        checkboxes.forEach((checkbox) => {
            checkbox.checked = selectAll.checked
        })
    }

    // 서비스 약관 전체동의 여부 체크
    function isSelectAll()  {
        var isSelectAll = true;
        const checkboxes = document.getElementsByName('term');

        checkboxes.forEach((checkbox) => {
            if(checkbox.checked === false){
                isSelectAll = false;
            }
        })
        return isSelectAll;
    }

    // 본인인증 팝업
    function fnPopup(){
        window.open('', 'popupChk', 'width=500, height=550, top=100, left=100, fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no');
        document.form_auth.action = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb";
        document.form_auth.target = "popupChk";
        document.form_auth.submit();
    }
</script>