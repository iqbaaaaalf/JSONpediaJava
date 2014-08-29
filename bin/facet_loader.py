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


# == facet_loader.py -s <source-URI> -d <destination-URI> -l <limit-num> -c <config-file> ==
#
# Example usage:
#   $ bin/facet_loader.py -s localhost:9300:jsonpedia_test_load:en -d localhost:9300:jsonpedia_test_facet:en -l 100 -c conf/faceting.properties

import sys
import subprocess

GRADLE_BIN = 'gradle'
FACETLOADER_GRADLE_CALL = 'runFacetLoader'

if __name__ == '__main__':
    cmd = "%s %s -Pargs_line='%s'" \
        % (GRADLE_BIN, FACETLOADER_GRADLE_CALL, ' '.join(sys.argv[1:]))
    print 'Executing command:', cmd
    subprocess.check_call(cmd, shell=True)
