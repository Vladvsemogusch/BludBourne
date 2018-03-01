package com.packtpub.libgdx.bludbourne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.packtpub.libgdx.bludbourne.EntityConfig;
import com.packtpub.libgdx.bludbourne.Utility;
import com.packtpub.libgdx.bludbourne.dialog.Conversation;
import com.packtpub.libgdx.bludbourne.dialog.ConversationChoice;
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph;

import java.util.ArrayList;

public class ConversationUI extends Window {
    private static final String TAG = ConversationUI.class.getSimpleName();

    private Label dialogText;
    private List listItems;
    private ConversationGraph graph;
    private String currentEntityID;

    private TextButton closeButton;

    private Json json;

    public ConversationUI() {
        super("dialog", Utility.STATUSUI_SKIN, "solidbackground");

        json = new Json();
        graph = new ConversationGraph();

        //create
        dialogText = new Label("No Conversation", Utility.STATUSUI_SKIN);
        dialogText.setWrap(true);
        dialogText.setAlignment(Align.center);
        listItems = new List<ConversationChoice>(Utility.STATUSUI_SKIN);

        closeButton = new TextButton("X", Utility.STATUSUI_SKIN);

        ScrollPane scrollPane = new ScrollPane(listItems, Utility.STATUSUI_SKIN, "inventoryPane");
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(true, false);
        scrollPane.setScrollBarPositions(false, true);

        //layout
        this.add();
        this.add(closeButton);
        this.row();

        this.defaults().expand().fill();
        this.add(dialogText).pad(10, 10, 10, 10);
        this.row();
        this.add(scrollPane).pad(10,10,10,10);

        //this.debug();
        this.pack();

        //Listeners
        listItems.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                ConversationChoice choice = (ConversationChoice)listItems.getSelected();
                if( choice == null ) return;
                graph.notify(graph, choice.getConversationCommandEvent());
                populateConversationDialog(choice.getDestinationId());
            }
                               }
        );
    }

    public TextButton getCloseButton(){
        return closeButton;
    }

    public String getCurrentEntityID() {
        return currentEntityID;
    }

    public void loadConversation(EntityConfig entityConfig){
        String fullFilenamePath = entityConfig.getConversationConfigPath();
        this.setName("");

        clearDialog();

        if( fullFilenamePath.isEmpty() || !Gdx.files.internal(fullFilenamePath).exists() ){
            Gdx.app.debug(TAG, "Conversation file does not exist!");
            return;
        }

        currentEntityID = entityConfig.getEntityID();
        this.setName(entityConfig.getEntityID());

        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));
        setConversationGraph(graph);
    }

    public void setConversationGraph(ConversationGraph graph){
        if( graph != null ) graph.removeAllObservers();
        this.graph = graph;
        populateConversationDialog(graph.getCurrentConversationID());
    }

    public ConversationGraph getCurrentConversationGraph(){
        return this.graph;
    }

    private void populateConversationDialog(String conversationID){
        clearDialog();

        Conversation conversation = graph.getConversationByID(conversationID);
        if( conversation == null ) return;
        graph.setCurrentConversation(conversationID);
        dialogText.setText(conversation.getDialog());
        ArrayList<ConversationChoice> choices =  graph.getCurrentChoices();
        if( choices == null ) return;
        listItems.setItems(choices.toArray());
        listItems.setSelectedIndex(-1);
    }

    private void clearDialog(){
        dialogText.setText("");
        listItems.clearItems();
    }

}
