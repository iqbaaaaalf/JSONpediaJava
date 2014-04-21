/*
 * JSONpedia v1.1 jquery plugin.
 */

function JSONpedia() {
    var _params;
    var _deferred = $.Deferred();


    function perform(params) {
        if(params.performing) return;
        params.performing = true;
        request = '/annotate/resource/' + params.out + '/' + params.entity + '?&procs=' + params.processors.join(',');
        console.log('Performing request: ' + request);
        $.get(request)
                .done(
                    function(data){ _deferred.resolve(_params, data, null) }
                )
                .fail(
                    function(xhr, status, error) {_deferred.resolve(_params, null, error + "[" + xhr.status + "]") }
                );
    }

    function handleReply(params, data, err) {
        if(params.handled) return;
        params.handled = true;
        if(data) params.done(data);
        if(err)  params.fail(err);
    }

    _handlers = {
        done: function(callback) {
            _params.done = callback;
            _deferred.done(handleReply);
            perform(_params);
            return _handlers;
        },
        fail: function(callback) {
            _params.fail = callback;
            _deferred.done(handleReply);
            perform(_params);
            return _handlers;
        }
    };

    _methods = {
        extractors: function () {
            _params.processors.push('Extractors');
            return _methods;
        },
        linkers: function () {
            _params.processors.push('Linkers');
            return _methods;
        },
        splitters: function () {
            _params.processors.push('Splitters');
            return _methods;
        },
        structure: function () {
            _params.processors.push('Structure');
            return _methods;
        },
        validate: function () {
            _params.processors.push('Validate');
            return _methods;
        },
        json: function () {
            _params.out = 'json';
            return _handlers;
        },
        html: function () {
            _params.out = 'html';
            return _handlers;
        }
    };

    _annotate = function (entity) {
        _params = { processors : [] };
        _params.entity = entity;
        return _methods;
    };

    return { annotate : _annotate };
}

