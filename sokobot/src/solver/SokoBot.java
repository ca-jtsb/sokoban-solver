package solver;
import java.util.*;
import java.lang.Math;

public class SokoBot {

  int width;
  int height;
  char[][] mapData;
  char[][] itemsData;

  int generatedStates = 0;
  int repeatedStates = 0;
  int deadlockStatesFound = 0;
  int nodesInFrontier = 0;
  int exploredStates = 0;
  int numMoves = 0;

  
  HashSet<String> deadlock;
  State g; // Goal state
  
  // Creates the starting state for the Sokoban puzzle.
  public State createStart(){
    int coordinateX = 0, coordinateY = 0;
    ArrayList<Integer> boxesX = new ArrayList<>();
    ArrayList<Integer> boxesY = new ArrayList<>();
    for(int y = 0; y < height; y++){
      for(int x = 0; x < width; x++){
        if(itemsData[y][x] == '@'){
          coordinateX = x;
          coordinateY = y;
        }
        if(itemsData[y][x] == '$'){
          boxesX.add(x);
          boxesY.add(y);
        }
      }
    }
    return new State(coordinateX, coordinateY, boxesX, boxesY, "");
  }

  // Creates the goal state 
  public State createGoals(){
    ArrayList<Integer> boxesX = new ArrayList<>();
    ArrayList<Integer> boxesY = new ArrayList<>();
    // Initialize the goal state based on the map data
    for(int y = 0; y < height; y++){
      for(int x = 0; x < width; x++){
        if(mapData[y][x] == '.'){
          boxesX.add(x);
          boxesY.add(y);
        }
      }
    }
    return new State(-1, -1, boxesX, boxesY, "");
  }

