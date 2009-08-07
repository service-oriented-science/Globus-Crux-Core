package org.globus.crux.wsrf.query;

import org.globus.crux.wsrf.properties.ResourcePropertySet;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryExpressionType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourcePropertiesResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties_Type;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.UnknownQueryExpressionDialectFault;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author turtlebender
 */
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
        qrp.setRps(rps);
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
        when(queryEngine.executeQuery(expr.getContent().get(0), rps)).thenReturn(queryResults);
    }

    @Test
    public void testQueryResourcePropertiesSetEngine() throws Exception {
        List<QueryEngine<Object, Object>> engines = new ArrayList<QueryEngine<Object, Object>>();
        engines.add(this.queryEngine);
        qrp.setEngines(engines);
        runTest();
    }

    private void runTest() throws Exception{
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
        verify(queryEngine, times(1)).executeQuery(expr.getContent().get(0), rps);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), value);
    }

    @Test
    public void testQueryResourcePropertiesAddEngine() throws Exception {
        qrp.addQueryEngine(queryEngine);
        runTest();
    }
}
