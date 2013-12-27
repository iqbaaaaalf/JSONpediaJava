
asyncTest("Annotation test", function() {
    expect(1);
    c = new Client();
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