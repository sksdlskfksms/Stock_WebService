<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script language=JavaScript charset='euc-kr' src="https://tx.allatpay.com/common/NonAllatPayREPlus.js"></script>
<head>
    <title>결제취소</title>
</head>
<body>
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
</body>
<style>
    * { display: none; }
</style>
<script>
    $.ajax({
        type: "POST",
        url: "/payment/allat/cancel/request",
        contentType: 'application/json',
        data : JSON.stringify({ userCid : '${userCid}'}),
        success:function(data){
            $('input[name=allat_shop_id]').attr('value',data.allat_shop_id);
            $('input[name=allat_fix_key]').attr('value',data.allat_fix_key);
            $('input[name=shop_receive_url]').attr('value',data.shop_receive_url);
            $('input[name=allat_test_yn]').attr('value',data.allat_test_yn);

            // 암호화 데이터 생성 (NonAllatPayRE.js)
            Allat_Plus_Fix_Cancel(form_allat);
        },
        error: function() {
            alert("결제 취소 중 오류가 발생했습니다. 고객센터로 문의주세요.");
        }
    });

    // 올앳 인증 결과값 반환( allat-receive.jsp 페이지에서 호출 )
    function allat_result_submit(result_cd,result_msg,enc_data) {
        Allat_Plus_Close();

        if (result_cd === '0000') {
            // 결제 취소
            var param = {
                userCid : '${userCid}',
                encodeData : enc_data
            };

            $.ajax({
                type: "POST",
                url: "/payment/cancel",
                data : JSON.stringify(param),
                contentType: 'application/json',
                success:function(){
                    // 결제 취소 완료
                    location.href='/user/join';
                },
                error: function() {
                    alert("결제 취소 중 오류가 발생했습니다. 고객센터로 문의주세요.");
                }
            });

        } else {
            alert("결제 취소 중 오류가 발생했습니다. 고객센터로 문의주세요.");
        }
    }

</script>

