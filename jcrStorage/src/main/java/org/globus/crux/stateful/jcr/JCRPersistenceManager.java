package org.globus.crux.stateful.jcr;

import org.apache.jackrabbit.core.TransientRepository;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;


/**
 * @author turtlebender
 */
public class JCRPersistenceManager {

    private Repository repository;
    private Credentials credentials;

    private Session getSession() throws RepositoryException {
        return repository.login(credentials);
    }

    public void getResource(QName keyQName, Object keyValue) throws RepositoryException {
        Session session = getSession();
        String prefix = session.getNamespacePrefix(keyQName.getNamespaceURI());
        NamespaceRegistry nr = session.getWorkspace().getNamespaceRegistry();
        if (prefix == null) {
            nr.registerNamespace("ns0", keyQName.getNamespaceURI());
        }
        QueryManager qm = session.getWorkspace().getQueryManager();
        int key = HashcodeUtil.hash(keyValue);
        String queryQPath = "//" + prefix + ":" + keyQName.getLocalPart() +
                "[@value='" + key + "']";
        Query q = qm.createQuery(queryQPath, Query.XPATH);
        QueryResult result = q.execute();
        System.out.println(result.getRows().getSize());
    }

    public void storeResource(Object toStore) throws RepositoryException{
        Session session = getSession();
        

    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public static void main(String[] args) throws Exception {
        Repository repository = new TransientRepository();
        Credentials creds = new SimpleCredentials("username", "password".toCharArray());
        JCRPersistenceManager manager = new JCRPersistenceManager();
        manager.setRepository(repository);
        manager.setCredentials(creds);
        QName name = new QName("http://www.counter.com", "CounterKey");
        manager.getResource(name, 15);
    }
}
