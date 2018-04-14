package pl.edu.uj.laciak.gesturedetector.utility;

import android.graphics.Point;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by darek on 07.09.17.
 */

public class Node implements Cloneable, Serializable {
    Point point;
    private int changeX;
    private int changeY;
    private Node prevNode;
    private boolean firstNode;

    public Node(int x, int y, Node prevNode) {
        if (x == -1) {
            x++;
        }
        if (y == -1) {
            y++;
        }
//        Log.d("Dodaje", x + "," + y);
        if (prevNode == null) {
            this.changeX = 0;
            this.changeY = 0;
            this.firstNode = true;
        } else {
            this.changeX = prevNode.point.x - x;
            this.changeY = prevNode.point.y - y;
            this.firstNode = false;
        }
        point = new Point(x, y);
        this.prevNode = prevNode;
    }


    public static Node buildTree(List<Integer> points) {
        boolean first = true;
        Constants constants = new Constants() {
            @Override
            Point getPointOfHash(int point) {
                return super.getPointOfHash(point);
            }
        };
        Node node = null, prevNode = null;
        Log.d("PUNKTY", points.toString());
        for (int point : points) {
            Point pointObj = constants.getPointOfHash(point);
            if (first) {
                node = new Node(pointObj.x, pointObj.y, null);
                first = false;
            } else {
                node = new Node(pointObj.x, pointObj.y, node);
            }
        }
        return node;
    }

    public static boolean checkPossibility(Node node, Point lastPoint) {
        boolean possible = false;
        int nextX = lastPoint.x;
        int nextY = lastPoint.y;
//        Log.d("start", nextX + ", " + nextY);
        do {
            nextX = nextX + node.changeX;
            nextY = nextY + node.changeY;
            if (nextX >= 0 && nextX < Constants.CIRCLE_X && nextY >= 0 && nextY < Constants.CIRCLE_Y) {
                possible = true;
//                Log.d("next", nextX + ", " + nextY);
            } else {
//                Log.d("wyszło", nextX + "," + nextY);
//                Log.d("punkt", node.point.x + ", " + node.point.y);
//                Log.d("prev", node.prevNode.point.x + ", " + node.prevNode.point.y);
//                Log.d("next", node.changeX + ", " + node.changeY);
                possible = false;
                break;
            }
        } while (node.hasPrev());
        return possible;
    }

    public static Node transform(Node node, Point point) {
        if (node.point.x == point.x && node.point.y == point.y) {
            return node;
        }
        int nextX = point.x;
        int nextY = point.y;
        Constants constants = new Constants() {
            @Override
            int getPointOfPoint(Point point) {
                return super.getPointOfPoint(point);
            }
        };
        List<Integer> list = new ArrayList<>();
        list.add(constants.getPointOfPoint(point));
        do {
            nextX = nextX + node.changeX;
            nextY = nextY + node.changeY;
            list.add(constants.getPointOfPoint(new Point(nextX, nextY)));
        } while (node.hasPrev());
        Collections.reverse(list);
        return Node.buildTree(list);
    }

    public static List<Integer> cutToMinimum(List<Integer> points) {
        List<Integer> list = new ArrayList<>();
        Constants constants = new Constants() {
            @Override
            Point getPointOfHash(int point) {
                return super.getPointOfHash(point);
            }
        };
        list.add(points.get(0));
        for (int i = 1; i < points.size() - 1; ++i) {
            Point prev = constants.getPointOfHash(points.get(i - 1));
            Point next = constants.getPointOfHash(points.get(i + 1));
            Point current = constants.getPointOfHash(points.get(i));
            double distanceA = Math.hypot(prev.x - current.x, prev.y - current.y);
            double distanceB = Math.hypot(current.x - next.x, current.y - next.y);
            double distanceC = Math.hypot(prev.x - next.x, prev.y - next.y);
            if (distanceA + distanceB != distanceC) {
                list.add(points.get(i));
            }
        }
        list.add(points.get(points.size() - 1));
        return list;
    }

    public static boolean doesNodeContainsOther(Node justMade, Node gesture) {
        Node copy2 = null, copy3 = null, justMadeCope = null, copy4 = null, justMadeCopy2 = null;
        try {
            copy2 = (Node) gesture.clone();
            copy3 = (Node) gesture.clone();
            justMadeCope = (Node) justMade.clone();
            copy4 = (Node) gesture.clone();
            justMadeCopy2 = (Node) justMade.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int prevCount = justMade.count();
        int newCount = gesture.count();
        Log.d("NOWY", prevCount + "");
        Log.d("STARY", newCount + "");
        if (prevCount != newCount && prevCount > 1) {
            return false;
        } else {
            if (prevCount == 1) {
                return true;
            }
            if (!checkPossibility(justMadeCope, copy2.point)) {
                return false;
            } else {
                Node transformed = transform(justMadeCopy2, copy3.point);
                return goThroughNode(copy4, transformed);
            }
        }
    }

    private static boolean goThroughNode(Node node, Node toCompare) {
        boolean exact = true;
        do {
            if (!(node.point.x == toCompare.point.x && node.point.y == toCompare.point.y)) {
                Log.d("Nie zgadza się", "(" + node.point.x + "," + node.point.y + ") (" + toCompare.point.x + "," + toCompare.point.y + ")");
                exact = false;
                //break;
            } else {
                Log.d("Zgadza się", "(" + node.point.x + "," + node.point.y + ") (" + toCompare.point.x + "," + toCompare.point.y + ")");
            }
        } while (node.hasPrev() && toCompare.hasPrev());
        return exact;
    }

    public boolean hasPrev() {
        if (prevNode != null) {
            this.point = this.prevNode.point;
            this.changeY = this.prevNode.changeY;
            this.changeX = this.prevNode.changeX;
            this.firstNode = this.prevNode.firstNode;
            this.prevNode = this.prevNode.prevNode;
            return true;
        }
        return false;
    }

    public int count() {
        int num = 0;
        do {
            ++num;
        } while (this.hasPrev());
        return num;
    }

    @Override
    public String toString() {
        return ("Posiada poprzedni: " + (prevNode != null));
    }
}
