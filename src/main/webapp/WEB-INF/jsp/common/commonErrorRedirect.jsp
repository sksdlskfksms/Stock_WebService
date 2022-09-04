<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="error" style="width:100%; min-height:500px; display:none; background:#fff; ">
    <div style="text-align: center;">
        <img src="/images/error.png" alt="" style="width:50px; margin-bottom: 10px; padding-top:20px" />
    </div>
    <div style="text-align: center;font-size: 16px;letter-spacing: -1px;font-weight: bold;">
        이용에 불편을 드려 죄송합니다.
    </div>
    <div style="text-align: center;font-size: 12px;letter-spacing: -1px;font-weight: normal;margin-top: 20px;">
        오류가 발생하였습니다.
    </div>
</div>
<script type="text/javascript">
    var msg = "${message}";
    var url = "${url}";

    if(msg === 'ERROR'){
        document.getElementById("error").style.display = 'table';
    }else {
        if (msg) alert(msg);
        location.href = url;
    }
</script>

