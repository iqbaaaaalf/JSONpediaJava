# JSONpedia

### Requirements:
- Gradle 1.12

### Compile and build jsonpedia:

Jsonpedia can be compiled by issuing the following command:

```bash
$ gradle build  # this will run tests
```

Some of the tests might fail as they expect to find mongodb and elasticsearch installed.

To compile jsonpedia without running tests execute: 
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

### Run CLI:

```bash
$ java -cp build/libs/jsonpedia-{VERSION}-full.jar com.machinelinking.exporter.CSVExporterCLI
```

Running without arguments will produce this usage message:
```
   Usage: <main class> [options]
     Options:
     * --in, -i        Input file
     * --out, -o       Output file
     * --prefix, -p    Page prefix
       --threads, -t   Number of threads
                       Default: 0
```
### CLI Usage examples

```bash
$ java -cp build/libs/jsonpedia-{VERSION}-full.jar \
  com.machinelinking.exporter.CSVExporterCLI \
  -p http://en.wikipedia.org/wiki -i src/test/resources/enwiki-latest-pages-articles-p3.xml.gz -o out.csv
```
```
Processing with 2 threads
Closing process...
Done.
processor: Processed pages: 25, elapsed time: 0 (ms), exceptions: []
templates: 1117, properties 4907, max properties/template: 52, avg properties/template: 4,393017
```

### Run the web interface
```
$ echo "server.host = 127.0.0.1" > /tmp/conf.properties
$ echo "server.port = 8080" > /tmp/conf.properties
$ java -jar build/libs/jsonpedia-{VERSION}-full.jar -c /tmp/conf.properties
```

You can now connect to ```http://127.0.0.1:8080/frontend/index.html``` in your browser and use the web interface!