  //Explores possible next states from current state
  public ArrayList<State> exploreState(State s, HashSet<String>explored){
    int coordinateX = s.getCoordinateX();
    int coordinateY = s.getCoordinateY();
    ArrayList<State> toExplore = new ArrayList<>(4);
    // Check moving up
    if (mapData[coordinateY -1][coordinateX] != '#'){
      int boxLoc = s.hasBox(coordinateX, coordinateY - 1);
      if(boxLoc != -1){
        // check if a box can be pushed up
        if(coordinateY - 2 >= 0 && coordinateY - 2 < height)
        if(s.hasBox(coordinateX, coordinateY - 2) == -1 && mapData[coordinateY-2][coordinateX] !='#' && !isDeadlock(coordinateX, coordinateY - 2)){
          // create a new state for the box pushed up
          State up = s.copy();
          up.setCoordinateY(coordinateY - 1);
          up.getBoxesY().set(boxLoc, coordinateY - 2);
          up.sortBoxes();
          up.setHeuristic(computeHeuristic(up));
          up.getPath().append("u");
            toExplore.add(up);
          if(!explored.contains(up.toString()))
            if(isGoal(up))
              return toExplore;
        }
      }
      else{
        // create a new state by moving up without pushing a box
        State up = s.copy();
        up.setCoordinateY(coordinateY-1);
        if(!explored.contains(up.toString())){

          up.setHeuristic(computeHeuristic(up));
          up.getPath().append("u");
          toExplore.add(up);
        }
      }
    }
    // repeat similar checks for moving down, left and right.
    if(mapData[coordinateY+1][coordinateX] != '#'){
      int boxLoc = s.hasBox(coordinateX, coordinateY + 1);
      if(boxLoc != -1){
        if(coordinateY + 2 >= 0 && coordinateY + 2 < height)
        if(s.hasBox(coordinateX, coordinateY + 2) == -1 && mapData[coordinateY+2][coordinateX] != '#'  && !isDeadlock(coordinateX, coordinateY + 2)){
            State down = s.copy();
          down.setCoordinateY(coordinateY + 1);
          down.getBoxesY().set(boxLoc, coordinateY + 2);
          down.sortBoxes();
          down.setHeuristic(computeHeuristic(down));
          down.getPath().append("d");
            toExplore.add(down);
          if(!explored.contains(down.toString()))
            if(isGoal(down))
              return toExplore;
        }
      }
      else{
        State down = s.copy();
        down.setCoordinateY(coordinateY + 1);
        if(!explored.contains(down.toString())){

          down.setHeuristic(computeHeuristic(down));
          down.getPath().append("d");
          toExplore.add(down);
        }
      }
    }
    if(mapData[coordinateY][coordinateX-1] != '#'){
      int boxLoc = s.hasBox(coordinateX - 1, coordinateY);
      if(boxLoc != -1){
        if(coordinateX - 2 >= 0 && coordinateX - 2 < width)
        if(s.hasBox(coordinateX - 2, coordinateY) == -1 && mapData[coordinateY][coordinateX - 2] != '#'  && !isDeadlock(coordinateX - 2, coordinateY)){
          State left = s.copy();
          left.setCoordinateX(coordinateX - 1);
          left.getBoxesX().set(boxLoc, coordinateX - 2);
          left.sortBoxes();
          left.setHeuristic(computeHeuristic(left));
          left.getPath().append("l");
            toExplore.add(left);
          if(!explored.contains(left.toString()))
            if(isGoal(left))
              return toExplore;
        }
      }
      else{
        State left = s.copy();
        left.setCoordinateX(coordinateX - 1);
        if(!explored.contains(left.toString())){

          left.getPath().append("l");
          left.setHeuristic(computeHeuristic(left));
          toExplore.add(left);
        }
      }
    }
    if(mapData[coordinateY][coordinateX + 1] != '#'){
      int boxLoc = s.hasBox(coordinateX + 1, coordinateY);
      if(boxLoc != -1){
        if(coordinateX + 2 >= 0 && coordinateX + 2 < width)
        if(s.hasBox(coordinateX + 2, coordinateY) == -1 && mapData[coordinateY][coordinateX + 2] != '#'  && !isDeadlock(coordinateX + 2, coordinateY)){
          State right = s.copy();
          right.setCoordinateX(coordinateX + 1);
          right.getBoxesX().set(boxLoc, coordinateX + 2);
          right.sortBoxes();
          right.setHeuristic(computeHeuristic(right));
          right.getPath().append("r");
          toExplore.add(right);
          if(!explored.contains(right.toString()))
            if(isGoal(right))
              return toExplore;
        }
      }
      else{
        State right = s.copy();
        right.setCoordinateX(coordinateX + 1);
        if(!explored.contains(right.toString())){
          right.getPath().append("r");
          right.setHeuristic(computeHeuristic(right));
          toExplore.add(right);
        }
      }
    }
    return toExplore;
  }

  // Checks if a given position is a "deadlock"
  public boolean isDeadlock(int x, int y){
    return deadlock.contains(y + " " + x);
  }
  
  // Performs greedy best-first search from the start state 's'
  public State gbfs(State s){
    Comparator<State> comp = Comparator.comparing(State :: getHeuristic).thenComparing(State :: getDepth);
    PriorityQueue<State> frontier = new PriorityQueue<>(comp);
    HashSet<String> explored = new HashSet<>();
    State curr = null;
    int nodectr = 0;
    frontier.add(s);
    while(frontier.size() > 0){
      curr = frontier.poll();
      if(isGoal(curr)){
        System.out.println("is goal is true");
        System.out.println("Search done");
        System.out.println("explored nodes: " + nodectr);
        return curr;
      }
      if(!explored.contains(curr.toString())){
        ArrayList<State> toExplore = exploreState(curr, explored);
        nodesInFrontier = frontier.size(); // Update nodes in frontier count
        for(State e : toExplore){
          if(isGoal(e)){
            System.out.println("explored nodes: " + explored.size());
            return e;
          }
          else
          generatedStates++; // Increment generated states count
            if (explored.contains(e.toString())) {
              repeatedStates++; // Increment repeated states count
            }
            frontier.offer(e);
            exploredStates++;
        }
        explored.add(curr.toString());
        nodectr++;
      }  
    }
    while (frontier.size() > 0) {
      curr = frontier.poll();
      if (isGoal(curr)) {
          System.out.println("is goal is true");
          System.out.println("Search done");
          System.out.println("explored nodes: " + nodectr);
          return curr;
      }
      if (!explored.contains(curr.toString())) {
          ArrayList<State> toExplore = exploreState(curr, explored);
          nodesInFrontier = frontier.size(); // Update nodes in frontier count
          for (State e : toExplore) {
              if (isGoal(e)) {
                  System.out.println("explored nodes: " + explored.size());
                  return e;
              } else {
                  generatedStates++; // Increment generated states count
                  if (explored.contains(e.toString())) {
                      repeatedStates++; // Increment repeated states count
                  }
                  frontier.offer(e);
              }
          }
          explored.add(curr.toString());
          exploredStates++; // Increment explored states count
          nodectr++;
      }
  }
    System.out.println("Search done");

    if(curr != null)
      return curr;
    else{
      return s;
    }
  }

