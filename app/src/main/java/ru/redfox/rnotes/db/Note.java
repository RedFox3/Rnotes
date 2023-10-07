package ru.redfox.rnotes.db;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String content;
    @Nullable
    private Uri imageUri;

    public Note(String title, String content, @Nullable Uri imageUri) {
        this.title = title;
        this.content = content;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Nullable
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(@Nullable Uri imageUri) {
        this.imageUri = imageUri;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        imageUri = Uri.parse(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        if(imageUri != null)
            dest.writeString(imageUri.toString());
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
