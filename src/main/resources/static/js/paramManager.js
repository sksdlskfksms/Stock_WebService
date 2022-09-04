var ParamManager = function(m) {
    var _param = {};
    var _type = {
        build : {
            INCLUDE : 1,
            EXCLUDE : 2
        }
    };

    /** 파라미터 저장 */
    var _setParam = function(key, value) {
        _param[key] = value || '';
    };

    /** 파라미터 조회 */
    var _getParam = function(key) {
        return _param[key] || '';
    }
    /** 파라미터 존재여부 */
    var _existsParam = function(key) {
        try {
            return !!_getParam(key);
        } catch(e) {
            return false;
        }
    }

    /** 현재의 전체 파라미터 세팅. */
    var _currentGetParamSetting = function() {
        var seperator = '&&&&&&&&&&&&&&&&&&&&&&&&&&';
        var paramsString = location.search.substr(location.search.indexOf('?') + 1),
            paramsArray = paramsString.split('&');

        for(var i in paramsArray){
            paramsArray[i] = paramsArray[i].replace('=', seperator);
        }

        getkey = function(s) {
            try {
                return s.split(seperator)[0];
            } catch(e) {
                return '';
            }
        },
        getValue = function(s) {
            try {
                return s.split(seperator)[1];
            } catch(e) {
                return '';
            }
        };

        if (paramsArray) {
            paramsArray.forEach(function(s) {
                var key = getkey(s);
                if (key) {
                    _setParam(key, getValue(s));
                }
            });
        }
    };

    /**
     * 현재의 파라미터 정보로 파라미터 string형태로 빌드함.
     * @param params 포함 혹은 제외할 파라미터 array or string(,로 파라미터 연결)
     * @param buildType 포함 (ParamManager.buildType.INCLUDE or 1), 제외 (ParamManager.buildType.EXCLUDE or 2)
     * @returns {string} 완성된 파라미터 strings (예 - 'id=232')
     * @private
     */
    var _buildParam = function(params, buildType) {
        var buildParams = [];

        if (!_param) { return ''; }
        if (!Array.isArray(params) && params) {
            params = params.split(',');

            for(var i in params){
                params[i] = params[i].trim();
            }

        }
        if (!buildType) { buildType = _type.build.INCLUDE; }

        // 해당 파라미터를 포함시킴
        if (buildType === _type.build.INCLUDE) {
            for (var key in _param) {
                if (params.includes(key)) {
                    buildParams.push(key);
                }
            }
        }
        // 해당 파라미터를 제외시킴.
        else if (buildType === _type.build.EXCLUDE) {
            for (var key in _param) {
                if (!params.includes(key)) {
                    buildParams.push(key);
                }
            }
        }
        // 그외 파라미터 값은 예외처리.
        else {
            console.error('buildType 값이 잘못되었습니다. buildType is ' + buildType);
            return '';
        }

        var buildArray = [];
        for (var i in buildParams){
            buildArray.push(buildParams[i] + '=' + _param[buildParams[i]]);
        }

        return buildArray.join('&');
    };

    /**
     * 현재의 파라미터 정보로 uri정보를 생성함
     * @param params 포함 혹은 제외할 파라미터 array or string(,로 파라미터 연결)
     * @param buildType 포함 (ParamManager.buildType.INCLUDE or 1), 제외 (ParamManager.buildType.EXCLUDE or 2)
     * @returns {string} 완성된 uri (예 - '/m/event/theme?id=232')
     * @private
     */
    var _buildUri = function(params, buildType) {
        var paramsString = _buildParam(params, buildType);
        return window.location.pathname + (paramsString ? '?' + paramsString : '');
    };

    /**
     * 현재의 파라미터 정보로 전체 url정보를 생성함.
     * @param params 포함 혹은 제외할 파라미터 array or string(,로 파라미터 연결)
     * @param buildType 포함 (ParamManager.buildType.INCLUDE or 1), 제외 (ParamManager.buildType.EXCLUDE or 2)
     * @returns {string} 완성된 url (예 - 'http://{domain}:{port}/m/event/theme?id=232')
     * @private
     */
    var _buildUrl = function(params, buildType) {
        return window.location.origin + _buildUri(params, buildType);
    };

    /**
     * 브라우저 주소창에 노출되는 url 세팅.
     * @param params 제외할 파라미터 array or string(,로 파라미터 연결)
     * @private
     */
    var _setSkipParams = function(params) {
        history.replaceState({}, null, _buildUri(params, _type.build.EXCLUDE));
    };


    // 함수 실행시 현재 파라미터를 모두 세팅함.
    _currentGetParamSetting();

    return {
        buildType : _type.build,
        init : _currentGetParamSetting,
        get : _getParam,
        set : _setParam,
        exists : _existsParam,
        build : _buildParam,
        buildUrl : _buildUrl,
        buildUri : _buildUri,
        setUrlSkipParams : _setSkipParams
    }
};