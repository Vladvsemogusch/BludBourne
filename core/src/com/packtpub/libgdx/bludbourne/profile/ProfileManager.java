package com.packtpub.libgdx.bludbourne.profile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Enumeration;
import java.util.Hashtable;

public class ProfileManager extends ProfileSubject {
    private static final String TAG = ProfileManager.class.getSimpleName();

    private Json json;
    private static ProfileManager profileManager;
    private Hashtable<String,FileHandle> profiles;
    private ObjectMap<String, Object> profileProperties = new ObjectMap<String, Object>();
    private String profileName;
    private boolean isNewProfile;

    private static final String SAVEGAME_SUFFIX = ".sav";
    public static final String DEFAULT_PROFILE = "default";


    private ProfileManager(){
        json = new Json();
        profiles = new Hashtable<String,FileHandle>();
        profiles.clear();
        profileName = DEFAULT_PROFILE;
        storeAllProfiles();
    }

    public static final ProfileManager getInstance(){
        if( profileManager == null){
            profileManager = new ProfileManager();
        }
        return profileManager;
    }

    public void setIsNewProfile(boolean isNewProfile){
        this.isNewProfile = isNewProfile;
    }

    public boolean getIsNewProfile(){
        return this.isNewProfile;
    }

    public Array<String> getProfileList(){
        Array<String> profiles = new Array<String>();
        for (Enumeration<String> e = this.profiles.keys(); e.hasMoreElements();){
            profiles.add(e.nextElement());
        }
        return profiles;
    }

    public FileHandle getProfileFile(String profile){
        if( !doesProfileExist(profile) ){
            return null;
        }
        return profiles.get(profile);
    }

    public void storeAllProfiles(){
        if( Gdx.files.isLocalStorageAvailable() ){
            FileHandle[] files = Gdx.files.local(".").list(SAVEGAME_SUFFIX);

            for(FileHandle file: files) {
                profiles.put(file.nameWithoutExtension(), file);
            }
        }else{
            //TODO: try external directory here
            return;
        }
    }

    public boolean doesProfileExist(String profileName){
        return profiles.containsKey(profileName);
    }

    public void writeProfileToStorage(String profileName, String fileData, boolean overwrite){
        String fullFilename = profileName+SAVEGAME_SUFFIX;

        boolean localFileExists = Gdx.files.local(fullFilename).exists();

        //If we cannot overwrite and the file exists, exit
        if( localFileExists && !overwrite ){
            return;
        }

        FileHandle file =  null;

        if( Gdx.files.isLocalStorageAvailable() ) {
            file = Gdx.files.local(fullFilename);
            String encodedString = Base64Coder.encodeString(fileData);
            file.writeString(encodedString, !overwrite);
        }

        profiles.put(profileName, file);
    }

    public void setProperty(String key, Object object){
        profileProperties.put(key, object);
    }

    public <T extends Object> T getProperty(String key, Class<T> type){
        T property = null;
        if( !profileProperties.containsKey(key) ){
            return property;
        }
        property = (T)profileProperties.get(key);
        return property;
    }

    public void saveProfile(){
        notify(this, ProfileObserver.ProfileEvent.SAVING_PROFILE);
        String text = json.prettyPrint(json.toJson(profileProperties));
        writeProfileToStorage(profileName, text, true);
    }

    public void loadProfile(){
        if( isNewProfile ){
            notify(this, ProfileObserver.ProfileEvent.CLEAR_CURRENT_PROFILE);
            saveProfile();
        }

        String fullProfileFileName = profileName+SAVEGAME_SUFFIX;
        boolean doesProfileFileExist = Gdx.files.local(fullProfileFileName).exists();

        if( !doesProfileFileExist ){
            //System.out.println("File doesn't exist!");
            return;
        }

        FileHandle encodedFile = profiles.get(profileName);
        String s = encodedFile.readString();

        String decodedFile = Base64Coder.decodeString(s);

        profileProperties = json.fromJson(ObjectMap.class, decodedFile);
        notify(this, ProfileObserver.ProfileEvent.PROFILE_LOADED);
        isNewProfile = false;
    }

    public void setCurrentProfile(String profileName){
        if( doesProfileExist(profileName) ){
            this.profileName = profileName;
        }else{
            this.profileName = DEFAULT_PROFILE;
        }
    }

}
