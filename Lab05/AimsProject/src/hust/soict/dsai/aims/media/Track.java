package hust.soict.dsai.aims.media;

import hust.soict.dsai.aims.exception.PlayerException;

public class Track implements Playable {
    private String title;
    private int length;
    public Track(String title, int length) {
        this.title = title;
        this.length = length;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    @Override
    public void play() throws PlayerException{
        System.out.println("Playing track: " + this.getTitle());
        System.out.println("Track length: " + this.getLength());
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Track)) {
            return false;
        }
        Track other = (Track) obj;

        if (this.getTitle() == null) {
            return other.getTitle() == null;
        }
        
        return this.getTitle().equals(other.getTitle()) && this.getLength() == other.getLength();
    }
}