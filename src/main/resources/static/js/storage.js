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
        /** storage type ??????. */
        changeStorageType : _changeStorageType,
        /** ???????????? ??? ??????. */
        setData : _setData,
        /** ???????????? ??? ??????. */
        getData : _getData,
        /** ??? ????????? ???????????? ??????. */
        setEncodeData : _setEncodeData,
        /** ???????????? ???????????? ????????? ?????? ??????. */
        getDecodeData : _getDecodeData,
        /** ???????????? ??? ??????. */
        removeData : _removeData,

        /** ???????????? ?????? ??????????????? ?????? (????????? ????????? ???????????? ??????) */
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

        /** ??????????????? ????????? ?????? ?????? ?????? ?????? ??????. */
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

        /** ??????????????? ??????. */
        setObject : _setValueObjectType,

        /** ?????? ?????? */
        getObject : _getValueObjectType,

        /** ??????????????? ????????? ?????? ?????? ?????? ??????. */
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

        /** ?????? ????????? ??????. */
        loadData : function (key) {
            key = key || _defaultKey;
            try {
                var data = _getValueObjectType(key);

                // ?????? ???????????? ???????????? ????????? ????????? ???????????? ??????.
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

        /** ?????? ???????????? ??????????????? ??????. */
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