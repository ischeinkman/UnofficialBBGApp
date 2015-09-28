package org.ramonaza.unofficialazaapp.helpers.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.ramonaza.unofficialazaapp.events.backend.EventRSSHandler;
import org.ramonaza.unofficialazaapp.people.backend.ContactCSVHandler;
import org.ramonaza.unofficialazaapp.people.backend.ContactDatabaseHandler;
import org.ramonaza.unofficialazaapp.people.backend.ContactInfoWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by ilan on 9/17/15.
 */
public class ChapterPackHandler {

    public static final String PREFIX="Chapter Pack";
    public static final String PREF_CHAPTERPACK="Cpack";

    public static final String EVENT_FILE_NAME="EventFeed.txt";
    public static final String PREF_EVENT_FEED="EventFeed";

    public static final String CSV_NAME = "AlephNameSchYAddMailNum.csv";
    public static final String DEFAULT_CSV_NAME="DefaultContactFileTemplate.csv";

    private File chapterPack;
    private boolean isZip;
    private Context context;
    private SharedPreferences prefs;
    private boolean eventsLoaded;
    private boolean contactsLoaded;

    public ChapterPackHandler(File chapterPackFile, Context context){
        this.context=context;
        this.prefs= PreferenceManager.getDefaultSharedPreferences(context);
        isZip=chapterPackFile.getName().contains(".zip");

        File dataDir=context.getExternalFilesDir(null);
        File newFile;
        if(isZip){
            newFile= new File(dataDir, "lastloadedpack");
        }
        else {
            for(File packFile:chapterPackFile.listFiles()){
                File newPackFile=new File(dataDir+"lastloadedpack/",packFile.getName());
                newPackFile.mkdirs();
                newPackFile.delete();
                packFile.renameTo(newPackFile);
            }
            newFile=new File(dataDir+"lastloadedpack");
            chapterPackFile.delete();
        }
        this.chapterPack=newFile;
    }

    public String getPackName(){
        return chapterPack.getName();
    }

    public boolean loadEventFeed(){
        if(eventsLoaded) return true;
        if(isZip) eventsLoaded = loadZipEvents();
        else eventsLoaded = loadFolderEvents();
        return eventsLoaded;
    }

    private boolean loadZipEvents(){
        ZipFile file;
        try {
            file=new ZipFile(chapterPack);
        } catch (IOException e) {
            return false;
        }
        ZipEntry eventZipFile=file.getEntry(EVENT_FILE_NAME);
        InputStream fileStream;
        try {
            fileStream=file.getInputStream(eventZipFile);
        } catch (IOException e) {
            return false;
        }
        Scanner streamScanner=new Scanner(fileStream);
        StringBuilder builder=new StringBuilder(100);
        while(streamScanner.hasNext()){
            builder.append(streamScanner.next());
        }
        String url=builder.toString();
        prefs.edit().putString(PREF_EVENT_FEED, url).commit();
        return true;
    }

    private boolean loadFolderEvents(){
        File[] eventFile=chapterPack.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.equals(EVENT_FILE_NAME);
            }
        });
        if (eventFile == null ||eventFile.length <= 0) return false;
        Scanner fileStream;
        try {
            fileStream=new Scanner(eventFile[0]);
        } catch (FileNotFoundException e) {
            return false;
        }
        StringBuilder builder= new StringBuilder(1000);
        while(fileStream.hasNext()){
            builder.append(fileStream.next());
        }
        String url=builder.toString();
        prefs.edit().putString(PREF_EVENT_FEED, url).commit();
        return true;
    }

    public boolean loadContactList(){
        if(contactsLoaded) return true;
        ContactCSVHandler csvHandler=(isZip) ? loadZipContactList() : loadFolderContactList();
        if(csvHandler == null) contactsLoaded = false;
        ContactDatabaseHandler databaseHandler = new ContactDatabaseHandler(context);
        databaseHandler.deleteContacts(null, null);
        for(ContactInfoWrapper contact : csvHandler.getCtactInfoListFromCSV())
            try {
                databaseHandler.addContact(contact);
            } catch (ContactDatabaseHandler.ContactCSVReadError contactCSVReadError) {
                contactsLoaded = false;
            }
        contactsLoaded = true;
        return contactsLoaded;
    }

    public boolean reLoadContactList(SQLiteDatabase db){
        ContactCSVHandler csvHandler=(isZip) ? loadZipContactList() : loadFolderContactList();
        if(csvHandler == null) contactsLoaded = false;
        ContactDatabaseHandler databaseHandler = new ContactDatabaseHandler(db);
        for(ContactInfoWrapper contact : csvHandler.getCtactInfoListFromCSV())
            try {
                databaseHandler.addContact(contact);
            } catch (ContactDatabaseHandler.ContactCSVReadError contactCSVReadError) {
                contactsLoaded = false;
            }
        contactsLoaded = true;
        return contactsLoaded;
    }

    private ContactCSVHandler loadZipContactList(){
        ZipFile file;
        try {
            file=new ZipFile(chapterPack);
        } catch (IOException e) {
            return null;
        }
        ZipEntry contactZipEntry=file.getEntry(CSV_NAME);
        InputStream fileStream;
        try {
            fileStream=file.getInputStream(contactZipEntry);
        } catch (IOException e) {
            return null;
        }
        return new ContactCSVHandler(fileStream);
    }

    private ContactCSVHandler loadFolderContactList(){
        File[] eventFile=chapterPack.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.equals(CSV_NAME);
            }
        });
        if (eventFile.length <= 0) return null;
        try {
            return new ContactCSVHandler(eventFile[0]);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public ContactDatabaseHandler getContactDatabase(){
        loadContactList();
        return new ContactDatabaseHandler(context);
    }

    public EventRSSHandler getEventRSSHandler(){
        loadEventFeed();
        String url=prefs.getString(PREF_EVENT_FEED,null);
        return new EventRSSHandler(url, true);
    }


}