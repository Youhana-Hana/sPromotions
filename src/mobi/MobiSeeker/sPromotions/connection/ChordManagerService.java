package mobi.MobiSeeker.sPromotions.connection;

import java.util.List;

import mobi.MobiSeeker.sPromotions.activites.Promotions;

import com.samsung.chord.ChordManager;

public class ChordManagerService {

    protected ChordManager chordManager = null;

    public ChordManagerService(ChordManager chordManager) {
        this.chordManager = chordManager;
    }

    public List<Integer> getAvailableInterfaceTypes() {
        return this.chordManager.getAvailableInterfaceTypes();
    }

    public String getChordFilePath() {
        return Promotions.getContext().getFilesDir().getAbsolutePath()+"/"+Promotions.Remote;
    }
}
