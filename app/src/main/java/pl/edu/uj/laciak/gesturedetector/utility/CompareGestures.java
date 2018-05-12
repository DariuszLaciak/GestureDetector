package pl.edu.uj.laciak.gesturedetector.utility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.LogisticRegression;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by darek on 03.09.17.
 */

public class CompareGestures {

    private static final int SAMPLES = 10;
    private Bitmap gestureJustMade;
    private Cursor allGestures;

    public CompareGestures(Cursor allGestures, Bitmap gestureJustMade) {
        this.allGestures = allGestures;
        this.gestureJustMade = gestureJustMade;
    }

    public int compare(Context context) {

        Mat gestureMat = new Mat();
        Mat gestureTransformed = new Mat();
        gestureJustMade = Utility.resize(gestureJustMade, gestureJustMade.getWidth()/5, gestureJustMade.getHeight()/5);

        MatOfFloat matOfFloat = new MatOfFloat();
        Utils.bitmapToMat(gestureJustMade, gestureMat);

        gestureMat.convertTo(gestureTransformed, CvType.CV_32F);

        Map<Integer, List<Mat>> mapOfStoredGestures = new HashMap<>();
        allGestures.moveToFirst();
        do {
            Bitmap bm = Utility.getBitmapFromFile(allGestures.getString(1), context);
            Mat gesture = new Mat();
            bm = Utility.resize(bm, bm.getWidth()/5, bm.getHeight()/5);
            Utils.bitmapToMat(bm,gesture);
            int gestureId = allGestures.getInt(4);
            if(mapOfStoredGestures.containsKey(gestureId)){
                if(mapOfStoredGestures.get(gestureId).size() < SAMPLES) {
                    mapOfStoredGestures.get(gestureId).add(gesture);
                }
            }
            else {
                List<Mat> list = new ArrayList<>();
                list.add(gesture);
                mapOfStoredGestures.put(gestureId,list);
            }
        }
        while (allGestures.moveToNext());

        Mat trainData = new Mat();
        List<Integer> trainLabs = new ArrayList<Integer>();


        for(Map.Entry<Integer, List<Mat>> entry : mapOfStoredGestures.entrySet()){
            for(Mat mat : entry.getValue()){
                mat.convertTo(mat, CvType.CV_32F);
                trainData.push_back(mat.reshape(1,1));
                trainLabs.add(entry.getKey());
            }
        }

        KNearest knn = KNearest.create();
        knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabs));


        Mat res = new Mat();
        float p = knn.findNearest(gestureTransformed.reshape(1,1), 2, res);
        Log.d("HEHEHHE",p+"");

        return (int) p;
        /*double max = 0;
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
            KNearest knn = KNearest.create();*/
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

            /*LinkedList<MatOfDMatch> dmatchesListOfMat = new LinkedList<>();
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

        return bestMatch;*/
    }

}
