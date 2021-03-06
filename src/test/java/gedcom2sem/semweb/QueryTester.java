// @formatter:off
/*
 * Copyright 2013, J. Pol
 *
 * This file is part of free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation.
 *
 * This package is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. A copy of the GNU General Public License is
 * available at <http://www.gnu.org/licenses/>.
 */
// @formatter:on
package gedcom2sem.semweb;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gedcom2sem.gedsem.Convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QueryTester
// name should not start or end with test so maven does not use it
// results from third parties (dbpedia/geonames) may vary
{
    private static final String MAIN = "src/main/resources/";
    private static final String TEST = "src/test/resources/";
    private static final String KENNEDY_TTL = "target/kennedy.ttl";

    private static final Collection<Object[]> constructorArgs = new ArrayList<Object[]>();
    private final Integer expectedNrOfLines;
    private final String queryFileName;
    private final String report;
    private final Boolean mashup;
    private final String endPointID;

    /** Same arguments as constructor. */
    private static void addTest(final Boolean mashup, final Integer expectedNrOfLines, final String endPointID, final String queryFileName)
    {
        constructorArgs.add(new Object[] {mashup, expectedNrOfLines, endPointID, queryFileName});
    }

    public QueryTester(final Boolean mashup, final Integer expectedNrOfLines, final String endPointID, final String queryFileName)
    {
        this.mashup = mashup;
        this.expectedNrOfLines = expectedNrOfLines;
        this.endPointID = endPointID;
        this.queryFileName = "src/main/resources/reports/" + queryFileName;
        this.report = "target/reports/" + queryFileName.replace(".arq", ".txt");
        new File(report).getParentFile().mkdirs();
    }

    @Parameters
    public static Collection<Object[]> getContructorParameters()
    {
        /* 00 */addTest(false, 13, null, "AgeDiffBetweenSpouses.arq");
        /* 01 */addTest(true, 49, null, "classmates.arq");
        /* 02 */addTest(false, 95, null, "CountEventsPerPlace.arq");
        /* 03 */addTest(false, 171, null, "CountGivnNames.arq");
        /* 04 */addTest(true, 98, "dbp", "dbpediaLanguages.arq");
        /* 05 */addTest(true, 201, "dbp", "dbpediaProperties.arq");
        /* 06 */addTest(true, 300, "dbp", "dbpediaRelatedEntities.arq");
        /* 07 */addTest(true, 29, "gn", "geonamesProperties.arq");
        /* 08 */addTest(true, 7, "gn", "geonamesRelatedEntities.arq");
        /* 09 */addTest(true, 98, null, "mashup.arq");
        /* 10 */addTest(true, 69, null, "places-by-birth.arq");
        /* 11 */addTest(true, 69, null, "places-by-marriage.arq");
        /* 12 */addTest(false, 4, null, "SOSA-EventDocuments.arq");
        /* 13 */addTest(false, 34, null, "SOSA-InbredStatistics.arq");
        /* 14 */addTest(false, 11, null, "SOSA-MultiMedia.arq");
        /* 15 */addTest(false, 34, null, "SOSA-Roots.arq");
        return constructorArgs;
    }

    @BeforeClass
    public static void gedcomToTTL() throws Exception
    {
        Convert.main(//
                MAIN + "prefixes.ttl", //
                MAIN + "rules/basic.rules", //
                MAIN + "rules/additional.rules", //
                TEST + "geoMashup.rules", //
                TEST + "kennedy.ged", //
                KENNEDY_TTL);
    }

    @Before
    public void beNice()
    {
        // too frequent access to a SPARQL end point causes "service not available" or
        // "bandwidth exceeded"
        if (endPointID != null)
            Nice.sleep(endPointID);
    }

    @Test
    public void run() throws Exception
    {
        if (mashup)
            Select.main("src/test/resources/geoNamesCache.ttl", KENNEDY_TTL, queryFileName, report);
        else
            Select.main(KENNEDY_TTL, queryFileName, report);
    }

    @After
    public void countLines() throws FileNotFoundException, IOException
    {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(report)));
        int nrOfLines = 0;
        while (bufferedReader.readLine() != null)
            nrOfLines++;
        bufferedReader.close();
        assertThat(queryFileName, nrOfLines, is(expectedNrOfLines));
    }
}
