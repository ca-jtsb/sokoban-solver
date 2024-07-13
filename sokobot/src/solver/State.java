package solver;
import java.util.*;

public class State {
    // Private variables to store the player's coordinates, heuristic value, box positions, and path.
    private int coordinateX;
    private int coordinateY;
    private int heuristic = 0;
    private ArrayList<Integer> boxesX;
    private ArrayList<Integer> boxesY;
    StringBuilder path;

    // Constructor to initialize the state with coordinates, box positions, and a path.
    public State(int coordinateX, int coordinateY, ArrayList<Integer> boxesX, ArrayList<Integer> boxesY, String sb) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.boxesX = boxesX;
        this.boxesY = boxesY;
        this.path = new StringBuilder(sb);
    }

    // Customized toString method to represent the state's information.
    public String toString() {
        this.sortBoxes();
        StringBuilder sb = new StringBuilder();
        sb.append(coordinateX + " " + coordinateY + " || ");

        for (int i = 0; i < boxesX.size(); i++) {
            sb.append(boxesX.get(i) + " " + boxesY.get(i) + " ");
        }

        return sb.toString();
    }

    // Create a copy of the state.
    public State copy() {
        ArrayList<Integer> newBoxesX = new ArrayList<>(boxesX);
        ArrayList<Integer> newBoxesY = new ArrayList<>(boxesY);
        String newPath = path.toString();
        State newState = new State(coordinateX, coordinateY, newBoxesX, newBoxesY, newPath);
        newState.setHeuristic(this.heuristic);
        return newState;
    }

    // Check if a box exists at a given coordinate and return its index.
    public int hasBox(int x, int y) {
        for (int i = 0; i < boxesX.size(); i++) {
            if (boxesX.get(i) == x && boxesY.get(i) == y) {
                return i;
            }
        }
        return -1;
    }

    // Sort the box positions lexicographically.
    public void sortBoxes() {
        List<List<Integer>> boxPoints = new ArrayList<List<Integer>>();
        for (int i = 0; i < boxesX.size(); i++) { // O(n)
            ArrayList<Integer> a = new ArrayList<>();
            a.add(boxesX.get(i));
            a.add(boxesY.get(i));
            boxPoints.add(a);
        }
        Collections.sort(boxPoints, new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> a, List<Integer> b) {
                int cmp = Integer.compare(a.get(0), b.get(0));

                if (cmp == 0) {
                    cmp = Integer.compare(a.get(1), b.get(1));
                }
                return cmp;
            }
        });
        for (int i = 0; i < boxPoints.size(); i++) {
            boxesX.set(i, boxPoints.get(i).get(0));
            boxesY.set(i, boxPoints.get(i).get(1));
        }
    }

    // Check if two states are equal by comparing coordinates and sorted box positions.
    public boolean equals(State s) {
        if (this.coordinateX != s.getCoordinateX())
            return false;
        if (this.coordinateY != s.getCoordinateY())
            return false;

        this.sortBoxes();
        s.sortBoxes();
        for (int i = 0; i < boxesX.size(); i++) {
            if (this.getBoxesX().get(i) != s.getBoxesX().get(i) || this.getBoxesY().get(i) != s.getBoxesY().get(i)) {
                return false;
            }
        }
        return true;
    }

    
    // Getter for the heuristic value.
    public int getHeuristic() {
        return this.heuristic;
    }

    // Getter for the depth of the state's path.
    public int getDepth() {
        return path.length();
    }

    // Calculate the A* search cost by adding the heuristic and path length.
    public int getAStar() {
        return heuristic + path.length();
    }

    // Getter methods for coordinate, box positions, and path.
    public int getCoordinateX() {
        return this.coordinateX;
    }

    public int getCoordinateY() {
        return this.coordinateY;
    }

    public ArrayList<Integer> getBoxesX() {
        return this.boxesX;
    }

    public ArrayList<Integer> getBoxesY() {
        return this.boxesY;
    }

    public StringBuilder getPath() {
        return this.path;
    }

    // Setter for the heuristic value.
    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    // Setter methods for coordinate.
    public void setCoordinateX(int x) {
        this.coordinateX = x;
    }

    public void setCoordinateY(int y) {
        this.coordinateY = y;
    }
}
