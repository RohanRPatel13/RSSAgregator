# RSSAgregator

## Objectives
1. Familiarity with using XMLTrees to process XML
2. Familiarity with using while loops and static methods
3. Exposure to RSS technology

## The Problem
For this project your task is to update the RSS Reader program you wrote for the previous project so that it reads multiple RSS feeds and generates the same nicely formatted HTML page of links for each feed, plus an HTML index page with links to the individual feed pages.

Your new program should ask the user for the name of an XML file containing a list of URLs for RSS v2.0 feeds (see below for the format of this file) and for the name of an output file in which to generate an HTML page with links to the pages for the individual RSS feeds (see below for an example). It should then read the input XML file into an XMLTree object and then process the list of RSS feeds from the XMLTree. For each RSS feed, the program should generate an HTML page with a table of links to all the news items in the feed (just like in the previous project). The program should also generate an HTML page with an index of links to the individual feed pages.

### Format of the Input XML Document
The top-level tag, feeds, has a required attribute, title, whose value is the title to be used in the index page; nested inside the top-level tag are 1 or more feed tags with the following required attributes: url, the URL of the RSS feed, name, the name to use for the link to the feed in the index page, and file, the name of the HTML file in which to generate the feed's table of links to news items (with the same format as the output in the previous project).