  // checks if a state 's' is present in the explored state list
  public boolean isExplored(State s, HashSet<State> exploredList){
    for(State explored : exploredList){
      if(s.equals(explored)){
        return true;
      }
    }
    return false;
  }

  // Checks if a state 's' is the goal staet by comparing box positions
  public boolean isGoal(State s){
    s.sortBoxes();
    g.sortBoxes();
    for(int i = 0; i < s.getBoxesX().size(); i++){
      if(s.getBoxesX().get(i) != g.getBoxesX().get(i) || s.getBoxesY().get(i) != g.getBoxesY().get(i)){
          return false;
      }
    }
    return true;
  }

  
  HashSet<String> findDeadlocks(){
    HashSet<String> deadSquares = new HashSet<>();
    for(int y = 1; y < height-1; y++){
      for(int x = 1; x < width-1; x++){    
        if(mapData[y][x] != '.' && mapData[y][x] != '#'){
          if(mapData[y-1][x] == '#' && mapData[y][x-1] == '#'){ 
            // found an upper-left corner of a potential dead zone
            String corner = y + " " + x;
            deadSquares.add(corner);
            HashSet<String> dTempUL = new HashSet<>();
            for(int i = x + 1; i < width; i++){
              if(mapData[y][i] == '#'){
                deadSquares.addAll(dTempUL);  
                i = width;
                dTempUL.clear();
              }
              else if(mapData[y][i] == '.' || mapData[y-1][i] != '#'){
                dTempUL.clear();
                i = width;
              }
              else{
                String point = y + " " + i;
                dTempUL.add(point);
              }
            }
            for(int i = y + 1; i < height; i++){
              if(mapData[i][x] == '#'){
                deadSquares.addAll(dTempUL);
                i = height;
                dTempUL.clear();
              }
              else if(mapData[i][x] == '.' || mapData[i][x-1] != '#'){
                dTempUL.clear();
                i = height;
              }
              else{
                String point = i + " " + x;
                dTempUL.add(point);
              } 
            }
          }  
          //Repeat similar checks for other corners of potential dead zones
          if(mapData[y-1][x] == '#' && mapData[y][x+1] == '#'){
            String corner = y + " " + x;
            deadSquares.add(corner);
            HashSet<String> dTempUR = new HashSet<>();
            for(int i = x - 1; i >= 0; i--){
              if(mapData[y][i] == '#'){
                deadSquares.addAll(dTempUR);
                i = -1;
                dTempUR.clear();
              }
              else if(mapData[y][i] == '.' || mapData[y-1][i] != '#'){
                dTempUR.clear();
                i = -1;
              }
              else{
                String point = y + " " + i;
                dTempUR.add(point);
              }
            }
            for(int i = y + 1; i < height; i++){
              if(mapData[i][x] == '#'){
                deadSquares.addAll(dTempUR);
                i = height;
                dTempUR.clear();
              }
              else if(mapData[i][x] == '.' || mapData[i][x+1] != '#'){
                dTempUR.clear();
                i = height;
              }
              else{
                String point = i + " " + x;
                dTempUR.add(point);
              } 
            }
          }  
          if(mapData[y+1][x] == '#' && mapData[y][x-1] == '#'){
            String corner = y + " " + x;
            deadSquares.add(corner);
            HashSet<String> dTempLL = new HashSet<>();
            for(int i = x + 1; i < width; i++){
              if(mapData[y][i] == '#'){
                deadSquares.addAll(dTempLL);
                i = width;
                dTempLL.clear();
              }
              else if(mapData[y][i] == '.' || mapData[y+1][i] != '#'){
                dTempLL.clear();
                i = width;
              }
              else{
                String point = y + " " + i;
                dTempLL.add(point);
              }
            }
            for(int i = y - 1; i >= 0; i--){
              if(mapData[i][x] == '#'){
                deadSquares.addAll(dTempLL);
                i = -1;
                dTempLL.clear();
              }
              else if(mapData[i][x] == '.' || mapData[i][x-1] != '#'){
                dTempLL.clear();
                i = -1;
              }
              else{
                String point = i + " " + x;
                dTempLL.add(point);
              } 
            }
          }  
          if(mapData[y+1][x] == '#' && mapData[y][x+1] == '#'){
            String corner = y + " " + x;
            deadSquares.add(corner);
            HashSet<String> dTempLR = new HashSet<>();
            for(int i = x - 1; i >= 0; i--){
               if(mapData[y][i] == '#'){
                deadSquares.addAll(dTempLR);
                i = -1;
                dTempLR.clear();
              }
              else if(mapData[y][i] == '.' || mapData[y+1][i] != '#'){
                dTempLR.clear();
                i = -1;
              }
              else{
                String point = y + " " + i;
                dTempLR.add(point);
              }
            }
            for(int i = y - 1; i >= 0; i--){
              if(mapData[i][x] == '#'){
                deadSquares.addAll(dTempLR);
                i = -1;
                dTempLR.clear();
              }
              else if(mapData[i][x] == '.' || mapData[i][x+1] != '#'){
                dTempLR.clear();
                i = -1; 
              }
              else{
                String point = i + " " + x;
                dTempLR.add(point);
              } 
            }  
      }
    }
    }
  }
  return deadSquares;
  }

