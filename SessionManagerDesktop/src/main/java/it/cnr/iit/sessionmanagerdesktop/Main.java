/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.iit.sessionmanagerdesktop;

//import it.cnr.iit.sessionmanagerdalutilities.Session;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author fabio
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SessionManagerDesktop sm = new SessionManagerDesktop("jdbc:mysql://146.48.125.102:3306/session_manager?user=ucon&password=ucon.2016");
        //SessionManager sm = new SessionManager("jdbc:postgresql://146.48.125.102:5432/session_manager?user=ucon&password=xxx");
        sm.start();

        List<String> attributes = new LinkedList<>();
        attributes.add("aaa");
        attributes.add("bbb");

        //ESEMPIO 1: CREA TRE SESSIONI, AGGIORNA LO STATO DI UNA, TESTA I METODI GETSESSIONSFORSTATUS E GETSESSIONFOR ID E LE ELIMINA
        /*sm.createEntryForSubject("1", "<policyOnGoing1>", "<policyPost1>", "<originalRequest1>", attributes, "status1", "pepURI1", "fabio");
        sm.createEntryForObject("2", "<policyOnGoing1>", "<policyPost1>", "<originalRequest1>", attributes, "status1", "pepURI1", "room");
        sm.createEntryForObject("3", "<policyOnGoing1>", "<policyPost1>", "<originalRequest1>", attributes, "status1", "pepURI1", "desk");
        sm.updateEntry("1", "status2");
 
        List<Session> result = sm.getSessionsForStatus("status1");
        for (Session s : result) {
            System.out.println(s.toString());
        }*/
        
        //System.out.println(sm.getSessionForId("1").toString());
 
        //sm.deleteEntry("1");
        //sm.deleteEntry("2");
        //sm.deleteEntry("3");

        //ESEMPIO 2: CREA 3 SESSIONI CON ON_GOING_ATTRIBUTES MULTI-VALORE E RICHIAMA LA GETSESSIONSFOROBJECTSATTRIBUTES INVIANDOLE 1 ATTRIBUTO
        /*sm.createEntryForSubject("1", "<policyOnGoing1>", "<policyPost1>", "<originalRequest1>", attributes, "status1", "pepURI1", "fabio");
        sm.createEntryForObject("2", "<policyOnGoing1>", "<policyPost1>", "<originalRequest1>", attributes, "status1", "pepURI1", "room");
        sm.createEntryForObject("3", "<policyOnGoing1>", "<policyPost1>", "<originalRequest1>", attributes, "status1", "pepURI1", "desk");
        sm.createEntryForObject("4", "<policyOnGoing1>", "<policyPost1>", "<originalRequest1>", attributes, "status1", "pepURI1", "desk");
        List<Session> result = sm.getSessionsForObjectAttributes("aaa");
        for (Session s : result) {
            System.out.println(s.toString());
        }
        List<Session> result1 = sm.getSessionsForObjectAttributes("desk","aaa");
        for (Session s : result1) {
            System.out.println(s.toString());
        }*/
        
        //ESEMPIO 3: CREA 1 SESSIONE CON ON_GOING_ATTRIBUTES AVENTI SIA SUBJECT CHE OBJECT E POI LI AGGIORNA
        /*sm.createEntry("3", "<policySet>", "<originalRequest1>", attributes, "status1", "pepURI1", "desk", "fabio");
        sm.updateObjectForSessionAndAttribute("3", "aaa", "desk");
        sm.updateSubjectForSessionAndAttribute("3", "aaa","fabio");
        sm.updateObjectForSessionAndAttribute("3", "bbb", "desk");
        sm.updateSubjectForSessionAndAttribute("3", "bbb","fabio");*/


        sm.stop();
    }
}
