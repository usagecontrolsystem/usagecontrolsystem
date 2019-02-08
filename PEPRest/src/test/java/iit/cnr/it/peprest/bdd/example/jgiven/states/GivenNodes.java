package iit.cnr.it.peprest.bdd.example.jgiven.states;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenNodes extends Stage<GivenNodes> {

    @ProvidedScenarioState
    String originNode;
    @ProvidedScenarioState
    String destinationNode;

    public GivenNodes an_origin_node(String node) {
        originNode = node;
        return self();
    }

    public GivenNodes destination_node(String node) {
        destinationNode = node;
        return self();
    }
}