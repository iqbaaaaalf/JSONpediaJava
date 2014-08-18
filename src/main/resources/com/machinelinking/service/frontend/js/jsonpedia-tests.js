
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