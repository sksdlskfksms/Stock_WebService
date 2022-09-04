// 핸드폰 번호에 하이픈 추가
function phoneFormat(){
    var str = $('#phoneFormat').text().trim();
    var phone = str.replace(/(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/,"$1-$2-$3");
    $('#phoneFormat').text(phone);
}

// 팝업창
var PopupUtil = {
    'close' : function( __id__){
        var msgPopup = $('div[name='+__id__+']');
        if (msgPopup)  {
            $(msgPopup).remove();
        }
    },
    'open': function(__id__, title, message, okCallback, cancelCallback){
        var msgPopup = $('div[name='+__id__+']');
        var scroll1 = parseInt(($(this).scrollTop()/2));

        if (msgPopup)  {
            $(msgPopup).remove();
        }

        var tmplString = $('#'+__id__+'').html();
        $(document.body).append(tmplString);

        $('div[name='+__id__+']').bind('touchmove',function(e){
            e.preventDefault();
        });

        var okCallbackFn = undefined;
        var cancelCallbackFn = undefined;

        if (okCallback == undefined) {
            okCallbackFn = function(){
                PopupUtil.close(__id__);
            }
        } else {
            okCallbackFn = function(){
                PopupUtil.close(__id__);
                okCallback();
            }
        }

        if (cancelCallback == undefined ) {
            cancelCallbackFn = function(){
                PopupUtil.close(__id__);
            }
        } else {
            cancelCallbackFn = function(){
                PopupUtil.close(__id__);
                cancelCallback();

            }
        }
        $('div[name='+__id__+']').children().children('.popup_wrap').css('top',window.scrollTop/2);
        if (navigator.platform) {
            var filter = "win16|win32|win64|mac|macintel";
            if (filter.indexOf(navigator.platform.toLowerCase()) > 0) {
                $('.popup_wrap').css({'top': scroll1 + 'px'});
            }
        }
        $('div[name='+__id__+']').find('[name=__OK_BTN__]').click(okCallbackFn);
        $('div[name='+__id__+']').find('[name=__CANCEL_BTN__]').click(cancelCallbackFn);
    }
};
