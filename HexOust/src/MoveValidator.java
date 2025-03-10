public class MoveValidator { // Checks if the selected hexagon is already occupied
  
    public boolean isValidMove(double q, double r, String[][] boardState) {
      
        return boardState[(int) q + 6][(int) r + 6] == null; // Move is valid if the hexagon is not occupied and prevents overwriting
    
    }
  
}
