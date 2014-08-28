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

## The StorageLoader tool

The _loader.py_ tool helps to process the latest online Wikipedia dumps and index them into the configured storages.
Details about the usage of this script can be found in its header. 

```bash
$ bin/loader.py
```

To process a dump manually instead:

```bash
$ java -cp build/libs/jsonpedia-{VERSION}-full.jar \
  com.machinelinking.storage.DefaultJSONStorageLoader \
  -p http://en.wikipedia.org/wiki -i src/test/resources/enwiki-latest-pages-articles-p3.xml.gz -o out.csv
```

```bash
Processing with 2 threads
Closing process...
Done.
processor: Processed pages: 25, elapsed time: 0 (ms), exceptions: []
templates: 1117, properties 4907, max properties/template: 52, avg properties/template: 4,393017
```

# License

![Creative Commons Attribution 4.0 International License Logo](https://i.creativecommons.org/l/by/4.0/88x31.png "Creative Commons Attribution 4.0 International License")