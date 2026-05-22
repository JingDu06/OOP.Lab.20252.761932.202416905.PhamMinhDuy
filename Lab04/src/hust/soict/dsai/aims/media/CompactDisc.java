package hust.soict.dsai.aims.media;

import java.util.ArrayList;

public class CompactDisc extends Disc implements Playable {

    private String artist;
    private ArrayList<Track> tracks = new ArrayList<>();

    public CompactDisc(int id, String title,
                       String category, float cost,
                       int length, String director,
                       String artist) {

        super(id, title, category, cost, length, director);
        this.artist = artist;
    }

    public void addTrack(Track track) {
        if (!tracks.contains(track)) {
            tracks.add(track);
        }
    }

    public void removeTrack(Track track) {
        tracks.remove(track);
    }

    @Override
    public int getLength() {
        int sum = 0;

        for (Track t : tracks) {
            sum += t.getLength();
        }

        return sum;
    }

    @Override
    public void play() {
        System.out.println("Playing CD: " + getTitle());

        for (Track t : tracks) {
            t.play();
        }
    }
}