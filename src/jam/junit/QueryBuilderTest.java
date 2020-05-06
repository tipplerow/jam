
package jam.junit;

import jam.sql.QueryBuilder;

import org.junit.*;
import static org.junit.Assert.*;

public class QueryBuilderTest {
    @Test public void testBuilder() {
        QueryBuilder builder = QueryBuilder.create();

        builder.select("substances.short_name");
        builder.select("articles.pmid");
        builder.select("articles.pub_date");
        builder.select("titles.title");

        builder.from("substances");

        builder.innerJoin("article_substances",
                          "article_substances.substance_key",
                          "substances.java_name");

        builder.innerJoin("articles",
                          "articles.pmid", 
                          "article_substances.pmid");

        builder.innerJoin("titles",
                          "titles.pmid",
                          "articles.pmid");

        builder.where("articles.pmid > 123456");
        builder.where("titles.title like '%cancer%'");

        builder.orderBy("substances.short_name");
        builder.orderByDesc("articles.pub_date");

        assertEquals("SELECT substances.short_name, articles.pmid, articles.pub_date, titles.title FROM substances INNER JOIN article_substances ON article_substances.substance_key = substances.java_name INNER JOIN articles ON articles.pmid = article_substances.pmid INNER JOIN titles ON titles.pmid = articles.pmid WHERE articles.pmid > 123456 AND titles.title like '%cancer%' ORDER BY substances.short_name, articles.pub_date DESC", builder.toString());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.QueryBuilderTest");
    }
}
