package hust.soict.dsai.aims.media;

import java.util.ArrayList;
import java.util.List;
import hust.soict.dsai.aims.exception.PlayerException;

public class CompactDisc extends Disc implements Playable{
    private String artist;
    private List<Track> tracks = new ArrayList<Track>();
    
    public CompactDisc(String title, String category, String director, float cost) {
        super(title, category, director, cost);
    }
    
    public CompactDisc(String title, String category, float cost) {
        super(title, category, cost);
    }
    
    public CompactDisc(String title) {
        super(title);
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void addTrack(Track t){
        if(tracks.contains(t)){
            System.out.println("Da ton tai bai hat trong dia: " + t.getTitle());
        }
        else{
            tracks.add(t);
            System.out.println("Da them bai hat: " + t.getTitle());
        }
    }
    
    public void removeTrack(Track t){
        if(!tracks.contains(t)){
            System.out.println("Bai hat khong ton tai trong dia nay: " + t.getTitle());
        }
        else{
            tracks.remove(t);
        }
    }
    
    public int getLength(){
        int sum = 0;
        for(Track t : tracks){
            sum += t.getLength();
        }
        this.setLength(sum);
        return sum;
    }
    
    @Override
    public void play() throws PlayerException {
        if (this.getLength() > 0) {
            System.out.println("Playing CD: " + this.getTitle());
            System.out.println("CD length: " + this.getLength());
            
            java.util.Iterator<Track> iter = tracks.iterator();
            Track nextTrack;
            while(iter.hasNext()) {
                nextTrack = iter.next();
                try {
                    nextTrack.play();
                } catch(PlayerException e) {
                    throw e;
                }
            }
            System.out.println("End of CD: "+ this.getTitle());
        } else {
            throw new PlayerException("ERROR: CD length is non-positive!");
        }
    }
}