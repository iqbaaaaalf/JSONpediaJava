/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

/*
 * JSONpedia v1.1 jquery plugin.
 */

function JSONpedia() {
    var _params;
    var _deferred = $.Deferred();

    function perform(params) {
        if(params.performing) return;
        params.performing = true;
        if('processors' in params) {
            request = '/annotate/resource/' + params.out + '/' + params.entity + '?&procs=' + params.processors.join(',');
        } else if('mongo' in params) {
            call = params.mongo[0];
            method = call[0];
            if('select' == method) {
                request = '/storage/mongo/select?q=' + encodeURIComponent(call[1]) +
                        "&filter=" + encodeURIComponent(call[2]) +
                        "&limit=" + call[3];
            } else if('mapred' == method) {
                request = '/storage/mongo/mapred?criteria=' + encodeURIComponent(call[1]) +
                        "&map=" + encodeURIComponent(call[2]) +
                        "&reduce=" + encodeURIComponent(call[3]) +
                        "&limit=" + call[4];
            } else throw new Error();
        } else if('elastic' in params) {
            call = params.elastic[0];
            request = '/storage/elastic/select?q=' + encodeURIComponent(call[1]) +
                        "&filter=" + encodeURIComponent(call[2]) +
                        "&limit=" + call[3];
        } else throw new Error();
        console.log('Performing request: ' + request);
        $.get(request)
                .done(
                function (data) {
                    _deferred.resolve(_params, data, null)
                }
        )
                .fail(
                function (xhr, status, error) {
                    _deferred.resolve(_params, null, error + "[" + xhr.status + "]")
                }
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

    _mongo_methods = {
        select: function(selector, filter, limit) {
            _params.mongo.push(['select', selector, filter, limit]);
            return _handlers;
        },
        mapred: function(criteria, map, reduce, limit) {
            _params.mongo.push(['mapred', criteria, map, reduce, limit]);
            return _handlers;
        }
    };

    _mongo = function() {
        _params = {mongo :[]};
        return _mongo_methods;
    };

    _elastic_methods = {
        select: function(selector, filter, limit) {
            _params.elastic.push(['select', selector, filter, limit]);
            return _handlers;
        }
    };

    _elastic = function() {
        _params = {elastic :[]};
        return _elastic_methods;
    };

    return {
        annotate: _annotate,
        mongo: _mongo,
        elastic: _elastic
    };
}

