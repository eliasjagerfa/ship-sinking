import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class Battlefield {
    final int width;
    final int height;
    private final String[][] coordinateSystem;
    private int shipHitFields;
    private int occupiedFields;
    private final ArrayList<int[]>[] calculatedShipsPositions;

    private final static String[] textBlocks = {"+", "---", "|"};

    public Battlefield(int width, int height, int shipsToPlace) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new String[height][width];
        for(String[] row : coordinateSystem){
            Arrays.fill(row, "free");
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
            
            int shipId = parseShipId(shotField);
            if(shipId >= 0) {
                String newFieldValue = "shipHit_" + shotField;
                
                coordinateSystem[x][y] = newFieldValue;
                shipHitFields++;

                // DIRTY: Find the hit ship and if its sunken
                ArrayList<int[]> shipPositions = calculatedShipsPositions[shipId];

                boolean isShipSunken = true; 

                for(int[] coordinates : shipPositions) {
                    String fieldValue = coordinateSystem[coordinates[0]][coordinates[1]];

                    if (parseShipId(fieldValue) >= 0) {
                        isShipSunken = false;
                        break;
                    }
                }

                return newFieldValue;
            } else if(shotField.equals("free")) {
                coordinateSystem[x][y] = "emptyHit";

                return "emptyHit";
            }
        }
        catch(IndexOutOfBoundsException err){
            System.out.println("Invalid Coordinates. Try again!");
            return "outOfBounds";
        }
        System.out.println("You already shot at these coordinates");
        return "alreadyShotAt";
    }   
    
    public boolean allAreSunken() {
        return (shipHitFields == occupiedFields);
    }

    private int parseShipId(String coordinateSystemValue) {
        try {
            return Integer.parseInt(coordinateSystemValue);
        } catch (NumberFormatException err) {
            return -1;
        }
    }

    public String convertBattlefieldToText() {
        Function<String, String> mapFieldToText = (field) -> {
            if ("emptyHit".equals(field)) return "x";
                
            if (field.startsWith("shipHit_")) {
                return field.replaceAll("[^0-9]", "");
            }

            return " ";
        };
    
        String[] mappedBattlefieldRows = new String[height];

        for (int i = 0; i < height; i++) {
            int rowIndex = height - i - 1;

            StringBuilder rowSb = new StringBuilder();

            rowSb.append(textBlocks[2]);

            for (int colIndex = 0; colIndex < width; colIndex++) {
                String convertedField = mapFieldToText.apply(coordinateSystem[colIndex][rowIndex]);
                rowSb.append(String.format(" %s %s", convertedField, textBlocks[2]));
            }

            mappedBattlefieldRows[i] = rowSb.toString();
        }

        String lineSeparator =
            textBlocks[0] +
            (textBlocks[1] + textBlocks[0]).repeat(width);

        StringBuilder sb = new StringBuilder();

        sb.append(lineSeparator).append("\n");

        for (String mappedRow: mappedBattlefieldRows) {
            sb.append(mappedRow).append("\n");
            sb.append(lineSeparator).append("\n");
        }

        return sb.toString();
    }
}
