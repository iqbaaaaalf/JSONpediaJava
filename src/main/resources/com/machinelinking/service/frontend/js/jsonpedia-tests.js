
asyncTest("annotate", function() {
    expect(1);
    c = new JSONpedia();
    c.annotate('en:Albert_Einstein').extractors()
            .linkers().splitters().structure().validate()
            .json()
            .done(
                function (data) {
                    ok(true, 'Loaded data: ' + data);
                    start();
                }
            )
            .fail(
                function(err) {
                    ok(false, 'Error while loading data: ' + err);
                    start();
                }
            );
});

asyncTest("mongo select", function() {
    expect(1);
    c = new JSONpedia();
    c.mongo().select('_id = #736 -> title', '__type : link', 1)
            .done(
                function (data) {
                    ok(true, 'Loaded data: ' + data);
                    start();
                }
            )
            .fail(
                function(err) {
                    ok(false, 'Error while loading data: ' + err);
                    start();
                }
            );
});

asyncTest("mongo mapred", function() {
    expect(1);
    c = new JSONpedia();
    c.mongo().mapred(
            '_id = #736',
            'function() { ocs = this.content.templates.occurrences; for(template in ocs) { emit(template, ocs[template]); } }',
            'function(key, values) { return Array.sum(values) }',
            10)
            .done(
                function (data) {
                    ok(true, 'Loaded data: ' + data);
                    start();
                }
            )
            .fail(
                function(err) {
                    ok(false, 'Error while loading data: ' + err);
                    start();
                }
            );
});

asyncTest("elastic select", function() {
    expect(1);
    c = new JSONpedia();
    c.elastic().select('Albert Einstein', '__type : link', 1)
            .done(
                function (data) {
                    ok(true, 'Loaded data: ' + data);
                    start();
                }
            )
            .fail(
                function(err) {
                    ok(false, 'Error while loading data: ' + err);
                    start();
                }
            );
});