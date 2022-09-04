<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="com.stock.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="join" value="<%=Const.JoinStatus.JOIN.name()%>" />
<c:set var="SERVICE" value="<%=Const.TermType.SERVICE.name()%>" />
<c:set var="PERSONAL_INFO" value="<%=Const.TermType.PERSONAL_INFO.name()%>" />
<c:set var="PERSONAL_INFO_SUPPLY" value="<%=Const.TermType.PERSONAL_INFO_SUPPLY.name()%>" />
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
        <div class="sub_title red">휴대전화 본인인증이 필요합니다.</div>
        <div class="text">
            실명인증으로 가입을 진행하고 있습니다.<br />입력하신 개인
            정보는 가입완료 전까지 저장되지 않습니다.
        </div>
        <button id="req_auth" class="btn btn-black on">휴대전화 본인인증</button>
    </div>
    <div class="service">
        <div class="title">서비스가입 약관 동의</div>
        <form action="">
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
            <button id="btn" class="btn btn-gray on">서비스 가입(결제) 하기</button>
            <div class="notice">
                매월 부가세 별도 15,000원이 부과됩니다. 가입 첫 달 or 두 번째 달은
                무료체험 기간을 반영하여 일한 계산됩니다. 매월 1일~말일까지 서비스
                이용금액을 전월 20일 선 결제로 진행됩니다.
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
    <form id="form" name="form_auth_result" action="/user/join" method="post">
        <input type = "hidden" name="name" id="name">
        <input type = "hidden" name="phoneNum" id="phoneNum">
        <input type = "hidden" name="userCid" id="userCid">
        <input type = "hidden" name="encodeData" id="encodeData">
    </form>
</section>
<script language='javascript'>
    var ssStorage = storage(StorageType.SESSION_TYEP),
        paramManager = ParamManager();

    ssStorage.setEncodeData("mediaUserKey", '${mediaUserKey}');
    var mediaKey = ssStorage.getDecodeData("mediaKey"),
        mediaUserKey =  ssStorage.getDecodeData("mediaUserKey");

    if (!mediaKey || !mediaUserKey) {
        alert("올바른 경로로 다시 접속해주세요.");
        location.href = '/main';
    }

    // 결제 중 에러 발생했을 시 팝업창 종료를 위함
    if(paramManager.get("error")){
        window.close();
    }

    $(document).ready(function() {
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
            alert("휴대전화 본인인증이 되지 않았습니다.");
            return false;
        });
    });

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

    // 본인인증 팝업
    function fnPopup(){
        // 1) 윈도우 팝업 => 원래 코드
        window.open('', 'popupChk', 'width=500, height=550, top=100, left=100, fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no');
        document.form_auth.target = "popupChk";
        document.form_auth.action = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb";
        document.form_auth.submit();
    }
</script>