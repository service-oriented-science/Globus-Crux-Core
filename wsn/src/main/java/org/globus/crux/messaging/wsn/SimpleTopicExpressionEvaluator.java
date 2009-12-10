package org.globus.crux.messaging.wsn;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class SimpleTopicExpressionEvaluator {
//    public static final String SIMPLE_TOPIC_DIALECT = "http://docs.oasis-open.org/wsn/2004/06/TopicExpression/Simple";
//    private Logger logger = LoggerFactory.getLogger(SimpleTopicExpressionEvaluator.class.getName());
//    ResourceBundle resourceBundle = ResourceBundle.getBundle(SimpleTopicExpressionEvaluator.class.getName());
//    //    private static I18n i18n = I18n.getI18n(Resources.class.getName());
//    private static String[] dialects = {SIMPLE_TOPIC_DIALECT};
//
//    public Collection resolve(TopicExpressionType expression,
//                              TopicManager topicList)
//            throws UnsupportedTopicExpressionDialectException,
//            TopicExpressionResolutionException,
//            InvalidTopicExpressionException,
//            TopicExpressionException {
//        QName topicName = (QName) expression.getContent().get(0);
//
//        logger.debug("Looking for topic with namespace: " +
//                topicName.getNamespaceURI() + " and local part " +
//                topicName.getLocalPart());
//
//        Collection result = new ArrayList();
//        List topicPath = new LinkedList();
//        topicPath.add(topicName);
//
//        WSNTopic topic = topicList.getTopic(topicPath);
//
//        if (topic != null) {
//            result.add(topic);
//        }
//
//        return result;
//    }
//
//    public String[] getDialects() {
//        return dialects;
//    }
//
//    public List<QName> getConcreteTopicPath(TopicExpressionType expression)
//            throws UnsupportedTopicExpressionDialectException,
//            InvalidTopicExpressionException,
//            TopicExpressionException {
//        List<QName> result = new LinkedList<QName>();
//        result.add((QName)expression.getContent().get(0));
//        return result;
//    }
//
//    public TopicExpressionType toTopicExpression(List<QName> topicPath)
//            throws InvalidTopicExpressionException,
//            TopicExpressionException {
//        if (topicPath == null || topicPath.size() != 1) {
//            throw new InvalidTopicExpressionException(
//                    resourceBundle.getString("invalidSimpleTopicPath")); /*NON-NLS*/
//        }
//
//        TopicExpressionType result= new TopicExpressionType();
//        result.setDialect(SIMPLE_TOPIC_DIALECT);
//        result.getContent().add(topicPath.get(0));
//        return result;
//    }
}
