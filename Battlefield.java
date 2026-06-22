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

    public void setShip(Ship ship, int shipNumber) {
        for(int position : ship.getPositions()) {
            if(ship.isHorizontal) {
                coordinateSystem[position][ship.y] = String.format("%d", shipNumber);
                occupiedFields++;
            } else {
                coordinateSystem[ship.x][position] = String.format("%d", shipNumber);
                occupiedFields++;
            }
        }
    }

    public boolean areShipPositionsValid(Ship ship) {
        for(int position : ship.getPositions()) {
            boolean isInBounds = ship.isHorizontal 
                ? position <= width && position >= 0
                : position <= height && position >= 0;

            boolean isOverlapping = ship.isHorizontal 
                ? parseShipId(coordinateSystem[position][ship.y]) >= 0
                : parseShipId(coordinateSystem[ship.x][position]) >= 0;

            if(!isInBounds || isOverlapping) {
                return false;
            }
        }
        return true;
    }

    public boolean hitField(int x, int y) {
        try{
            String shotField = coordinateSystem[x][y];
            if(shotField.equals("isFree") || parseShipId(shotField) >= 0) {
                coordinateSystem[x][y] = shotField.equals("isFree") ? "isEmptyHit" : ("isShipHit_" + shotField);
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

    private int parseShipId(String coordinateSystemValue) {
        try {
            return Integer.parseInt(coordinateSystemValue);
        } catch (NumberFormatException err) {
            return -1;
        }
    }
}
