package com.packtpub.libgdx.bludbourne.UI;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.bludbourne.Utility;
import com.packtpub.libgdx.bludbourne.battle.LevelTable;

public class StatusUI extends Window implements StatusSubject {
    private Image hpBar;
    private Image mpBar;
    private Image xpBar;

    private ImageButton inventoryButton;
    private ImageButton questButton;
    private Array<StatusObserver> observers;

    private Array<LevelTable> levelTables;
    private static final String LEVEL_TABLE_CONFIG = "scripts/level_tables.json";

    //Attributes
    private int levelVal = -1;
    private int goldVal = -1;
    private int hpVal = -1;
    private int mpVal = -1;
    private int xpVal = 0;

    private int xpCurrentMax = -1;
    private int hpCurrentMax = -1;
    private int mpCurrentMax = -1;

    private Label hpValLabel;
    private Label mpValLabel;
    private Label xpValLabel;
    private Label levelValLabel;
    private Label goldValLabel;

    private float barWidth = 0;
    private float barHeight = 0;

    public StatusUI(){
        super("stats", Utility.STATUSUI_SKIN);

        levelTables = LevelTable.getLevelTables(LEVEL_TABLE_CONFIG);

        observers = new Array<StatusObserver>();

        //groups
        WidgetGroup group = new WidgetGroup();
        WidgetGroup group2 = new WidgetGroup();
        WidgetGroup group3 = new WidgetGroup();

        //images
        hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
        Image bar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
        mpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("MP_Bar"));
        Image bar2 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
        xpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("XP_Bar"));
        Image bar3 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));

        barWidth = hpBar.getWidth();
        barHeight = hpBar.getHeight();


        //labels
        Label hpLabel = new Label(" hp: ", Utility.STATUSUI_SKIN);
        hpValLabel = new Label(String.valueOf(hpVal), Utility.STATUSUI_SKIN);
        Label mpLabel = new Label(" mp: ", Utility.STATUSUI_SKIN);
        mpValLabel = new Label(String.valueOf(mpVal), Utility.STATUSUI_SKIN);
        Label xpLabel = new Label(" xp: ", Utility.STATUSUI_SKIN);
        xpValLabel = new Label(String.valueOf(xpVal), Utility.STATUSUI_SKIN);
        Label levelLabel = new Label(" lv: ", Utility.STATUSUI_SKIN);
        levelValLabel = new Label(String.valueOf(levelVal), Utility.STATUSUI_SKIN);
        Label goldLabel = new Label(" gp: ", Utility.STATUSUI_SKIN);
        goldValLabel = new Label(String.valueOf(goldVal), Utility.STATUSUI_SKIN);

        //buttons
        inventoryButton= new ImageButton(Utility.STATUSUI_SKIN, "inventory-button");
        inventoryButton.getImageCell().size(32, 32);

        questButton = new ImageButton(Utility.STATUSUI_SKIN, "quest-button");
        questButton.getImageCell().size(32,32);

        //Align images
        hpBar.setPosition(3, 6);
        mpBar.setPosition(3, 6);
        xpBar.setPosition(3, 6);

        //add to widget groups
        group.addActor(bar);
        group.addActor(hpBar);
        group2.addActor(bar2);
        group2.addActor(mpBar);
        group3.addActor(bar3);
        group3.addActor(xpBar);

        //Add to layout
        defaults().expand().fill();

        //account for the title padding
        this.pad(this.getPadTop() + 10, 10, 10, 10);

        this.add();
        this.add(questButton).align(Align.center);
        this.add(inventoryButton).align(Align.right);
        this.row();

        this.add(group).size(bar.getWidth(), bar.getHeight()).padRight(10);
        this.add(hpLabel);
        this.add(hpValLabel).align(Align.left);
        this.row();

        this.add(group2).size(bar2.getWidth(), bar2.getHeight()).padRight(10);
        this.add(mpLabel);
        this.add(mpValLabel).align(Align.left);
        this.row();

        this.add(group3).size(bar3.getWidth(), bar3.getHeight()).padRight(10);
        this.add(xpLabel);
        this.add(xpValLabel).align(Align.left).padRight(20);
        this.row();

        this.add(levelLabel).align(Align.left);
        this.add(levelValLabel).align(Align.left);
        this.row();
        this.add(goldLabel);
        this.add(goldValLabel).align(Align.left);

        //this.debug();
        this.pack();
    }

    public ImageButton getInventoryButton() {
        return inventoryButton;
    }

    public ImageButton getQuestButton() {
        return questButton;
    }

    public int getLevelValue(){
        return levelVal;
    }
    public void setLevelValue(int levelValue){
        this.levelVal = levelValue;
        levelValLabel.setText(String.valueOf(levelVal));
        notify(levelVal, StatusObserver.StatusEvent.UPDATED_LEVEL);
    }

    public int getGoldValue(){
        return goldVal;
    }
    public void setGoldValue(int goldValue){
        this.goldVal = goldValue;
        goldValLabel.setText(String.valueOf(goldVal));
        notify(goldVal, StatusObserver.StatusEvent.UPDATED_GP);
    }

    public void addGoldValue(int goldValue){
        this.goldVal += goldValue;
        goldValLabel.setText(String.valueOf(goldVal));
        notify(goldVal, StatusObserver.StatusEvent.UPDATED_GP);
    }

    public int getXPValue(){
        return xpVal;
    }

    public void addXPValue(int xpValue){
        this.xpVal += xpValue;

        if( xpVal > xpCurrentMax ){
            updateToNewLevel();
        }

        xpValLabel.setText(String.valueOf(xpVal));

        updateBar(xpBar, xpVal, xpCurrentMax);

        notify(xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValue(int xpValue){
        this.xpVal = xpValue;

        if( xpVal > xpCurrentMax ){
            updateToNewLevel();
        }

        xpValLabel.setText(String.valueOf(xpVal));

        updateBar(xpBar, xpVal, xpCurrentMax);

        notify(xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValueMax(int maxXPValue){
        this.xpCurrentMax = maxXPValue;
    }

    public void setStatusForLevel(int level){
        for( LevelTable table: levelTables ){
            if( Integer.parseInt(table.getLevelID()) == level ){
                setXPValueMax(table.getXpMax());
                setXPValue(0);

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                return;
            }
        }
    }

    public void updateToNewLevel(){
        for( LevelTable table: levelTables ){
            //System.out.println("XPVAL " + xpVal + " table XPMAX " + table.getXpMax() );
            if( xpVal > table.getXpMax() ){
                continue;
            }else{
                setXPValueMax(table.getXpMax());

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                notify(levelVal, StatusObserver.StatusEvent.LEVELED_UP);
                return;
            }
        }
    }

    public int getXPValueMax(){
        return xpCurrentMax;
    }

    //HP
    public int getHPValue(){
        return hpVal;
    }

    public void removeHPValue(int hpValue){
        hpVal = MathUtils.clamp(hpVal - hpValue, 0, hpCurrentMax);
        hpValLabel.setText(String.valueOf(hpVal));

        updateBar(hpBar, hpVal, hpCurrentMax);

        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void addHPValue(int hpValue){
        hpVal = MathUtils.clamp(hpVal + hpValue, 0, hpCurrentMax);
        hpValLabel.setText(String.valueOf(hpVal));

        updateBar(hpBar, hpVal, hpCurrentMax);

        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValue(int hpValue){
        this.hpVal = hpValue;
        hpValLabel.setText(String.valueOf(hpVal));

        updateBar(hpBar, hpVal, hpCurrentMax);

        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValueMax(int maxHPValue){
        this.hpCurrentMax = maxHPValue;
    }

    public int getHPValueMax(){
        return hpCurrentMax;
    }

    //MP
    public int getMPValue(){
        return mpVal;
    }

    public void removeMPValue(int mpValue){
        mpVal = MathUtils.clamp(mpVal - mpValue, 0, mpCurrentMax);
        mpValLabel.setText(String.valueOf(mpVal));

        updateBar(mpBar, mpVal, mpCurrentMax);

        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void addMPValue(int mpValue){
        mpVal = MathUtils.clamp(mpVal + mpValue, 0, mpCurrentMax);
        mpValLabel.setText(String.valueOf(mpVal));

        updateBar(mpBar, mpVal, mpCurrentMax);

        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValue(int mpValue){
        this.mpVal = mpValue;
        mpValLabel.setText(String.valueOf(mpVal));

        updateBar(mpBar, mpVal, mpCurrentMax);

        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValueMax(int maxMPValue){
        this.mpCurrentMax = maxMPValue;
    }

    public int getMPValueMax(){
        return mpCurrentMax;
    }

    public void updateBar(Image bar, int currentVal, int maxVal){
        int val = MathUtils.clamp(currentVal, 0, maxVal);
        float tempPercent = (float) val / (float) maxVal;
        float percentage = MathUtils.clamp(tempPercent, 0, 100);
        bar.setSize(barWidth*percentage, barHeight);
    }

    @Override
    public void addObserver(StatusObserver statusObserver) {
        observers.add(statusObserver);
    }

    @Override
    public void removeObserver(StatusObserver statusObserver) {
        observers.removeValue(statusObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(StatusObserver observer: observers){
            observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(int value, StatusObserver.StatusEvent event) {
        for(StatusObserver observer: observers){
            observer.onNotify(value, event);
        }
    }

}
