package net.codjo.product.ontology.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.agent.Aid;
import net.codjo.agent.JadeWrapper;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.agent.test.MessageBuilder;
import net.codjo.agent.test.Story.ConnectionType;
import net.codjo.ontology.common.api.OntologyException;
import net.codjo.product.ontology.message.GetProductAction;
import net.codjo.product.ontology.message.GetProductException;
import net.codjo.product.ontology.message.GetProductResponse;
import net.codjo.product.ontology.message.Status;
import static net.codjo.product.ontology.plugin.ProductOntologyPlugin.ONTOLOGY_NAME;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import java.io.IOException;
import junit.framework.TestCase;
/**
 *
 */
public class ProductOntologyPluginTest extends TestCase {
    private ProductOntologyPlugin plugin = new ProductOntologyPlugin();
    private AgentContainerFixture fixture = new AgentContainerFixture();
    private AclMessage aclMessage = new AclMessage(AclMessage.Performative.REQUEST);
    private static final String STATUS_XML =
          "<status xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='product.xsd'>"
          + "  <label>mon nom</label>"
          + "  <creationDateSt>0</creationDateSt>"
          + "  <accountCloseLastYearSt>0</accountCloseLastYearSt>"
          + "  <accountCloseFirstYearSt>0</accountCloseFirstYearSt>"
          + "  <prorogDurationOpcvmSt>0</prorogDurationOpcvmSt>"
          + "  <editionDateSt>0</editionDateSt>"
          + "  <durationExistOpcvmSt>0</durationExistOpcvmSt>"
          + "  <isProductTypeValuated>false</isProductTypeValuated>"
          + "  <accountCloseDateSt>0</accountCloseDateSt>"
          + "  <juridicalStructureSt>0</juridicalStructureSt>"
          + "  <productionCenterSt>0</productionCenterSt>"
          + "  <closeDateSt>0</closeDateSt>"
          + "  <agreementDateSt>0</agreementDateSt>"
          + "  <europeanComplianceSt>0</europeanComplianceSt>"
          + "</status>";


    @Override
    protected void setUp() throws Exception {
        fixture.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
    }


    public void test_getOperations() throws Exception {
        assertNotNull(plugin.getOperations());
    }


    public void test_getOperations_toXml() throws Exception {
        Status status = new Status();
        status.setLabel("mon nom");

        XmlUtil.assertEquivalent(STATUS_XML, plugin.getOperations().toXml(status));
    }


    public void test_getOperations_fromXml() throws Exception {
        Status status = plugin.getOperations().fromXml(STATUS_XML);
        assertEquals("mon nom", status.getLabel());
    }


    public void test_createAgentOperations() throws Exception {
        Agent agent = new Agent();
        assertNotNull(plugin.getOperations().createProductOntology(agent));

        ContentManager contentManager = JadeWrapper.unwrapp(agent).getContentManager();

        assertNotNull(contentManager.lookupOntology(ONTOLOGY_NAME));
        assertNotNull(contentManager.lookupLanguage(new SLCodec().getName()));
    }


    public void test_createAgentOperations_twice() throws Exception {
        Agent agent = new Agent();
        assertNotNull(plugin.getOperations().createProductOntology(agent));
        assertNotNull(plugin.getOperations().createProductOntology(agent));
    }


    public void test_operations_fillMessage() throws Exception {
        Agent agent = new Agent();
        fixture.startContainer(ConnectionType.NO_CONNECTION);
        fixture.startNewAgent("roberto", agent);

        ProductOntology ontology = plugin.getOperations().createProductOntology(agent);

        ontology.fillMessage(aclMessage, createAction("toto", "pipo", "code", "2008-01-01", "2008", "partB"));

        assertEquals(ONTOLOGY_NAME, aclMessage.getOntology());
        assertEquals(ProductOntologyPlugin.DEFAULT_LANGUAGE, aclMessage.getLanguage());
        assertEquals(createGetProductActionString(agent.getAID(),
                                                  "toto", "pipo", "code",
                                                  "2008-01-01",
                                                  "2008",
                                                  "partB"),
                     aclMessage.getContent());
    }


    public void test_operations_fillMessage_error() throws Exception {
        ProductOntology ontology = plugin.getOperations().createProductOntology(new Agent());

        try {
            ontology.fillMessage(aclMessage, createAction("toto", "pipo",
                                                          "code", "2008-01-01", "2008", "partB"));
            fail();
        }
        catch (OntologyException ex) {
            assertNotNull(ex.getCause());
        }
    }


