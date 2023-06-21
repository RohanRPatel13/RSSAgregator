import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.xmltree.XMLTree;
import components.xmltree.XMLTree1;

/**
 * Program to convert an XML RSS (version 2.0) feed from a given URL into the
 * corresponding HTML output file.
 *
 * @author Rohan Patel
 *
 */
public final class RSSAggregator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private RSSAggregator() {
    }

    /**
     * Outputs the "opening" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * <html> <head> <title>the channel tag title as the page title</title>
     * </head> <body>
     * <h1>the page title inside a link to the <channel> link</h1>
     * <p>
     * the channel description
     * </p>
     * <table border="1">
     * <tr>
     * <th>Date</th>
     * <th>Source</th>
     * <th>News</th>
     * </tr>
     *
     * @param channel
     *            the channel element XMLTree
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the root of channel is a <channel> tag] and out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(XMLTree channel, SimpleWriter out) {
        assert channel != null : "Violation of: channel is not null";
        assert out != null : "Violation of: out is not null";
        assert channel.isTag() && channel.label().equals("channel") : ""
                + "Violation of: the label root of channel is a <channel> tag";
        assert out.isOpen() : "Violation of: out.is_open";
        String title = "";
        int titl = getChildElement(channel, "title");
        if (titl == -1) {
            title = "Empty Title";
        } else {
            if (channel.child(titl).numberOfChildren() > 0) {
                title = (channel.child(titl).child(0).toString());
            } else {
                title = "Empty Title";
            }
        }

        int description = getChildElement(channel, "description");
        String des = "";
        if (description == -1) {
            des = "No description";
        } else {
            if (channel.child(description).numberOfChildren() > 0) {
                des = channel.child(description).child(0).label();
            } else {
                des = "No description";
            }
        }
        int link = getChildElement(channel, "link");

        out.println("<html> <head> <title>" + title + "</title>");
        out.println("</head> <body>");
        out.println("<h1>");
        out.println("<a href=" + channel.child(link).child(0).label() + ">");
        out.println(title + "</a>");
        out.println("</h1>");
        out.println("<p>");
        out.println(des);
        out.println("</p>");
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Source</th>");
        out.println("<th>News</th>");
        out.println("</tr>");
    }

    /**
     * Outputs the "closing" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * </table>
     * </body> </html>
     *
     * @param out
     *            the output stream
     * @updates out.contents
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    private static void outputFooter(SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("</table>");
        out.println("</body> </html>");
    }

    /**
     * Finds the first occurrence of the given tag among the children of the
     * given {@code XMLTree} and return its index; returns -1 if not found.
     *
     * @param xml
     *            the {@code XMLTree} to search
     * @param tag
     *            the tag to look for
     * @return the index of the first child of type tag of the {@code XMLTree}
     *         or -1 if not found
     * @requires [the label of the root of xml is a tag]
     * @ensures <pre>
     * getChildElement =
     *  [the index of the first child of type tag of the {@code XMLTree} or
     *   -1 if not found]
     * </pre>
     */
    private static int getChildElement(XMLTree xml, String tag) {
        assert xml != null : "Violation of: xml is not null";
        assert tag != null : "Violation of: tag is not null";
        assert xml.isTag() : "Violation of: the label root of xml is a tag";
        int num = -1;
        boolean check = false;
        for (int i = 0; i < xml.numberOfChildren(); i++) {
            if (xml.child(i).label().equals(tag) && check == false) {
                num = i;
                check = true;
            }
        }
        return num;
    }

    /**
     * Processes one news item and outputs one table row. The row contains three
     * elements: the publication date, the source, and the title (or
     * description) of the item.
     *
     * @param item
     *            the news item
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the label of the root of item is an <item> tag] and
     *           out.is_open
     * @ensures <pre>
     * out.content = #out.content *
     *   [an HTML table row with publication date, source, and title of news item]
     * </pre>
     */
    private static void processItem(XMLTree item, SimpleWriter out) {
        assert item != null : "Violation of: item is not null";
        assert out != null : "Violation of: out is not null";
        assert item.isTag() && item.label().equals("item") : ""
                + "Violation of: the label root of item is an <item> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("<tr>");

        int dateIndex = getChildElement(item, "pubDate");
        String date = "";
        if (dateIndex == -1) {
            date = "No date available";
        } else {
            date = item.child(dateIndex).child(0).label();
        }
        out.println("<td>" + date + "</td>");

        int sourceIndex = getChildElement(item, "source");
        String source = "";
        if (sourceIndex == -1) {
            out.println("<td>No source available</td>");
        } else {
            if (item.child(sourceIndex).numberOfChildren() > 0) {
                out.println("<td>");
                out.println("<a href="
                        + item.child(sourceIndex).attributeValue("url") + ">");
                out.println(item.child(sourceIndex).child(0).label());
                out.println("</a>");
                out.println("</td>");
            } else {
                out.println("<td>No source available</td>");
            }

        }

        int titleIndex = getChildElement(item, "title");
        int descriptionIndex = getChildElement(item, "description");
        int linkIndex = getChildElement(item, "link");
        if (item.child(titleIndex).numberOfChildren() == 0) {
            //out.println("<td>" + "No title available" + "</td>");
            out.println("<td>");
            out.println(
                    "<a href=" + item.child(linkIndex).child(0).label() + ">");
            out.println(item.child(descriptionIndex).child(0).label() + "</a>");
            out.println("</td>");
        } else if (item.child(titleIndex).child(0).label().equals("")
                && item.child(descriptionIndex).child(0).label().equals("")
                && linkIndex == -1) {
            out.println("<td>" + "No title available" + "</td>");
        } else if (item.child(titleIndex).child(0).label().equals("")
                && item.child(descriptionIndex).child(0).label().equals("")) {
            out.println("<td>");
            out.println(
                    "<a href=" + item.child(linkIndex).child(0).label() + ">");
            out.println("No title available</a>");
            out.println("</td>");
        } else if (!(item.child(titleIndex).child(0).label().equals(""))
                && linkIndex == -1) {
            out.println(
                    "<td>" + item.child(titleIndex).child(0).label() + "</td>");
        } else if (!(item.child(titleIndex).child(0).label().equals(""))
                && linkIndex != -1) {
            out.println("<td>");
            out.println(
                    "<a href=" + item.child(linkIndex).child(0).label() + ">");
            out.println(item.child(titleIndex).child(0).label() + "</a>");
            out.println("</td>");
        } else if (!(item.child(descriptionIndex).child(0).label().equals(""))
                && linkIndex == -1) {
            out.println("<td>" + item.child(descriptionIndex).child(0).label()
                    + "</td>");
        } else if (!(item.child(descriptionIndex).child(0).label().equals(""))
                && linkIndex != -1) {
            out.println("<td>");
            out.println(
                    "<a href=" + item.child(linkIndex).child(0).label() + ">");
            out.println(item.child(descriptionIndex).child(0).label() + "</a>");
            out.println("</td>");
        }

        out.println("</tr>");

    }

    /**
     * Processes one XML RSS (version 2.0) feed from a given URL converting it
     * into the corresponding HTML output file.
     *
     * @param url
     *            the URL of the RSS feed
     * @param file
     *            the name of the HTML output file
     * @param out
     *            the output stream to report progress or errors
     * @updates out.content
     * @requires out.is_open
     * @ensures <pre>
     * [reads RSS feed from url, saves HTML document with table of news items
     *   to file, appends to out.content any needed messages]
     * </pre>
     */
    private static void processFeed(String url, String file, SimpleWriter out) {
        out = new SimpleWriter1L("src/" + file);

        XMLTree xml = new XMLTree1(url);
        XMLTree channel = xml.child(0);

        if (xml.label().equals("rss")
                && xml.attributeValue("version").equals("2.0")) {
            outputHeader(channel, out);
            for (int i = 0; i < channel.numberOfChildren(); i++) {
                if (channel.child(i).label().equals("item")) {
                    processItem(channel.child(i), out);
                }
            }
            outputFooter(out);
        } else {
            out.println("XMLTree must be RSS 2.0");
        }
        out.close();
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        out.println("Enter an XML file: ");
        String RSSFile = in.nextLine();
        out.println("Enter a name for the ouput file: ");
        String filename = in.nextLine() + ".html";
        SimpleWriter RSSout = new SimpleWriter1L("src/" + filename);

        XMLTree xml = new XMLTree1(RSSFile);

        RSSout.println("<html> <head> <title>" + xml.attributeValue("title")
                + "</title>");

        RSSout.println("</head> <body>");
        RSSout.println("<h1>");
        RSSout.println(xml.attributeValue("title"));
        RSSout.println("</h1>");

        for (int i = 0; i < xml.numberOfChildren(); i++) {
            if (xml.child(i).label().equals("feed")) {
                String url = xml.child(i).attributeValue("url");
                String name = xml.child(i).attributeValue("name");
                String file = xml.child(i).attributeValue("file");
                //SimpleWriter HTMLout = new SimpleWriter1L("src/" + file);
                processFeed(url, file, RSSout);
                RSSout.println("<ul style=\"list-style-type: disc;\">");
                RSSout.println("<li>");
                RSSout.println("<a href=" + file + ">");
                RSSout.println(name + "</a>");
                RSSout.println("</li>");
                RSSout.println("</ul>");
                //HTMLout.close();
            }
        }

        RSSout.println("</body> </html>");

        in.close();
        out.close();
        RSSout.close();
    }

}