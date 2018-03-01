package com.packtpub.libgdx.bludbourne.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.bludbourne.Entity;
import com.packtpub.libgdx.bludbourne.EntityConfig;
import com.packtpub.libgdx.bludbourne.Utility;
import com.packtpub.libgdx.bludbourne.battle.BattleObserver;
import com.packtpub.libgdx.bludbourne.battle.BattleState;
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory;
import com.packtpub.libgdx.bludbourne.sfx.ShakeCamera;

public class BattleUI extends Window implements BattleObserver {
    private static final String TAG = BattleUI.class.getSimpleName();

    private AnimatedImage image;

    private final int enemyWidth = 96;
    private final int enemyHeight = 96;

    private BattleState battleState = null;
    private TextButton attackButton = null;
    private TextButton runButton = null;
    private Label damageValLabel = null;

    private float battleTimer = 0;
    private final float checkTimer = 1;

    private ShakeCamera battleShakeCam = null;
    private Array<ParticleEffect> effects;

    private float origDamageValLabelY = 0;
    private Vector2 currentImagePosition;

    public BattleUI(){
        super("BATTLE", Utility.STATUSUI_SKIN, "solidbackground");

        battleTimer = 0;
        battleState = new BattleState();
        battleState.addObserver(this);

        effects = new Array<ParticleEffect>();
        currentImagePosition = new Vector2(0,0);

        damageValLabel = new Label("0", Utility.STATUSUI_SKIN);
        damageValLabel.setVisible(false);

        image = new AnimatedImage();
        image.setTouchable(Touchable.disabled);

        Table table = new Table();
        attackButton = new TextButton("Attack", Utility.STATUSUI_SKIN, "inventory");
        runButton = new TextButton("Run", Utility.STATUSUI_SKIN, "inventory");
        table.add(attackButton).pad(20, 20, 20, 20);
        table.row();
        table.add(runButton).pad(20, 20, 20, 20);

        //layout
        this.setFillParent(true);
        this.add(damageValLabel).align(Align.left).padLeft(enemyWidth / 2).row();
        this.add(image).size(enemyWidth, enemyHeight).pad(10, 10, 10, enemyWidth / 2);
        this.add(table);

        this.pack();

        origDamageValLabelY = damageValLabel.getY()+enemyHeight;

        attackButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        battleState.playerAttacks();
                    }
                }
        );
        runButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        battleState.playerRuns();
                    }
                }
        );
    }

    public void battleZoneTriggered(int battleZoneValue){
        battleState.setCurrentZoneLevel(battleZoneValue);
    }

    public boolean isBattleReady(){
        if( battleTimer > checkTimer ){
            battleTimer = 0;
            return battleState.isOpponentReady();
        }else{
            return false;
        }
    }

    public BattleState getCurrentState(){
        return battleState;
    }

    @Override
    public void onNotify(Entity entity, BattleEvent event) {
        switch(event){
            case PLAYER_TURN_START:
                runButton.setDisabled(true);
                runButton.setTouchable(Touchable.disabled);
                attackButton.setDisabled(true);
                attackButton.setTouchable(Touchable.disabled);
                break;
            case OPPONENT_ADDED:
                image.setEntity(entity);
                image.setCurrentAnimation(Entity.AnimationType.IMMOBILE);
                image.setSize(enemyWidth, enemyHeight);

                currentImagePosition.set(image.getX(),image.getY());
                if( battleShakeCam == null ){
                    battleShakeCam = new ShakeCamera(currentImagePosition.x, currentImagePosition.y, 30.0f);
                }

                this.setName("Level " + battleState.getCurrentZoneLevel() + " " + entity.getEntityConfig().getEntityID());
                break;
            case OPPONENT_HIT_DAMAGE:
                int damage = Integer.parseInt(entity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()));
                damageValLabel.setText(String.valueOf(damage));
                damageValLabel.setY(origDamageValLabelY);
                battleShakeCam.startShaking();
                damageValLabel.setVisible(true);
                break;
            case OPPONENT_DEFEATED:
                damageValLabel.setVisible(false);
                damageValLabel.setY(origDamageValLabelY);
                break;
            case OPPONENT_TURN_DONE:
                 attackButton.setDisabled(false);
                 attackButton.setTouchable(Touchable.enabled);
                runButton.setDisabled(false);
                runButton.setTouchable(Touchable.enabled);
                break;
            case PLAYER_TURN_DONE:
                battleState.opponentAttacks();
                break;
            case PLAYER_USED_MAGIC:
                float x = currentImagePosition.x + (enemyWidth/2);
                float y = currentImagePosition.y + (enemyHeight/2);
                effects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.WAND_ATTACK, x,y));
                break;
            default:
                break;
        }
    }

    public void resetDefaults(){
        battleTimer = 0;
        battleState.resetDefaults();
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);

        //Draw the particles last
        for( int i = 0; i < effects.size; i++){
            ParticleEffect effect = effects.get(i);
            if( effect == null ) continue;
            effect.draw(batch);
        }
    }

    @Override
    public void act(float delta){
        battleTimer = (battleTimer + delta)%60;
        if( damageValLabel.isVisible() && damageValLabel.getY() < this.getHeight()){
            damageValLabel.setY(damageValLabel.getY()+5);
        }

        if( battleShakeCam != null && battleShakeCam.isCameraShaking() ){
            Vector2 shakeCoords = battleShakeCam.getNewShakePosition();
            image.setPosition(shakeCoords.x, shakeCoords.y);
        }

        for( int i = 0; i < effects.size; i++){
            ParticleEffect effect = effects.get(i);
            if( effect == null ) continue;
            if( effect.isComplete() ){
                effects.removeIndex(i);
                effect.dispose();
            }else{
                effect.update(delta);
            }
        }

        super.act(delta);
    }
}