    public void test_operations_extractGetProductAction() throws Exception {
        ProductOntology ontology = plugin.getOperations().createProductOntology(new Agent());

        aclMessage.setLanguage(ProductOntologyPlugin.DEFAULT_LANGUAGE);
        aclMessage.setOntology(ONTOLOGY_NAME);
        aclMessage.setContent(createGetProductActionString(new Aid("leon@nikita.fr", false),
                                                           "toto",
                                                           "pipo",
                                                           "code",
                                                           "2008-01-01",
                                                           "2008",
                                                           "partB"));

        GetProductAction productAction = ontology.extractGetProductAction(aclMessage);

        assertNotNull(productAction);
        assertEquals("code", productAction.getCode());
        assertEquals("2008-01-01", productAction.getDate());
        assertEquals("2008", productAction.getFeesYear());
        assertEquals("partB", productAction.getPartSelector());
    }


    public void test_operations_extractGetProductAction_error() throws Exception {
        ProductOntology ontology = plugin.getOperations().createProductOntology(new Agent());

        aclMessage.setLanguage(ProductOntologyPlugin.DEFAULT_LANGUAGE);
        aclMessage.setOntology(ONTOLOGY_NAME);
        aclMessage.setContent("bad content");

        try {
            ontology.extractGetProductAction(aclMessage);
            fail();
        }
        catch (OntologyException ex) {
            assertNotNull(ex.getCause());
        }
    }


    public void test_operations_fillMessageResponse() throws Exception {
        Agent agent = new Agent();
        fixture.startContainer(ConnectionType.NO_CONNECTION);
        fixture.startNewAgent("roberto", agent);

        ProductOntology ontology = plugin.getOperations().createProductOntology(agent);

        ontology.fillMessage(aclMessage,
                             createAction("toto", "pipo", "code", "2008-01-01", "2008", "partB"),
                             new GetProductResponse());

        assertEquals(ONTOLOGY_NAME, aclMessage.getOntology());
        assertEquals(ProductOntologyPlugin.DEFAULT_LANGUAGE, aclMessage.getLanguage());
        assertEquals(createResultString(agent.getAID(),
                                        "toto", "pipo", "code", "2008-01-01", "2008", "partB"),
                     aclMessage.getContent());
    }


    public void test_operations_fillMessageResponse_error() throws Exception {
        Agent agent = new Agent();
        fixture.startContainer(ConnectionType.NO_CONNECTION);
        fixture.startNewAgent("roberto", agent);

        ProductOntology ontology = plugin.getOperations().createProductOntology(agent);

        GetProductResponse response = new GetProductResponse();
        GetProductException exception = new GetProductException();
        exception.setMessage("Exception thrown");
        response.setException(exception);

        ontology.fillMessage(aclMessage,
                             createAction("toto", "pipo", "code", "2008-01-01", "2008", "partB"),
                             response);

        assertEquals(ONTOLOGY_NAME, aclMessage.getOntology());
        assertEquals(ProductOntologyPlugin.DEFAULT_LANGUAGE, aclMessage.getLanguage());
        assertEquals(createResultStringWithException(agent.getAID(),
                                                     "toto", "pipo", "code",
                                                     "2008-01-01",
                                                     "2008",
                                                     "partB",
                                                     "Exception thrown"),
                     aclMessage.getContent());
    }


    public void test_operations_extractGetProductResponse() throws Exception {
        ProductOntology ontology = plugin.getOperations().createProductOntology(new Agent());

        aclMessage.setLanguage(ProductOntologyPlugin.DEFAULT_LANGUAGE);
        aclMessage.setOntology(ONTOLOGY_NAME);
        aclMessage.setContent(createResultString(new Aid("leon@nikita.fr", false),
                                                 "toto", "pipo", "code", "2008-01-01", "2008", "partB"
        ));

        GetProductResponse response = ontology.extractGetProductResponse(aclMessage);

        assertNotNull(response);
    }


    public void test_operations_extractGetProductResponse_error() throws Exception {
        ProductOntology ontology = plugin.getOperations().createProductOntology(new Agent());

        aclMessage.setLanguage(ProductOntologyPlugin.DEFAULT_LANGUAGE);
        aclMessage.setOntology(ONTOLOGY_NAME);
        aclMessage.setContent("bad");

        try {
            ontology.extractGetProductResponse(aclMessage);
            fail();
        }
        catch (OntologyException ex) {
            assertNotNull(ex.getCause());
        }
    }


