#!/usr/bin/env python
# -*- coding: utf-8 -*-

LATEST_DUMPS = 'http://dumps.wikimedia.org/enwiki/latest/'

WORK_DIR = 'work'

MVN_BIN = 'mvn'
MVN_HEAP_SIZE = '8g'
LOADER = 'com.machinelinking.storage.DefaultJSONStorageLoader'
LOADER_CONFIG = 'default.properties'


import urllib
import lxml
import os
import re
import time
import subprocess

from lxml import html


def atoi(text):
    return int(text) if text.isdigit() else text


def natural_keys(text):
    return [ atoi(c) for c in re.split('(\d+)', text) ]


def get_latest_articles_list():
    dumps_page = urllib.urlopen(LATEST_DUMPS).read()
    dumps_page_dom = lxml.html.fromstring(dumps_page)
    download_links = [dump.get('href') for dump in dumps_page_dom.cssselect('td.n a')]
    latest_download_links = [link for link in download_links if re.match('^enwiki-latest-pages-articles[0-9]+.xml-p[0-9]+p[0-9]+\.bz2$', link)]
    latest_download_links.sort(key=natural_keys)
    return latest_download_links


def get_filename(url):
    return url.split('/')[-1].split('#')[0].split('?')[0]


def download_file(url, dir, file):
        try:
            os.mkdir(dir)
            print 'Process dir [%s] created.' % dir
        except Exception:
            pass
        try:
            urllib.urlretrieve(url, '%s/%s' % (dir, file))
        except Exception as e:
            raise Exception("Error while downloading url [%s] in file [%s]" % (url, file), e)


def ingest_file(config, file):
    cmd = "MAVEN_OPTS='-Xms%s -Xmx%s -Dlog4j.configuration=file:conf/log4j.properties' %s exec:java -Dexec.mainClass=%s -Dexec.args='%s %s' 2>&1 1> %s.log" \
          % (MVN_HEAP_SIZE, MVN_HEAP_SIZE, MVN_BIN, LOADER, config, file, file)
    print 'Executing command:', cmd
    subprocess.check_call(cmd, shell=True)


def process_articles_dumps(config, n):
    latest_articles_links = get_latest_articles_list()
    print 'Retrieved latest articles links:', latest_articles_links
    for i in xrange(0, n):
        article_link = latest_articles_links[i]
        article_filename = get_filename(article_link)
        article_file = '%s/%s' % (WORK_DIR, article_filename)
        print 'Processing article %d - link: %s file: %s' % (i, article_link, article_file)
        print 'Start download ...'
        dt1 = time.time()
        download_file(LATEST_DUMPS + article_link, WORK_DIR, article_filename)
        dt2 = time.time()
        print 'Download complete in %d sec.' % (dt2 - dt1)
        print 'Start ingestion ...'
        it1 = time.time()
        ingest_file(config, article_file)
        it2 = time.time()
        print 'Ingestion complete in %d sec.' % (it2 - it1)
        os.remove(article_file)
        print 'File deleted.'


if __name__ == '__main__':
    """
    Example usage: bin/loader.py conf/default.properties 1
    """
    import sys
    if len(sys.argv) != 3:
        print 'Usage: $0 <config-file> <no-dumps>'
        sys.exit(1)
    process_articles_dumps(sys.argv[1], int(sys.argv[2]))
    sys.exit(0)