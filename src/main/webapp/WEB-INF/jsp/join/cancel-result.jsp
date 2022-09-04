<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<header>
    <div class="back" onclick="javascript:history.back();"></div>
    가입완료
</header>
<section>
    <div class="main">
        <div class="title">가입 해지가<br />완료되었습니다.</div>
        <img src="/images/cencel_main.png" alt="" />
    </div>
    <div class="real_name">
        <div class="title">가입정보</div>
        <div class="data_text">
            <div>가입자명 : ${name}</div>
            <div id="phoneFormat">휴대폰번호 : ${phoneNum}</div>
            <div>가입일 : ${joinDate}</div>
            <div>해지일 : ${cancelDate}</div>
        </div>
        <div class="notice">
            ※ 선 결제된 해지월 요금은 일할 계산 되어 부분 취소 청구됩니다.
        </div>
        <button class="btn btn-black on" onclick="location.href='/main'">서비스 메인</button>
    </div>
</section>
<script language='javascript'>
    $(document).ready(function() {
        phoneFormat();
    });
</script>