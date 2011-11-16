package net.codjo.product.ontology.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.agent.test.SubStep;
import net.codjo.ontology.common.api.AgentAction;
import net.codjo.product.ontology.message.GetProductResponse;
import net.codjo.product.ontology.message.Status;
/**
 *
 */
public class ReplyToGetProductActionWith implements SubStep {
    private Status status;


    protected ReplyToGetProductActionWith(Status status) {
        this.status = status;
    }


    public void run(Agent agent, AclMessage message) throws Exception {
        AclMessage inform = message.createReply(AclMessage.Performative.INFORM);

        ProductOntologyPlugin plugin = new ProductOntologyPlugin();

        ProductOntology productOntology = plugin.getOperations().createProductOntology(agent);

        AgentAction agentAction = productOntology.extractAction(message);

        GetProductResponse getProductResponse = new GetProductResponse();
        getProductResponse.setStatus(status);

        productOntology.fillMessage(inform, agentAction, getProductResponse);

        agent.send(inform);
    }


    public Status getStatus() {
        return status;
    }
}
