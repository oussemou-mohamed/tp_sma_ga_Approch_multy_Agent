package ma.enset.ga.sma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.enset.ga.sequencial.GAUtils;

import java.util.*;

public class MainAgentGA extends Agent {
    ////////////////////////////////////////////////////
    List<AgentFitness> agentsFitness=new ArrayList<>();
    ///////////////////////////////////////////////////
    @Override
    protected void setup() {
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setType("ga");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] agentsDescriptions = DFService.search(this, dfAgentDescription);
            System.out.println(agentsDescriptions.length);
            for (DFAgentDescription dfAD:agentsDescriptions) {
                agentsFitness.add(new AgentFitness(dfAD.getName(),0));
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        calculateFintness();

        addBehaviour(new Behaviour() {
            int cpt=0;
            @Override
            public void action() {
                ACLMessage receivedMSG = receive();
                if (receivedMSG!=null){
                    cpt++;
                    System.out.println(cpt);
                    int fintess=Integer.parseInt(receivedMSG.getContent());
                    AID sender=receivedMSG.getSender();
                    System.out.println(sender.getName()+" "+fintess);
                    setAgentFintess(sender,fintess);
                    if(cpt==GAUtils.POPULATION_SIZE){
                        Collections.sort(agentsFitness,Collections.reverseOrder());
                        showPopulation();
                    }
                }else {
                    block();
                }
            }

            @Override
            public boolean done() {
                return cpt==GAUtils.MAX_IT || agentsFitness.get(0).getFitness() ==GAUtils.MAX_FITNES;
            }

        });

    }
    ////////////////////////////////////////////////////
private void calculateFintness(){
    ACLMessage message=new ACLMessage(ACLMessage.REQUEST);

    for (AgentFitness agf:agentsFitness) {
        message.addReceiver(agf.getAid());
    }
    message.setContent("fitness");
    send(message);
}
    ///////////////////////////////////////////////////
private void setAgentFintess(AID aid,int fitness){
        for (int i=0;i<GAUtils.POPULATION_SIZE;i++){
            if(agentsFitness.get(i).getAid().equals(aid)){
                agentsFitness.get(i).setFitness(fitness);
                System.out.println(fitness+"=:="+agentsFitness.get(i).getFitness());
                break;
            }
        }
}

private void showPopulation(){
    for (AgentFitness agentFitness:agentsFitness) {
        System.out.println(agentFitness.getAid().getName()+" "+agentFitness.getFitness());
    }
}
}
