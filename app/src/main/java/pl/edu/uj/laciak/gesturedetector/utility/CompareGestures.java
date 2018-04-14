package pl.edu.uj.laciak.gesturedetector.utility;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.ml.KNearest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by darek on 03.09.17.
 */

public class CompareGestures {

    private Map<Integer, Bitmap> allGesturesStored;
    private Bitmap gestureJustMade;

    public CompareGestures(Map<Integer, Bitmap> allGesturesStored, Bitmap gestureJustMade) {
        this.allGesturesStored = allGesturesStored;
        this.gestureJustMade = gestureJustMade;
    }

    public int compare() {
        double max = 0;
        int bestMatch = 0;
        double related = 0;

        Mat gestureMat = new Mat();


        Utils.bitmapToMat(gestureJustMade, gestureMat);


        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        MatOfKeyPoint madeGesture = new MatOfKeyPoint();
        detector.detect(gestureMat, madeGesture);


        //Log.d("LOG!","Pierwotny gest: "+madeGesture.size());
        Mat descriptorMade = new Mat();
        extractor.compute(gestureMat, madeGesture, descriptorMade);
        List<KeyPoint> madeGestureKeyPoints = madeGesture.toList();
        int number = 1;
        for (Map.Entry<Integer, Bitmap> entry : allGesturesStored.entrySet()) {
            Mat gestureToCompare = new Mat();
            Utils.bitmapToMat(entry.getValue(), gestureToCompare);

            MatOfKeyPoint gesture = new MatOfKeyPoint();
            detector.detect(gestureToCompare, gesture);
            //Log.d("LOG!", "Obrazek nr "+(number++)+": "+gesture.size());
            List<KeyPoint> storedGestureKeyPoints = gesture.toList();
            Mat descriptorExisted = new Mat();
            extractor.compute(gestureToCompare, gesture, descriptorExisted);
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);
            KNearest knn = KNearest.create();
            /*MatOfDMatch dmatchesListOfMat = new MatOfDMatch();
            matcher.match(descriptorMade, descriptorExisted, dmatchesListOfMat);

            LinkedList<DMatch> good_matchesList = new LinkedList<>();
            for (DMatch match : dmatchesListOfMat.toList()) {
                if(match.distance < 5){
                    good_matchesList.add(match);
                }
            }
            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(good_matchesList);*/

            //Log.d("LOG!","Dobre trafienia: "+ goodMatches.size());

            LinkedList<MatOfDMatch> dmatchesListOfMat = new LinkedList<>();
            matcher.knnMatch(descriptorMade, descriptorExisted, dmatchesListOfMat, 4);

            LinkedList<DMatch> good_matchesList = new LinkedList<>();
            for (int matchIndx = 0; matchIndx < dmatchesListOfMat.size(); matchIndx++) {
                double ratio = 0.8;
                if (dmatchesListOfMat.get(matchIndx).toArray()[0].distance < ratio * dmatchesListOfMat.get(matchIndx).toArray()[1].distance) {
                    good_matchesList.addLast(dmatchesListOfMat.get(matchIndx).toArray()[0]);
                }
            }


            double drawingPoints = gesture.size().height;
            double matchedPoints = good_matchesList.size();
            double drawedPoints = madeGesture.size().height;
            Log.d("he", good_matchesList.size() + ", " + drawedPoints);

            double relatedMatch = matchedPoints / drawingPoints;
            double toDrawed = matchedPoints / drawedPoints;
            double differenceSizes = drawingPoints / drawedPoints;

            double result = relatedMatch * toDrawed * 100;

            if (max < matchedPoints) {
                max = matchedPoints;
                bestMatch = entry.getKey();
                related = result;
            }
        }
        Log.d("LOG!", "Najlepszy match: " + bestMatch + ", wynosi: " + related + "%");
        if (related < 2) {
            return -1;
        }

        return bestMatch;
    }

}
