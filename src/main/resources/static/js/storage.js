var StorageType = {
    LOCAL_TYPE : 1,
    SESSION_TYEP : 2
};

var storage = function(storageType) {

    // default storage type
    var _defaultKey = 'default-storage-key',
        _formSaveKey = 'save-storage-key',
        _formEncodedKey = 'required-encoded',
        _storage = function(type) {
            switch (type) {
                case StorageType.SESSION_TYEP:
                    return sessionStorage;
                case StorageType.LOCAL_TYPE:
                default:
                    return localStorage;
            }
        }(storageType),
        _setExpired = function(key, expires) {
            if (!isNaN(expires)) {
                var expiresObject = {
                    times : expires,
                    regist : (new Date()).getTime()
                };
                _storage.setItem(key + '-expires', JSON.stringify(expiresObject));
            }
        },
        _isExpired = function(key) {
            var expiresObject = _storage.getItem(key + '-expires'),
                expired = false;

            if (expiresObject) {
                expiresObject = JSON.parse(expiresObject);

                var now = (new Date()).getTime(),
                    expect = expiresObject.regist + (expiresObject.times * 60 * 60 * 1000);
                return now > expect;
            }

            return expired;
        },
        _removeExpired = function() {
            _storage.removeItem(key + '-expires');
        },
        _encode = function(value) {
            if (!value) return '';
            return btoa(encodeURIComponent(value));
        },
        _decode = function(value) {
            if (!value) return '';
            return decodeURIComponent(atob(value));
        },
        _changeStorageType = function (type) {
            switch (type) {
                case StorageType.LOCAL_TYPE:
                    _storage = localStorage;
                    break;
                case StorageType.SESSION_TYEP:
                    _storage = sessionStorage;
                    break;
            }
        },
        _setData = function (key, data, expires) {
            key = key || _defaultKey;
            if (!data) return;
            if (_storage) {
                _storage.setItem(key, data);
                _setExpired(key, expires);
            }
        },
        _getData = function (key) {
            key = key || _defaultKey;
            if (_storage) {
                if (!_isExpired(key)) {
                    return _storage.getItem(key);
                }
            }
            return '';
        },
        _setEncodeData = function (key, data, expires) {
            _setData(key, _encode(data), expires);
        },
        _getDecodeData = function (key) {
            return _decode(_getData(key));
        },
        _removeData = function (key) {
            key = key || _defaultKey;
            try {
                if (_storage) {
                    _storage.removeItem(key);
                    _removeExpired(key);
                }
            } catch (e) {
                console.error(e);
            }
        },
        _getValueObjectType = function(key) {
            try {
                return JSON.parse(_getData(key)) || {};
            } catch(e) {
                return {};
            }
        },
        _setValueObjectType = function(key, data, expires) {
            try {
                return _setData(key, JSON.stringify(data) || {}, expires);
            } catch(e) {
            }
        };

    return {
        /** storage type 변경. */
        changeStorageType : _changeStorageType,
        /** 스토리지 값 세팅. */
        setData : _setData,
        /** 스토리지 값 조회. */
        getData : _getData,
        /** 값 암호화 처리하여 저장. */
        setEncodeData : _setEncodeData,
        /** 인코딩된 데이터를 복호화 하여 조회. */
        getDecodeData : _getDecodeData,
        /** 스토리지 값 삭제. */
        removeData : _removeData,

        /** 스토리지 값을 객체형태로 저장 (공통의 키값을 사용하기 위함) */
        setAttr : function(key, id, value, encoding, expires) {
            key = key || _defaultKey;
            if (!id) return;
            if (!value) return;
            try {
                var data = _getValueObjectType(key);
                data[id] = encoding === true ? _encode(value) : value;
                _setValueObjectType(key, data, expires);
            } catch(e) {
            }
        },

        /** 객체형태로 저장된 값의 해당 일부 속성 조회. */
        getAttr : function(key, id, encoding) {
            key = key || _defaultKey;
            if (!id) return '';
            try {
                var data = _getValueObjectType(key);
                return encoding === true ? _decode(data[id]) : data[id];
            } catch(e) {
                return ''
            }
        },

        /** 객체형태로 저장. */
        setObject : _setValueObjectType,

        /** 객체 조회 */
        getObject : _getValueObjectType,

        /** 객체형태로 저장된 값의 일부 속성 삭제. */
        removeAttr : function(key, id) {
            key = key || _defaultKey;
            if (!id) return;
            try {
                var data = _getValueObjectType(key);
                delete data[id];
            } catch(e) {
                console.error(e);
            }
        },

        /** 초기 데이터 세팅. */
        loadData : function (key) {
            key = key || _defaultKey;
            try {
                var data = _getValueObjectType(key);

                // 세션 스토리지 저장소에 저장할 대상을 반복하여 로드.
                if (data) {
                    $('.save-storage').each(function (i, e) {
                        var $input = $(e),
                            key = $input.data(_formSaveKey),
                            requiredEncoded = $input.data(_formEncodedKey),
                            value = data[key];

                        if (value) {
                            if (requiredEncoded) {
                                value = _decode(value);
                            }

                            if ($input.is('input')) {
                                $input.val(value);
                            } else if ($input.is('select')) {
                                var $options = $input.find('option');

                                $options.prop('selected', false);
                                $options.filter('[value="' + value + '"]').prop('selected', true);
                            } else if ($input.is('textarea')) {
                                $input.val(value);
                            }
                        }
                    });
                }
            } catch (e) {
                console.error(e);
            }
        },

        /** 대상 데이터를 스토리지에 저장. */
        saveData : function (key) {
            key = key || _defaultKey;
            try {
                var data = _getValueObjectType(key);

                $('.save-storage').each(function (i, e) {
                    var $input = $(e),
                        key = $input.data(_formSaveKey),
                        requiredEncoded = $input.data(_formEncodedKey),
                        value = $input.val();

                    if (value) {
                        data[key] = function (v) {
                            if (requiredEncoded) {
                                return _encode(v);
                            }
                            return v;
                        }(value);
                    }
                });

                this.setData(key, JSON.stringify(data));
            } catch (e) {
                console.error(e);
            }
        }
    };
};