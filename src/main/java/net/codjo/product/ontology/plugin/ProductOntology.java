package net.codjo.product.ontology.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.ontology.common.api.AgentAction;
import net.codjo.ontology.common.api.Concept;
import net.codjo.ontology.common.api.OntologyException;
import net.codjo.product.ontology.message.GetProductAction;
import net.codjo.product.ontology.message.GetProductResponse;
import jade.util.leap.List;
/**
 *
 */
public interface ProductOntology {

    void fillMessage(AclMessage request, AgentAction action)
          throws OntologyException;


    AgentAction extractAction(AclMessage request) throws OntologyException;


    GetProductAction extractGetProductAction(AclMessage request)
          throws OntologyException;


    void fillMessage(AclMessage inform, AgentAction action, Concept response)
          throws OntologyException;


    GetProductResponse extractGetProductResponse(AclMessage request)
          throws OntologyException;


    void fillMessageList(AclMessage inform, AgentAction action, List response)
          throws OntologyException;


    List extractGetProductResponseList(AclMessage request)
          throws OntologyException;
}
