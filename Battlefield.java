import java.util.ArrayList;
import java.util.Arrays;

public class Battlefield {
    final int width;
    final int height;
    private final String[][] coordinateSystem;
    private int ShipHitFields;
    private int occupiedFields;
    private final ArrayList<int[]>[] calculatedShipsPositions;

    public Battlefield(int width, int height, int shipsToPlace) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new String[height][width];
        for(String[] row : coordinateSystem){
            Arrays.fill(row, "isFree");
        }

        this.calculatedShipsPositions = new ArrayList[shipsToPlace];
        for (int i = 0; i < calculatedShipsPositions.length; i++) {
            calculatedShipsPositions[i] = new ArrayList<>();
        }
    }

    public void setShip(Ship ship, int shipId) {
        ArrayList<int[]> csps = new ArrayList(ship.length);
        for(int position : ship.getPositions()) {
            if(ship.isHorizontal) {
                coordinateSystem[position][ship.y] = String.format("%d", shipId);
                occupiedFields++;
                csps.add(new int[] {ship.x, position});
            } else {
                coordinateSystem[ship.x][position] = String.format("%d", shipId);
                occupiedFields++;
                csps.add(new int[] {position, ship.y});
            }
        }
        calculatedShipsPositions[shipId] = csps;
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

    public String hitField(int x, int y) {
        try{
            String shotField = coordinateSystem[x][y];
            if(shotField.equals("isFree") || parseShipId(shotField) >= 0) {
                coordinateSystem[x][y] = shotField.equals("isFree") ? "emptyHit" : ("isShipHit_" + shotField);
                ShipHitFields += shotField.equals("isFree") ? 0 : 1;
                String hitType = coordinateSystem[x][y];
                return hitType;
            }
        }
        catch(IndexOutOfBoundsException err){
            System.out.println("Invalid Coordinates. Try again!");
            return "outOfBounds";
        }
        return "alreadyShotAt";
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
