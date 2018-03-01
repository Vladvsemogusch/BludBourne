package com.packtpub.libgdx.bludbourne.tests;

import com.badlogic.gdx.utils.Json;
import com.packtpub.libgdx.bludbourne.quest.QuestGraph;
import com.packtpub.libgdx.bludbourne.quest.QuestTask;
import com.packtpub.libgdx.bludbourne.quest.QuestTaskDependency;

import java.util.Hashtable;

public class QuestGraphTest {
    static Hashtable<String, QuestTask> questTasks;
    static QuestGraph graph;
    static Json json;
    static String quit = "q";
    static String input = "";


    public static void main (String[] arg) {
        json = new Json();

        questTasks = new Hashtable<String, QuestTask>();

        QuestTask firstTask = new QuestTask();
        firstTask.setId("500");
        firstTask.setTaskPhrase("Come back to me with the bones");

        QuestTask secondTask = new QuestTask();
        secondTask.setId("601");
        secondTask.setTaskPhrase("Pickup 5 bones from the Isle of Death");

        questTasks.put(firstTask.getId(), firstTask);
        questTasks.put(secondTask.getId(), secondTask);

        graph = new QuestGraph();
        graph.setTasks(questTasks);

        QuestTaskDependency firstDep = new QuestTaskDependency();
        firstDep.setSourceId(firstTask.getId());
        firstDep.setDestinationId(secondTask.getId());

        QuestTaskDependency cycleDep = new QuestTaskDependency();
        cycleDep.setSourceId(secondTask.getId());
        cycleDep.setDestinationId(firstTask.getId());

        graph.addDependency(firstDep);
        graph.addDependency(cycleDep);

        System.out.println(graph.toString());

        questTasks.clear();
        graph.clear();

        QuestTask q1 = new QuestTask();
        q1.setId("1");
        q1.setTaskPhrase("Come back to me with the items");

        QuestTask q2 = new QuestTask();
        q2.setId("2");
        q2.setTaskPhrase("Collect 5 horns");

        QuestTask q3 = new QuestTask();
        q3.setId("3");
        q3.setTaskPhrase("Collect 5 furs");

        QuestTask q4 = new QuestTask();
        q4.setId("4");
        q4.setTaskPhrase("Find the area where the Tuskan beast feasts");

        questTasks.put(q1.getId(), q1);
        questTasks.put(q2.getId(), q2);
        questTasks.put(q3.getId(), q3);
        questTasks.put(q4.getId(), q4);

        graph.setTasks(questTasks);

        QuestTaskDependency qDep1 = new QuestTaskDependency();
        qDep1.setSourceId(q1.getId());
        qDep1.setDestinationId(q2.getId());

        QuestTaskDependency qDep2 = new QuestTaskDependency();
        qDep2.setSourceId(q1.getId());
        qDep2.setDestinationId(q3.getId());

        QuestTaskDependency qDep3 = new QuestTaskDependency();
        qDep3.setSourceId(q2.getId());
        qDep3.setDestinationId(q4.getId());

        QuestTaskDependency qDep4 = new QuestTaskDependency();
        qDep4.setSourceId(q3.getId());
        qDep4.setDestinationId(q4.getId());

        graph.addDependency(qDep1);
        graph.addDependency(qDep2);
        graph.addDependency(qDep3);
        graph.addDependency(qDep4);

        System.out.println(json.prettyPrint(graph));

        questTasks.clear();
        graph.clear();

        QuestTask q01 = new QuestTask();
        q01.setId("1");
        q01.setTaskPhrase("Come back to me with the herbs");
        q01.resetAllProperties();

        QuestTask q02 = new QuestTask();
        q02.setId("2");
        q02.setTaskPhrase("Please collect 5 herbs for my sick mother");
        q02.resetAllProperties();

        questTasks.put(q01.getId(), q01);
        questTasks.put(q02.getId(), q02);

        graph.setTasks(questTasks);

        QuestTaskDependency qDep01 = new QuestTaskDependency();
        qDep01.setSourceId(q01.getId());
        qDep01.setDestinationId(q02.getId());

        graph.addDependency(qDep01);

        System.out.println(json.prettyPrint(graph));


    }
}
