package com.example.tagged.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tagged.song.Song;

@Database(entities = {Song.class}, version = 6, exportSchema = false)
public abstract class SongRoomDatabase extends RoomDatabase
{
    public abstract SongDao songDao();

    private static SongRoomDatabase instance;

    static SongRoomDatabase getDatabase(final Context context)
    {
        //singleton
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), SongRoomDatabase.class, "song_database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }


}





