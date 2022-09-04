<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
    * { display: none; }
</style>
<script>
    var frm = document.frm;
    opener.document.form_auth_result.name.value = '${name}';
    opener.document.form_auth_result.phoneNum.value = '${phoneNum}';
    opener.document.form_auth_result.userCid.value = '${userCid}';
    opener.document.form_auth_result.encodeData.value = '${encodeData}';
    opener.document.form_auth_result.submit();

    var win = window.open('','_self');
    win.close();
</script>
