package com.packtpub.libgdx.bludbourne.tests;


import com.packtpub.libgdx.bludbourne.dialog.Conversation;
import com.packtpub.libgdx.bludbourne.dialog.ConversationChoice;
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph;

import java.util.ArrayList;
import java.util.Hashtable;

public class ConversationGraphTest {
    static Hashtable<String, Conversation> conversations;
    static ConversationGraph graph;
    static String quit = "q";
    static String input = "";

    public static void main (String[] arg) {
        conversations = new Hashtable<String, Conversation>();

        Conversation start = new Conversation();
        start.setId("500");
        start.setDialog("Do you want to play a game?");

        Conversation yesAnswer = new Conversation();
        yesAnswer.setId("601");
        yesAnswer.setDialog("BOOM! Bombs dropping everywhere");

        Conversation noAnswer = new Conversation();
        noAnswer.setId("802");
        noAnswer.setDialog("Too bad!");

        Conversation unconnectedTest = new Conversation();
        unconnectedTest.setId("250");
        unconnectedTest.setDialog("I am unconnected");

        conversations.put(start.getId(), start);
        conversations.put(noAnswer.getId(), noAnswer);
        conversations.put(yesAnswer.getId(), yesAnswer);
        conversations.put(unconnectedTest.getId(), unconnectedTest);

        graph = new ConversationGraph(conversations, start.getId());

        ConversationChoice yesChoice = new ConversationChoice();
        yesChoice.setSourceId(start.getId());
        yesChoice.setDestinationId(yesAnswer.getId());
        yesChoice.setChoicePhrase("YES");

        ConversationChoice noChoice = new ConversationChoice();
        noChoice.setSourceId(start.getId());
        noChoice.setDestinationId(noAnswer.getId());
        noChoice.setChoicePhrase("NO");

        ConversationChoice startChoice01 = new ConversationChoice();
        startChoice01.setSourceId(yesAnswer.getId());
        startChoice01.setDestinationId(start.getId());
        startChoice01.setChoicePhrase("Go to beginning!");

        ConversationChoice startChoice02 = new ConversationChoice();
        startChoice02.setSourceId(noAnswer.getId());
        startChoice02.setDestinationId(start.getId());
        startChoice02.setChoicePhrase("Go to beginning!");

        graph.addChoice(yesChoice);
        graph.addChoice(noChoice);
        graph.addChoice(startChoice01);
        graph.addChoice(startChoice02);

        //System.out.println(graph.toString());
        //System.out.println(graph.displayCurrentConversation());
        //System.out.println(graph.toJson());

        while( !input.equalsIgnoreCase(quit) ){
            Conversation conversation = getNextChoice();
            if( conversation == null ) continue;
            graph.setCurrentConversation(conversation.getId());
            //System.out.println(graph.displayCurrentConversation());
        }
    }

    public static Conversation getNextChoice(){
        ArrayList<ConversationChoice> choices = graph.getCurrentChoices();
        for(ConversationChoice choice: choices){
            //System.out.println(choice.getDestinationId() + " " + choice.getChoicePhrase());
        }
        input = System.console().readLine();

        Conversation choice = null;
        try {
            choice = graph.getConversationByID(input);
        }catch( NumberFormatException nfe){
            return null;
        }
        return choice;
    }

}