    private GetProductAction createAction(String user,
                                          String password,
                                          String code,
                                          String date,
                                          String feesYear,
                                          String partSelector) {
        GetProductAction productAction = new GetProductAction();
        productAction.setUser(user);
        productAction.setPassword(password);
        productAction.setCode(code);
        productAction.setDate(date);
        productAction.setFeesYear(feesYear);
        productAction.setPartSelector(partSelector);
        return productAction;
    }


    private String createGetProductActionString(Aid aid,
                                                String user,
                                                String password,
                                                String code,
                                                String date,
                                                String feesYear,
                                                String partSelector) {
        return "(" + toActionString(aid, user, password, code, date, feesYear, partSelector) + ")";
    }


    private String toActionString(Aid aid,
                                  String user,
                                  String password,
                                  String code,
                                  String date,
                                  String feesYear,
                                  String partSelector) {
        return String.format("(action "
                             + "%s "
                             + "(GetProductAction :user %s "
                             + ":password %s :code %s "
                             + ":date \"%s\" "
                             + ":feesYear \"%s\" "
                             + ":partSelector %s))",
                             aid.toString()
                                   .replaceAll("\\( ", "(")
                                   .replaceAll(" \\)", ")")
                                   .replaceAll("  ", " "),
                             user, password, code, date, feesYear, partSelector);
    }


    private String createResultString(Aid aid,
                                      String user, String password, String code,
                                      String date,
                                      String feesYear,
                                      String partSelector) {
        return "((result "
               + toActionString(aid, user, password, code, date, feesYear, partSelector)
               + " (GetProductResponse)))";
    }


    private String createResultStringWithException(Aid aid,
                                                   String user, String password, String code,
                                                   String date,
                                                   String feesYear,
                                                   String partSelector,
                                                   String message) {
        return "((result "
               + toActionString(aid, user, password, code, date, feesYear, partSelector)
               + " (GetProductResponse :exception (GetProductException :message \""
               + message
               + "\"))))";
    }


    public void test_operations_fillMessageResponse_status() throws Exception {
        Agent agent = new Agent();
        fixture.startContainer(ConnectionType.NO_CONNECTION);
        fixture.startNewAgent("roberto", agent);

        ProductOntology ontology = plugin.getOperations().createProductOntology(agent);

        GetProductResponse getProductResponse = new GetProductResponse();
        Status status = plugin.getOperations().fromXml(loadFile("ProductOntologyPluginTest.xml"));
        getProductResponse.setStatus(status);

        ontology.fillMessage(aclMessage,
                             createAction("toto", "pipo", "code", "2008-01-01", "2008", "partB"),
                             getProductResponse);

        assertEquals(
              "((result " + toActionString(agent.getAID(),
                                           "toto", "pipo", "code", "2008-01-01", "2008", "partB")
              + loadFile("ProductOntologyPluginTest.txt"),
              aclMessage.getContent());
    }


    public void test_operations_extractGetProductResponse_status() throws Exception {
        ProductOntology ontology = plugin.getOperations().createProductOntology(new Agent());

        aclMessage.setLanguage(ProductOntologyPlugin.DEFAULT_LANGUAGE);
        aclMessage.setOntology(ONTOLOGY_NAME);
        aclMessage.setContent("((result " + toActionString(new Aid("roberto@A7WQ083:35700/JADE", false),
                                                           "toto", "pipo", "code", "2008-01-01",
                                                           "2008", "partB")
                              + loadFile("ProductOntologyPluginTest.txt"));

        GetProductResponse response = ontology.extractGetProductResponse(aclMessage);

        XmlUtil.assertEquivalent(loadFile("ProductOntologyPluginTest.xml"),
                                 plugin.getOperations().toXml(response.getStatus()));
    }


    public void test_testUtil() throws Exception {
        fixture.startContainer(ConnectionType.NO_CONNECTION);
        MessageBuilder builder = ProductOntologyTestUtil.getProductActionMessage("code", "2008-01-01",
                                                                                 "2008", "partB",
                                                                                 "toto", "pipo");
        AclMessage message = builder.get();

        ProductOntology ontology = plugin.getOperations().createProductOntology(new Agent());

        assertEquals("code", ontology.extractGetProductAction(message).getCode());
    }


    private String loadFile(String name) throws IOException {
        return FileUtil.loadContent(getClass().getResource(name));
    }
}
