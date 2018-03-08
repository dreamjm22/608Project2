package com.example.mvservice2;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.countNonZero;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_NONE;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.minEnclosingCircle;

/**
 * Created by sunting on 10/13/17.
 */

public class MotionDetecor {
    BackgroundSubtractorMOG2 bg;
    int erosion_size = 2;
    int min_contour_area = 1000;
    int max_contour_number = 200;
    Mat element, fgimg, backgroundImage, hierarchy;
    List<MatOfPoint> contours;

    public MotionDetecor(){
        this.bg = Video.createBackgroundSubtractorMOG2();
        this.fgimg = new Mat();
        this.backgroundImage = new Mat();
        this.hierarchy = new Mat();
        this.contours = new ArrayList<MatOfPoint>();
        this.element = getStructuringElement( MORPH_RECT, new Size( 2*erosion_size + 1, 2*erosion_size+1 ), new Point( erosion_size, erosion_size ) );
    }

    public Point MotionDetection(Mat aInputFrame) {

        Point centroid = new Point(0, 0);
        contours.clear();
        bg.apply(aInputFrame, fgimg);

        erode (fgimg, fgimg, element);
        dilate (fgimg, fgimg, element);

        if (countNonZero(fgimg) < fgimg.cols())  // no motion, only noise
        {
            return centroid;
        }

        findContours(fgimg, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_NONE);

        if (contours.size() > max_contour_number) // global motion, max_contour_number = 200;
        {
            return centroid;
        }

        Point center = new Point();
        float[] radius = new float[contours.size()];

        List<MatOfPoint2f> contours2f = new ArrayList<MatOfPoint2f>();

        double center_x = 0, center_y = 0;
        float sum_radius = 0;

        for (int i = 0; i < contours.size(); i++)
        {
            contours2f.add(new MatOfPoint2f());
            contours.get(i).convertTo(contours2f.get(i), CvType.CV_32FC2);
            minEnclosingCircle(contours2f.get(i), center, radius);
            if (Imgproc.contourArea(contours.get(i)) < min_contour_area)
            {
                continue;
            }
            else
            {
                center_x += (center.x * radius[0]);
                center_y += (center.y * radius[0]);
                sum_radius += radius[0];
            }
        }

        if (sum_radius > 0)
        {
            centroid.x = center_x/sum_radius;
            centroid.y = center_y/sum_radius;
        }

        return centroid;

    }

}