 public int computeHeuristic(State s){
    ArrayList<Integer> sbx = s.getBoxesX();
    ArrayList<Integer> sby = s.getBoxesY();
    ArrayList<Integer> gbx = g.getBoxesX();
    ArrayList<Integer> gby = g.getBoxesY();
    int sum = 0;
    for(int i = 0; i < sbx.size(); i++){
        int md = Integer.MAX_VALUE;
        for(int j = 0; j < gbx.size(); j++){
            int dist = Math.abs(sbx.get(i) - gbx.get(j)) + Math.abs(sby.get(i) - gby.get(j));
            md = Math.min(md, dist);
        }
        sum += md;
    }
    return sum;
}



  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    this.width = width;
    this.height = height;
    this.mapData = mapData;
    this.itemsData = itemsData;

    State s = createStart();
    s.sortBoxes();

    System.out.println(s.toString());

   
    this.g = createGoals();
    this.g.sortBoxes();
    System.out.println(g.toString());
    
    this.deadlock = findDeadlocks();

    System.out.println(computeHeuristic(s));
    s.setHeuristic(computeHeuristic(s));

    try {
      State state = gbfs(s);
      String soln = state.getPath().toString();
      numMoves = soln.length(); // Calculate the number of moves
      System.out.println("Solution: " + soln);
      printStatistics(); // Print the statistics
      return soln;
  } catch (Exception ex) {
      ex.printStackTrace();
      return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }
}

// Print the statistics
public void printStatistics() {
  System.out.println("Generated States: " + generatedStates);
  System.out.println("Repeated States: " + repeatedStates);
  //System.out.println("Deadlock States Found: " + deadlockStatesFound);
  System.out.println("Nodes in Frontier upon Termination: " + nodesInFrontier);
  //System.out.println("Explored States: " + exploredStates);
  System.out.println("Number of Moves: " + numMoves);
}

}
