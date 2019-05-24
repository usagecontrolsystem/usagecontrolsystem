package it.cnr.iit.peprest.messagetrack;

import java.util.List;

public interface MessagesPerSession {

    List<String> getMessagesPerSession( String sessionId );

}
