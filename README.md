# JSONpedia README

# Quick Tutorial

The code snippet below shows how to retrieve the JSON structure of the WikiText DOM of the http://en.wikipedia.org/wiki/London page.
Then a filtering over sections and over references inside sections is performed, the fitered elements are rendered as HTML. 

```java
import com.machinelinking.main.JSONpedia;
import org.codehaus.jackson.JsonNode;

JSONpedia jsonpedia = JSONpedia.instance();
JsonNode root = jsonpedia.process("en:London").flags("Structure").json();

JsonNode[] sections = jsonpedia.applyFilter("@type:section", root);
String firstSectionHTML = jsonpedia.render("en:London", sections[0]);

JsonNode[] allReferencesInSections = jsonpedia.applyFilter("@type:section>@type:reference", root);
String allReferencesHTML = jsonpedia.render("en:London", allReferencesInSections);
```

## What is JSONpedia?
       
JSONpedia is a library, a REST service and a CLI tool meant to parse, elaborate and convert WikiText documents to JSON,
in order to facilitate the consumption of the huge availability of the MediaWiki semi-structured content.

It has been initially designed to extract linguistic resources from the Wikipedia dump and to perform massive data scraping. 

This library provides:
 
- a set of classes and code samples (test cases) to build complex processing pipelines over WikiText parsing events; 
- a REST service to convert MediaWiki content in realtime by passing a URI or WikiText;
- a web frontend to play with the library functionalities;
- a set of CLI tools to process the Wikipedia dump and store it into MongoDB and Elasticsearch, manipulate data and 
  perform transformation in UNIX command pipeline style;
- a REST service to query the MongoDB and Elasticsearch content;
- and [**even more**](https://bitbucket.org/hardest/jsonpedia/issues) to come.

An online version can be found at [jsonpedia.org](http://jsonpedia.org).

## Main features
- WikiText event parser
- Configurable event processing pipeline
- WikiMedia template processing support
- DBpedia template mapping integration
- HTML rendering support
- ElasticSearch storage support
- MongoDB storage support
- REST interface
- Web frontend
- CLI interface

## Documentation

See [documentation](/hardest/jsonpedia/src/HEAD/documentation.md) for further references.

## Requirements
- Gradle 1.12
- Optionally ElasticSearch 1.1
- Optionally MongoDB 2.6

## Compile and build JSONpedia
JSONpedia can be compiled by issuing the following command:

```bash
$ gradle build  # this will run tests
```

Some of the tests might fail as they expect to find MongoDB and Elasticsearch installed.

To compile JSONpedia without running tests execute: 
```
gradle build -x tests
```

When the compilation finishes the resulting binary can be found in ```build/libs```
A self-contained jar can also be built by issuing 
```bash
$ gradle fullCapsule
```

The resulting jar will also be in ```build/libs``` with the name ```jsonpedia-{VERSION}-full.jar```.

Please note that building a capsule will not run any tests.

## Run the web interface

```bash
$ echo "server.host = 127.0.0.1" > /tmp/conf.properties
$ echo "server.port = 8080" > /tmp/conf.properties
$ java -cp build/libs/jsonpedia-{VERSION}-full.jar com.machinelinking.cli.server -c /tmp/conf.properties
```

You can now connect to ```http://127.0.0.1:8080/frontend/index.html``` in your browser and use the web interface!

## Run the Storage Loader

The _loader.py_ tool helps to process the latest online Wikipedia dumps and index them into the configured storages.
Details about the usage of this script can be found in its header.

To perform massing loading of first 10 Wikipedia dumps using the default configuration run: 

```bash
$ bin/loader.py conf/default.properties 10
```

```bash
Retrieved latest articles links: [ ... 'enwiki-latest-pages-articles27.xml-p029625017p043536594.bz2']
Processing article 0 - link: enwiki-latest-pages-articles1.xml-p000000010p000010000.bz2 file: work/enwiki-latest-pages-articles1.xml-p000000010p000010000.bz2
Start download ...
Download complete in 12 sec.
Start ingestion ...
Ingestion completed in 63 sec.
[...]
```

To process a single dump manually:

```bash
$ java -cp build/libs/jsonpedia-{VERSION}-full.jar \
  com.machinelinking.cli.loader \
  -p http://en.wikipedia.org/wiki -i src/test/resources/enwiki-latest-pages-articles-p3.xml.gz -o out.csv
```

```bash
Processing with 2 threads
Closing process...
Done.
processor: Processed pages: 25, elapsed time: 0 (ms), exceptions: []
templates: 1117, properties 4907, max properties/template: 52, avg properties/template: 4,393017
```

## Run the Storage Facet Loader

The _facet_loader.py_ tool helps to produce a facet index based on an existing index in Elasticsearch.
Details about the usage of this script can be found in its header.

Example: process all documents in db 'jsonpedia_test_load' collection 'en' following configuration specified in 
'faceting.properties' and store the generated documents in db 'jsonpedia_test_facet' collection en.

```bash
$ bin/facet_loader.py -s localhost:9300:jsonpedia_test_load:en -d localhost:9300:jsonpedia_test_facet:en \
        -l 100 -c conf/faceting.properties
```

```bash
...
Executing command: gradle runFacetLoader -Pargs_line='-s localhost:9300:jsonpedia_test_load:en -d localhost:9300:jsonpedia_test_facet:en -l 100 -c conf/faceting.properties'
...
Facet Loading Report:           
Processed docs: 58, Generated facet docs: 1051
```

## Run the CSV Exporter

The CSV Exporter CLI tool allows to convert Wikipedia dumps to tabular data generated from page parsing.

To convert the gzipped dump in test resources using the page prefix of en Wikipedia with a mono thread processor run:

```bash
$ java -cp build/libs/jsonpedia-{VERSION}-full.jar com.machinelinking.cli.exporter \
    --prefix http://en.wikipedia.org \
    --in src/test/resources/dumps/enwiki-latest-pages-articles-p1.xml.gz \
    --out out.csv --threads 1
```

# License

![Creative Commons Attribution 4.0 International License Logo](https://i.creativecommons.org/l/by/4.0/88x31.png "Creative Commons Attribution 4.0 International License")