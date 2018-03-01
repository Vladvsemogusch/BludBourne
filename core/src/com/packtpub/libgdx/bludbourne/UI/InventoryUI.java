package com.packtpub.libgdx.bludbourne.UI;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.packtpub.libgdx.bludbourne.Component;
import com.packtpub.libgdx.bludbourne.Entity;
import com.packtpub.libgdx.bludbourne.InventoryItem;
import com.packtpub.libgdx.bludbourne.InventoryItemFactory;
import com.packtpub.libgdx.bludbourne.InventoryItem.ItemUseType;
import com.packtpub.libgdx.bludbourne.InventoryItem.ItemTypeID;
import com.packtpub.libgdx.bludbourne.Utility;

public class InventoryUI extends Window implements InventorySubject, InventorySlotObserver{

    public final static int numSlots = 50;
    public static final String PLAYER_INVENTORY = "Player_Inventory";
    public static final String STORE_INVENTORY = "Store_Inventory";

    private int lengthSlotRow = 10;
    private Table inventorySlotTable;
    private Table playerSlotsTable;
    private Table equipSlots;
    private DragAndDrop dragAndDrop;
    private Array<Actor> inventoryActors;

    private Label _DPValLabel;
    private int _DPVal = 0;
    private Label _APValLabel;
    private int _APVal = 0;

    private final int slotWidth = 52;
    private final int slotHeight = 52;

    private Array<InventoryObserver> observers;

    private InventorySlotTooltip inventorySlotTooltip;

