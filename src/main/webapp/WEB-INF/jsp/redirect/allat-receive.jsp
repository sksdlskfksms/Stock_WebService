<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <title>결제중</title>
</head>
<style>
    * { display: none; }
</style>
<script>
    if(window.opener != undefined) {
        opener.result_submit('${sResultCd}','${sResultMsg}','${sEncData}');
        window.close();
    }else {
        parent.allat_result_submit('${sResultCd}','${sResultMsg}','${sEncData}');
    }
</script>

