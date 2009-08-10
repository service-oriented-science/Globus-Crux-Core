package org.globus.crux.wsrf.query;

import org.globus.crux.wsrf.properties.ResourcePropertySet;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.oasis.wsrf.properties.QueryExpressionType;
import org.oasis.wsrf.properties.InvalidQueryExpressionFault;
import org.oasis.wsrf.properties.QueryResourceProperties_Type;
import org.oasis.wsrf.properties.QueryResourcePropertiesResponse;
import org.oasis.wsrf.properties.UnknownQueryExpressionDialectFault;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author turtlebender
 */
@Test(groups = {"wsrf", "query"})
public class DefaultQueryResourcePropertiesTest {
    @Mock
    QueryEngine queryEngine;
    @Mock
    ResourcePropertySet rps;

    private QueryExpressionType expr;
    private Element value;
    private DefaultQueryResourceProperties qrp;


    @BeforeMethod
    public void setup() throws Exception {
        initMocks(this);
        qrp = new DefaultQueryResourceProperties();
        expr = new QueryExpressionType();
        expr.setDialect(DefaultXPathQueryEngine.XPATH_1_DIALECT);
        expr.getContent().add(new XPathQuery());
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        value = doc.createElementNS("http://counter.com", "Value");
        value.setTextContent("20");
        List<Object> queryResults = new ArrayList<Object>();
        queryResults.add(value);
        when(queryEngine.canProcessDialect(DefaultXPathQueryEngine.XPATH_1_DIALECT)).thenReturn(true);
        when(queryEngine.canProcessDialect("Fake Dialect")).thenReturn(false);
        when(queryEngine.getQueryType()).thenReturn(XPathQuery.class);
        when(queryEngine.executeQuery(expr.getContent().get(0))).thenReturn(queryResults);
    }

    @Test(expectedExceptions = InvalidQueryExpressionFault.class)
    public void testNullQuery() throws Exception {
        QueryResourceProperties_Type query = new QueryResourceProperties_Type();
        expr.getContent().set(0, null);
        query.setQueryExpression(expr);
        qrp.addQueryEngine(this.queryEngine);
        qrp.queryResourceProperties(query);
    }

    @Test
    public void testQueryResourcePropertiesSetEngine() throws Exception {
        List<QueryEngine<Object, Object>> engines = new ArrayList<QueryEngine<Object, Object>>();
        engines.add(this.queryEngine);
        qrp.setEngines(engines);
        runTest();
    }

    private void runTest() throws Exception {
        QueryResourceProperties_Type query = new QueryResourceProperties_Type();
        query.setQueryExpression(expr);
        QueryResourcePropertiesResponse response = qrp.queryResourceProperties(query);
        List<Object> results = response.getContent();
        QueryExpressionType badExpr = new QueryExpressionType();
        badExpr.setDialect("Fake Dialect");
        query.setQueryExpression(badExpr);
        try {
            qrp.queryResourceProperties(query);
            fail();
        } catch (UnknownQueryExpressionDialectFault e) {
            //better happen
        }
        verify(queryEngine, times(2)).canProcessDialect(any(String.class));
        verify(queryEngine, times(1)).executeQuery(expr.getContent().get(0));
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), value);
    }

    @Test
    public void testQueryResourcePropertiesAddEngine() throws Exception {
        qrp.addQueryEngine(queryEngine);
        runTest();
    }
}
