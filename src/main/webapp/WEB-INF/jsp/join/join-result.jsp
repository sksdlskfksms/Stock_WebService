<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<header>
    <div class="back" onclick="javascript:history.back();"></div>
     가입완료
</header>
<div class="main">
    <div class="title">가입이<br>완료되었습니다.</div>
    <img src="/images/sign_main.png" alt="" />
</div>
<div class="real_name">
    <div class="title">가입정보</div>
    <div class="data_text">
        <div>가입자명 : ${name}</div>
        <div id="phoneFormat">휴대폰 번호 : ${phoneNum}</div>
        <div>가입일 : ${joinDate}</div>
    </div>
    <button class="btn btn-black on" onclick="goMain()">주식 정보 더보기</button>
</div>
<script language='javascript'>
    var ssStorage = storage(StorageType.SESSION_TYEP),
        mediaUserKey =  ssStorage.getDecodeData("mediaUserKey");

    function goMain(){
        location.href='/main?mediaUserKey=' + mediaUserKey;
    }

    $(document).ready(function() {
        if(window.opener != undefined){
            opener.parent.location = document.location.href;
            window.close();
        }

        phoneFormat();
    });
</script>