package net.codjo.product.ontology.plugin;
import static net.codjo.agent.AclMessage.Performative.REQUEST;
import net.codjo.agent.Agent;
import net.codjo.agent.Aid;
import net.codjo.agent.test.MessageBuilder;
import net.codjo.agent.test.SubStep;
import net.codjo.ontology.common.api.AgentAction;
import net.codjo.ontology.common.api.OntologyException;
import net.codjo.product.ontology.message.GetProductAction;
import net.codjo.product.ontology.message.Status;
/**
 *
 */
public class ProductOntologyTestUtil {
    private ProductOntologyTestUtil() {
    }


    public static MessageBuilder getProductActionMessage(String code, String date,
                                                         String feesYear,
                                                         String partSelector,
                                                         String user,
                                                         String password) {
        GetProductAction getProduct = new GetProductAction();
        getProduct.setUser(user);
        getProduct.setPassword(password);
        getProduct.setCode(code);
        getProduct.setDate(date);
        getProduct.setFeesYear(feesYear);
        getProduct.setPartSelector(partSelector);

        try {
            return createMessage(getProduct);
        }
        catch (OntologyException e) {
            throw new RuntimeException("Impossible d'encoder getProductAction", e);
        }
    }


    public static SubStep replyWith(Status status) {
        return new ReplyToGetProductActionWith(status);
    }


    private static MessageBuilder createMessage(AgentAction getProduct) throws OntologyException {
        MessageBuilder builder = MessageBuilder.message(REQUEST);

        Agent agent = new Agent() {

            @Override
            public Aid getAID() {
                return new Aid("initiator");
            }
        };

        ProductOntologyPlugin plugin = new ProductOntologyPlugin();
        plugin.getOperations().createProductOntology(agent).fillMessage(builder.get(), getProduct);
        return builder;
    }
}
