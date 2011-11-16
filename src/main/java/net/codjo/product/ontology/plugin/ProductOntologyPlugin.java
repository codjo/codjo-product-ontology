package net.codjo.product.ontology.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.agent.JadeWrapper;
import net.codjo.ontology.common.api.AgentAction;
import net.codjo.ontology.common.api.Concept;
import net.codjo.ontology.common.api.OntologyException;
import net.codjo.plugin.common.AbstractApplicationPlugin;
import net.codjo.product.ontology.message.GetProductAction;
import net.codjo.product.ontology.message.GetProductResponse;
import net.codjo.product.ontology.message.JadeProductOntology;
import net.codjo.product.ontology.message.Status;
import jade.util.leap.List;
/**
 *
 */
public class ProductOntologyPlugin extends AbstractApplicationPlugin {
    private ProductOntologyPluginOperations operations = new ProductOntologyPluginOperationsImpl();
    public static final String ONTOLOGY_NAME = JadeProductOntology.ONTOLOGY_NAME;
    public static final String DEFAULT_LANGUAGE = "fipa-sl";


    public ProductOntologyPluginOperations getOperations() {
        return operations;
    }


    private static class ProductOntologyPluginOperationsImpl implements ProductOntologyPluginOperations {
        private ProductXmlizer productXmlizer;


        public String toXml(Status status) {
            if (productXmlizer == null) {
                productXmlizer = new ProductXmlizer();
            }
            return productXmlizer.toXml(status);
        }


        public Status fromXml(String xml) {
            if (productXmlizer == null) {
                productXmlizer = new ProductXmlizer();
            }
            return productXmlizer.fromXml(xml);
        }


        public ProductOntology createProductOntology(Agent agent) {
            return new ProductOntologyImpl(agent);
        }
    }

    private static class ProductOntologyImpl implements ProductOntology {
        private final Agent agent;
        private final jade.content.lang.sl.SLCodec defaultCodec = new jade.content.lang.sl.SLCodec();


        ProductOntologyImpl(Agent agent) {
            this.agent = agent;
            getContentManager().registerOntology(JadeProductOntology.getInstance());
            getContentManager().registerLanguage(defaultCodec);
        }


        public void fillMessage(AclMessage request, AgentAction action) throws OntologyException {
            fill(request, toJadeAction(action));
        }


        public AgentAction extractAction(AclMessage request) throws OntologyException {
            try {
                return (AgentAction)((jade.content.onto.basic.Action)extract(request)).getAction();
            }
            catch (Exception failure) {
                throw new OntologyException(failure);
            }
        }


        public GetProductAction extractGetProductAction(AclMessage request) throws OntologyException {
            try {
                return (GetProductAction)((jade.content.onto.basic.Action)extract(request)).getAction();
            }
            catch (Exception failure) {
                throw new OntologyException(failure);
            }
        }


        public void fillMessage(AclMessage inform, AgentAction action, Concept response)
              throws OntologyException {
            fill(inform, new jade.content.onto.basic.Result(toJadeAction(action), response));
        }


        public GetProductResponse extractGetProductResponse(AclMessage request)
              throws OntologyException {
            try {
                return (GetProductResponse)((jade.content.onto.basic.Result)extract(request)).getValue();
            }
            catch (Exception failure) {
                throw new OntologyException(failure);
            }
        }


        public void fillMessageList(AclMessage inform, AgentAction action, List response)
              throws OntologyException {
            fill(inform, new jade.content.onto.basic.Result(toJadeAction(action), response));
        }


        public List extractGetProductResponseList(AclMessage request)
              throws OntologyException {
            try {
                return (List)((jade.content.onto.basic.Result)extract(request)).getValue();
            }
            catch (Exception failure) {
                throw new OntologyException(failure);
            }
        }


        private void fill(AclMessage request, jade.content.ContentElement content)
              throws OntologyException {
            request.setOntology(ONTOLOGY_NAME);
            request.setLanguage(defaultCodec.getName());

            try {
                getContentManager().fillContent(JadeWrapper.unwrapp(request), content);
            }
            catch (Exception failure) {
                throw new OntologyException(failure);
            }
        }


        private jade.content.ContentElement extract(AclMessage request) throws Exception {
            return getContentManager().extractContent(JadeWrapper.unwrapp(request));
        }


        private jade.content.onto.basic.Action toJadeAction(AgentAction action) {
            return new jade.content.onto.basic.Action(JadeWrapper.unwrapp(agent.getAID()), action);
        }


        private jade.content.ContentManager getContentManager() {
            return JadeWrapper.unwrapp(agent).getContentManager();
        }
    }
}
