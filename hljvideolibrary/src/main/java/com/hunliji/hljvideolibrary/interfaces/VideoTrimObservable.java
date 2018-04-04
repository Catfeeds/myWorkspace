package com.hunliji.hljvideolibrary.interfaces;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceViaHeapImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.hunliji.hljcommonlibrary.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;

/**
 * Created by luohanlin on 23/03/2017.
 */

public class VideoTrimObservable {

    public static void startTrim(
            @NonNull File src,
            @NonNull String dst,
            long startMs,
            long endMs,
            @NonNull Subscriber<Uri> subscriber) throws IOException {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.US).format(new Date());
        final String fileName = "TRIM_" + timeStamp + ".mp4";
        final String filePath = dst + fileName;

        File file = new File(filePath);
        file.getParentFile()
                .mkdirs();
        genVideoUsingMp4Parser(src, file, startMs, endMs, subscriber);
    }

    private static void genVideoUsingMp4Parser(
            @NonNull File src,
            @NonNull File dst,
            long startMs,
            long endMs,
            Subscriber<Uri> subscriber) throws IOException {
        Movie movie;
        try {
            movie = MovieCreator.build(new FileDataSourceViaHeapImpl(src.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
            subscriber.onError(new Throwable("剪辑错误，可能由于视频过大，请选择适当大小的视频文件"));
            return;
        }

        List<Track> tracks = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());
        // remove all tracks we will create new tracks from the old

        double startTime1 = startMs / 1000;
        double endTime1 = endMs / 1000;

        boolean timeCorrected = false;

        // Here we try to find a track that has sync samples. Since we can only start decoding
        // at such a sample we SHOULD make sure that the start of the new fragment is exactly
        // such a frame
        for (Track track : tracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                if (timeCorrected) {
                    // This exception here could be a false positive in case we have multiple tracks
                    // with sync samples at exactly the same positions. E.g. a single movie
                    // containing
                    // multiple qualities of the same video (Microsoft Smooth Streaming file)

                    throw new RuntimeException(
                            "The startTime has already been corrected by another track with " +
                                    "SyncSample. Not Supported.");
                }
                startTime1 = correctTimeToSyncSample(track, startTime1, false);
                endTime1 = correctTimeToSyncSample(track, endTime1, true);
                timeCorrected = true;
            }
        }

        for (Track track : tracks) {
            long currentSample = 0;
            double currentTime = 0;
            double lastTime = -1;
            long startSample1 = -1;
            long endSample1 = -1;

            for (int i = 0; i < track.getSampleDurations().length; i++) {
                long delta = track.getSampleDurations()[i];


                if (currentTime > lastTime && currentTime <= startTime1) {
                    // current sample is still before the new starttime
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTime1) {
                    // current sample is after the new start time and still before the new endtime
                    endSample1 = currentSample;
                }
                lastTime = currentTime;
                currentTime += (double) delta / (double) track.getTrackMetaData()
                        .getTimescale();
                currentSample++;
            }
            movie.addTrack(new AppendTrack(new CroppedTrack(track, startSample1, endSample1)));
        }

        dst.getParentFile()
                .mkdirs();

        if (!dst.exists()) {
            dst.createNewFile();
        }

        Container out = new DefaultMp4Builder().build(movie);

        FileOutputStream fos = new FileOutputStream(dst);
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);

        fc.close();
        fos.close();
        subscriber.onNext(Uri.parse(dst.toString()));
        subscriber.onCompleted();
    }

    private static double correctTimeToSyncSample(
            @NonNull Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(),
                        currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData()
                    .getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    public static String getVideoCoverPathFromFrame(Uri uri) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(uri.getPath(),
                MediaStore.Images.Thumbnails.MINI_KIND);
        File f = FileUtil.createImageFile();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return f.getAbsolutePath();
    }
}
