#!/usr/bin/env python
# -*- coding: utf-8 -*-

# JSONpedia - Convert any MediaWiki document to JSON.
#
# Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
#
# To the extent possible under law, the author has dedicated all copyright and related and
# neighboring rights to this software to the public domain worldwide.
# This software is distributed without any warranty.
#
# You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
# If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.


# == loader.py <config-file> <num-dumps> ==
#
# Example usage:
#   Run the Storage Loader with default configuration over the first four article dumps of Wikipedia
#     (
#       http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles1.xml-p000000010p000010000.bz2
#       http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles2.xml-p000010002p000024999.bz2
#       http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles3.xml-p000025001p000055000.bz2
#       http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles4.xml-p000055002p000104998.bz2
#     )
#
#   $ loader.py conf/default.properties 4
#
# This script automates the process of downloading the latest Wikipedia article dumps and run over them the
# specified LOADER passing to it the configuration specified as first argument.
# Specifically the script does:
# 1 - retrieve the list of download URLs for the latest Wikipedia dumps specified in LATEST_DUMPS page.
# For every dump URL:
#   2 - download dump i-th into the WORK_DIR
#   3 - run LOADER with specified configuration over the downloaded dump producing a separate log
#   4 - delete the dump
#

LATEST_DUMPS = 'http://dumps.wikimedia.org/enwiki/latest/'

WORK_DIR = 'work'

GRADLE_BIN = 'gradle'
LOADER = 'com.machinelinking.storage.DefaultJSONStorageLoader'


import urllib
import lxml
import os
import re
import time
import subprocess
import traceback

from lxml import html
from subprocess import CalledProcessError

def atoi(text):
    return int(text) if text.isdigit() else text


def natural_keys(text):
    return [ atoi(c) for c in re.split('(\d+)', text) ]


def get_latest_articles_list():
    dumps_page = urllib.urlopen(LATEST_DUMPS).read()
    dumps_page_dom = lxml.html.fromstring(dumps_page)
    download_links = [dump.get('href') for dump in dumps_page_dom.cssselect('td.n a')]
    latest_download_links = [
        link for link in download_links
        if re.match('^enwiki-latest-pages-articles[0-9]+.xml-p[0-9]+p[0-9]+\.bz2$', link)
    ]
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
    cmd = "%s runLoader -Pconfig=%s -Pdump=%s 2>&1 > %s.log" \
          % (GRADLE_BIN, config, file, file)
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
        try:
            ingest_file(config, article_file)
        except CalledProcessError as e:
            print 'Error while processing file:'
            traceback.format_exception_only(type(e), e)
        finally:
            it2 = time.time()
            print 'Ingestion completed in %d sec.' % (it2 - it1)
        os.remove(article_file)
        print 'File deleted.'


if __name__ == '__main__':
    """
    Example usage: bin/loader.py conf/default.properties 1
    """
    import sys
    if len(sys.argv) != 3:
        print 'Usage: $0 <config-file> <num-dumps>'
        sys.exit(1)
    process_articles_dumps(sys.argv[1], int(sys.argv[2]))
    sys.exit(0)