import java.util.Arrays;

public class Battlefield {
    final int width;
    final int height;
    private final String[][] coordinateSystem; //0 is free, 1 is already shot at, 2 is occupied, 3 is hit //PRÜFEN: Was ist mit Overflow? 
    private int occupiedFields;
    private int ShipHitFields;

    public Battlefield(int width, int height) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new String[height][width];
        for(String[] row : coordinateSystem){
            Arrays.fill(row, "isFree");
        }
        
    }


    public boolean setShip(Ship ship) {
        for(int i = 0; i < 2; i++) {
            for(int position : ship.getPositions()) {
                if(i == 0) {
                    boolean isInBounds = ship.isHorizontal 
                        ? position <= width && position >= 0
                        : position <= height && position >= 0;

                    boolean isOverlapping = ship.isHorizontal 
                        ? coordinateSystem[position][ship.y].equals("isOccupied") 
                        : coordinateSystem[ship.x][position].equals("isOccupied");

                    if(!isInBounds || isOverlapping){
                        return false; 
                    }
                } else {
                    if(ship.isHorizontal) {
                        coordinateSystem[position][ship.y] = "isOccupied";
                        occupiedFields++;
                    } else {
                        coordinateSystem[ship.x][position] = "isOccupied";
                        occupiedFields++;
                    }
                }
            }
        }
        return true;
    }
    public boolean hitField(int x, int y) {
        try{
            String shotField = coordinateSystem[x][y];
            if(shotField.equals("isFree") || shotField.equals("isOccupied")) {
                coordinateSystem[x][y] = shotField.equals("isFree") ? "isEmptyHit" : "isShipHit";
                ShipHitFields += shotField.equals("isFree") ? 0 : 1;
                return true;
            }
        }
        catch(IndexOutOfBoundsException err){}

        return false;
    }   
    
    public boolean allAreSunken() {
        return (ShipHitFields == occupiedFields);
    } 
}
