# JSONpedia README

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
See [documentation](/hardest/jsonpedia/src/19e316066249c126f4e8e067321eaafcea566721/documentation.md) for further references.

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
$ java -jar build/libs/jsonpedia-{VERSION}-full.jar -c /tmp/conf.properties
```

You can now connect to ```http://127.0.0.1:8080/frontend/index.html``` in your browser and use the web interface!

## The StorageLoader tool

The StorageLoader tool loads the data extracted from a specific wikimedia dump into the configured storages.
In can be run massively using the 

```bash
$ bin/loader.py
```
wrapper script or by specifying a dump:

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

## The CSV exporter tool

```bash
$ java -cp build/libs/jsonpedia-{VERSION}-full.jar com.machinelinking.exporter.CSVExporterCLI
```

Running without arguments will produce this usage message:

```bash
   Usage: <main class> [options]
     Options:
     * --in, -i        Input file
     * --out, -o       Output file
     * --prefix, -p    Page prefix
       --threads, -t   Number of threads
                       Default: 0
```

# License

<a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">JSONpedia</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="http://michelemostarda.it" property="cc:attributionName" rel="cc:attributionURL">Michele Mostarda</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International License</a>.