package me.suncloud.marrymemo.model;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

public class Audio implements Serializable {

    private static final long serialVersionUID = 5196552588639486354L;
    private Music recordMusic;
    private Music fileMusic;
    private Music classicMusic;
    private ArrayList<Music> otherMusics;
    private int kind;

    public Audio(JSONArray array) {
        if (array != null) {
            otherMusics = new ArrayList<>();
            int size = array.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    Music music = new Music(array.optJSONObject(i));
                    if (!music.isDestroy()) {
                        if (!JSONUtil.isEmpty(music.getAudioPath())) {
                            if (music.isSelected()) {
                                kind = music.getKind();
                            }
                            switch (music.getKind()) {
                                case 1:
                                    if (recordMusic != null) {
                                        if (music.isSelected() || !recordMusic.isSelected()) {
                                            otherMusics.add(recordMusic);
                                            recordMusic = music;
                                        } else {
                                            otherMusics.add(music);
                                        }
                                    } else {
                                        recordMusic = music;
                                    }
                                    break;
                                case 2:
                                    if (fileMusic != null) {
                                        if (music.isSelected() || !fileMusic.isSelected()) {
                                            otherMusics.add(fileMusic);
                                            fileMusic = music;
                                        } else {
                                            otherMusics.add(music);
                                        }
                                    } else {
                                        fileMusic = music;
                                    }
                                    break;
                                case 3:
                                    if (classicMusic != null) {
                                        if (music.isSelected() || !classicMusic.isSelected()) {
                                            otherMusics.add(classicMusic);
                                            classicMusic = music;
                                        } else {
                                            otherMusics.add(music);
                                        }
                                    } else {
                                        classicMusic = music;
                                    }
                                    break;
                                default:
                                    otherMusics.add(music);
                                    break;
                            }
                        } else {
                            otherMusics.add(music);
                        }
                    }
                }
            }
        }
    }

    public Music getClassicMusic() {
        return classicMusic;
    }

    public void setClassicMusic(Music classicMusic) {
        this.classicMusic = classicMusic;
    }

    public Music getFileMusic() {
        return fileMusic;
    }

    public void setFileMusic(Music fileMusic) {
        this.fileMusic = fileMusic;
    }

    public Music getRecordMusic() {
        return recordMusic;
    }

    public void setRecordMusic(Music recordMusic) {
        this.recordMusic = recordMusic;
    }

    public String getClassicMusicPath() {
        return classicMusic == null ? "" : classicMusic.getAudioPath();
    }

    public String getClassicMusicM3u8Path() {
        return classicMusic == null ? "" : classicMusic.getM3u8Path();
    }

    public String getFileMusicPath() {
        return fileMusic == null ? "" : fileMusic.getAudioPath();
    }

    public String getRecordMusicPath() {
        return recordMusic == null ? "" : recordMusic.getAudioPath();
    }

    public String getClassicMusicName() {
        return classicMusic == null ? "" : classicMusic.getTitle();
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getFileMusicName() {
        return fileMusic == null ? "" : fileMusic.getTitle();
    }

    public ArrayList<Music> getOtherMusics() {
        return otherMusics;
    }

    public String getCurrentPath() {
        switch (kind) {
            case 1:
                return getRecordMusicPath();
            case 2:
                return getFileMusicPath();
            case 3:
                return getClassicMusicPath();
            default:
                return null;
        }
    }
}