    public InventoryUI(){
        super("Inventory", Utility.STATUSUI_SKIN, "solidbackground");

        observers = new Array<InventoryObserver>();

        dragAndDrop = new DragAndDrop();
        inventoryActors = new Array<Actor>();

        //create
        inventorySlotTable = new Table();
        inventorySlotTable.setName("Inventory_Slot_Table");

        playerSlotsTable = new Table();
        equipSlots = new Table();
        equipSlots.setName("Equipment_Slot_Table");

        equipSlots.defaults().space(10);
        inventorySlotTooltip = new InventorySlotTooltip(Utility.STATUSUI_SKIN);

        Label DPLabel = new Label("Defense: ", Utility.STATUSUI_SKIN);
        _DPValLabel = new Label(String.valueOf(_DPVal), Utility.STATUSUI_SKIN);

        Label APLabel = new Label("Attack : ", Utility.STATUSUI_SKIN);
        _APValLabel = new Label(String.valueOf(_APVal), Utility.STATUSUI_SKIN);

        Table labelTable = new Table();
        labelTable.add(DPLabel).align(Align.left);
        labelTable.add(_DPValLabel).align(Align.center);
        labelTable.row();
        labelTable.row();
        labelTable.add(APLabel).align(Align.left);
        labelTable.add(_APValLabel).align(Align.center);

        InventorySlot headSlot = new InventorySlot(
                ItemUseType.ARMOR_HELMET.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_helmet")));

        InventorySlot leftArmSlot = new InventorySlot(
                ItemUseType.WEAPON_ONEHAND.getValue() |
                ItemUseType.WEAPON_TWOHAND.getValue() |
                ItemUseType.ARMOR_SHIELD.getValue() |
                ItemUseType.WAND_ONEHAND.getValue() |
                ItemUseType.WAND_TWOHAND.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_weapon"))
        );

        InventorySlot rightArmSlot = new InventorySlot(
                ItemUseType.WEAPON_ONEHAND.getValue() |
                ItemUseType.WEAPON_TWOHAND.getValue() |
                ItemUseType.ARMOR_SHIELD.getValue() |
                ItemUseType.WAND_ONEHAND.getValue() |
                ItemUseType.WAND_TWOHAND.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_shield"))
        );

        InventorySlot chestSlot = new InventorySlot(
                ItemUseType.ARMOR_CHEST.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_chest")));

        InventorySlot legsSlot = new InventorySlot(
                ItemUseType.ARMOR_FEET.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_boot")));

        headSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        leftArmSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        rightArmSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        chestSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        legsSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));

        headSlot.addObserver(this);
        leftArmSlot.addObserver(this);
        rightArmSlot.addObserver(this);
        chestSlot.addObserver(this);
        legsSlot.addObserver(this);

        dragAndDrop.addTarget(new InventorySlotTarget(headSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(leftArmSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(chestSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(rightArmSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(legsSlot));

        playerSlotsTable.setBackground(new Image(new NinePatch(Utility.STATUSUI_TEXTUREATLAS.createPatch("dialog"))).getDrawable());

        //layout
        for(int i = 1; i <= numSlots; i++){
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            inventorySlotTable.add(inventorySlot).size(slotWidth, slotHeight);

            inventorySlot.addListener(new ClickListener() {
                                         @Override
                                         public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                                             super.touchUp(event, x, y, pointer, button);
                                             if( getTapCount() == 2 ){
                                                 InventorySlot slot = (InventorySlot)event.getListenerActor();
                                                 if( slot.hasItem() ){
                                                     InventoryItem item = slot.getTopInventoryItem();
                                                     if( item.isConsumable() ){
                                                         String itemInfo = item.getItemUseType() + Component.MESSAGE_TOKEN + item.getItemUseTypeValue();
                                                         InventoryUI.this.notify(itemInfo, InventoryObserver.InventoryEvent.ITEM_CONSUMED);
                                                         slot.removeActor(item);
                                                         slot.remove(item);
                                                     }
                                                 }
                                             }
                                         }


                                      }
            );


            if(i % lengthSlotRow == 0){
                inventorySlotTable.row();
            }
        }

        equipSlots.add();
        equipSlots.add(headSlot).size(slotWidth, slotHeight);
        equipSlots.row();

        equipSlots.add(leftArmSlot).size(slotWidth, slotHeight);
        equipSlots.add(chestSlot).size(slotWidth, slotHeight);
        equipSlots.add(rightArmSlot).size(slotWidth, slotHeight);
        equipSlots.row();

        equipSlots.add();
        equipSlots.right().add(legsSlot).size(slotWidth, slotHeight);

        playerSlotsTable.add(equipSlots);
        inventoryActors.add(inventorySlotTooltip);

        this.add(playerSlotsTable).padBottom(20);
        this.add(labelTable);
        this.row();
        this.add(inventorySlotTable).colspan(2);
        this.row();
        this.pack();
    }

    public DragAndDrop getDragAndDrop(){
        return dragAndDrop;
    }

    public Table getInventorySlotTable() {
        return inventorySlotTable;
    }

    public Table getEquipSlotTable() {
        return equipSlots;
    }

    public void resetEquipSlots(){
        _DPVal = 0;
        _APVal = 0;

        _DPValLabel.setText(String.valueOf(_DPVal));
        notify(String.valueOf(_DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);

        _APValLabel.setText(String.valueOf(_APVal));
        notify(String.valueOf(_APVal), InventoryObserver.InventoryEvent.UPDATED_AP);
    }

    public static void clearInventoryItems(Table targetTable){
        Array<Cell> cells = targetTable.getCells();
        for( int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot = (InventorySlot)cells.get(i).getActor();
            if( inventorySlot == null ) continue;
            inventorySlot.clearAllInventoryItems(false);
        }
    }

    public static Array<InventoryItemLocation> removeInventoryItems(String name, Table inventoryTable){
        Array<Cell> cells = inventoryTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            inventorySlot.removeAllInventoryItemsWithName(name);
        }
        return items;
    }

    public static void populateInventory(Table targetTable, Array<InventoryItemLocation> inventoryItems, DragAndDrop draganddrop, String defaultName, boolean disableNonDefaultItems){
        clearInventoryItems(targetTable);

        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < inventoryItems.size; i++){
            InventoryItemLocation itemLocation = inventoryItems.get(i);
            ItemTypeID itemTypeID = ItemTypeID.valueOf(itemLocation.getItemTypeAtLocation());
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(itemLocation.getLocationIndex()).getActor());

            for( int index = 0; index < itemLocation.getNumberItemsAtLocation(); index++ ){
                InventoryItem item = InventoryItemFactory.getInstance().getInventoryItem(itemTypeID);
                String itemName =  itemLocation.getItemNameProperty();
                if( itemName == null || itemName.isEmpty() ){
                    item.setName(defaultName);
                }else{
                    item.setName(itemName);
                }

                inventorySlot.add(item);
                if( item.getName().equalsIgnoreCase(defaultName) ){
                    draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
                }else if( disableNonDefaultItems == false ){
                    draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
                }
            }
        }
    }

    public static Array<InventoryItemLocation> getInventory(Table targetTable){
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            int numItems = inventorySlot.getNumItems();
            if( numItems > 0 ){
                items.add(new InventoryItemLocation(
                        i,
                        inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems,
                        inventorySlot.getTopInventoryItem().getName()));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventoryFiltered(Table targetTable, String filterOutName){
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            int numItems = inventorySlot.getNumItems();
            if( numItems > 0 ){
                String topItemName = inventorySlot.getTopInventoryItem().getName();
                if( topItemName.equalsIgnoreCase(filterOutName)) continue;
                //System.out.println("[i] " + i + " itemtype: " + inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems " + numItems);
                items.add(new InventoryItemLocation(
                        i,
                        inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems,
                        inventorySlot.getTopInventoryItem().getName()));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventory(Table targetTable, String name){
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            int numItems = inventorySlot.getNumItems(name);
            if( numItems > 0 ){
                //System.out.println("[i] " + i + " itemtype: " + inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems " + numItems);
                items.add(new InventoryItemLocation(
                        i,
                        inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems,
                        name));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventoryFiltered(Table sourceTable, Table targetTable, String filterOutName){
        Array<InventoryItemLocation> items = getInventoryFiltered(targetTable, filterOutName);
        Array<Cell> sourceCells = sourceTable.getCells();
        int index = 0;
        for( InventoryItemLocation item : items ) {
            for (; index < sourceCells.size; index++) {
                InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
                if (inventorySlot == null) continue;
                int numItems = inventorySlot.getNumItems();
                if (numItems == 0) {
                    item.setLocationIndex(index);
                    //System.out.println("[index] " + index + " itemtype: " + item.getItemTypeAtLocation() + " numItems " + numItems);
                    index++;
                    break;
                }
            }
            if( index == sourceCells.size ){
                //System.out.println("[index] " + index + " itemtype: " + item.getItemTypeAtLocation() + " numItems " + item.getNumberItemsAtLocation());
                item.setLocationIndex(index-1);
            }
        }
        return items;
    }


    public static void setInventoryItemNames(Table targetTable, String name){
        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            inventorySlot.updateAllInventoryItemNames(name);
        }
    }

    public boolean doesInventoryHaveSpace(){
        Array<Cell> sourceCells = inventorySlotTable.getCells();
        int index = 0;

        for (; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if (inventorySlot == null) continue;
            int numItems = inventorySlot.getNumItems();
            if (numItems == 0) {
                return true;
            }else{
                index++;
            }
        }
        return false;
    }

    public void addEntityToInventory(Entity entity, String itemName){
        Array<Cell> sourceCells = inventorySlotTable.getCells();
        int index = 0;

            for (; index < sourceCells.size; index++) {
                InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
                if (inventorySlot == null) continue;
                int numItems = inventorySlot.getNumItems();
                if (numItems == 0) {
                    InventoryItem inventoryItem = InventoryItemFactory.getInstance().getInventoryItem(ItemTypeID.valueOf(entity.getEntityConfig().getItemTypeID()));
                    inventoryItem.setName(itemName);
                    inventorySlot.add(inventoryItem);
                    dragAndDrop.addSource(new InventorySlotSource(inventorySlot, dragAndDrop));
                    break;
                }
            }
    }

    public void removeQuestItemFromInventory(String questID){
        Array<Cell> sourceCells = inventorySlotTable.getCells();
        for (int index = 0; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if (inventorySlot == null) continue;
            InventoryItem item = inventorySlot.getTopInventoryItem();
            if( item == null ) continue;
            String inventoryItemName = item.getName();
            if (inventoryItemName != null && inventoryItemName.equals(questID) ) {
                inventorySlot.clearAllInventoryItems(false);
            }
        }
    }

    public Array<Actor> getInventoryActors(){
        return inventoryActors;
    }

    @Override
    public void onNotify(InventorySlot slot, SlotEvent event) {
        switch(event)
        {
            case ADDED_ITEM:
                InventoryItem addItem = slot.getTopInventoryItem();
                if( addItem == null ) return;
                if( addItem.isInventoryItemOffensive() ){
                    _APVal += addItem.getItemUseTypeValue();
                    _APValLabel.setText(String.valueOf(_APVal));
                    notify(String.valueOf(_APVal), InventoryObserver.InventoryEvent.UPDATED_AP);

                    if( addItem.isInventoryItemOffensiveWand() ){
                        notify(String.valueOf(addItem.getItemUseTypeValue()), InventoryObserver.InventoryEvent.ADD_WAND_AP);
                    }

                }else if( addItem.isInventoryItemDefensive() ){
                    _DPVal += addItem.getItemUseTypeValue();
                    _DPValLabel.setText(String.valueOf(_DPVal));
                    notify(String.valueOf(_DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);
                }
                break;
            case REMOVED_ITEM:
                InventoryItem removeItem = slot.getTopInventoryItem();
                if( removeItem == null ) return;
                if( removeItem.isInventoryItemOffensive() ){
                    _APVal -= removeItem.getItemUseTypeValue();
                    _APValLabel.setText(String.valueOf(_APVal));
                    notify(String.valueOf(_APVal), InventoryObserver.InventoryEvent.UPDATED_AP);

                    if( removeItem.isInventoryItemOffensiveWand() ){
                        notify(String.valueOf(removeItem.getItemUseTypeValue()), InventoryObserver.InventoryEvent.REMOVE_WAND_AP);
                    }

                }else if( removeItem.isInventoryItemDefensive() ){
                    _DPVal -= removeItem.getItemUseTypeValue();
                    _DPValLabel.setText(String.valueOf(_DPVal));
                    notify(String.valueOf(_DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addObserver(InventoryObserver inventoryObserver) {
        observers.add(inventoryObserver);
    }

    @Override
    public void removeObserver(InventoryObserver inventoryObserver) {
        observers.removeValue(inventoryObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(InventoryObserver observer: observers){
            observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(String value, InventoryObserver.InventoryEvent event) {
        for(InventoryObserver observer: observers){
            observer.onNotify(value, event);
        }
    }
}
