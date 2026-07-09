import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Battlefield {
    private final int width;
    private final int height;
    private final String[][] coordinateSystem;
    private int toHit;
    private final List<List<GameTypes.shipCoordinate>> shipsPositions;

    private final static String[] textBlocks = {"+", "---", "|"};
    
    public Battlefield(int width, int height, Map<Integer, Integer> shipsToPlace) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new String[width][height];
        for(String[] row : coordinateSystem){
            Arrays.fill(row, "");
        }

        this.shipsPositions = new ArrayList<>();
        shipsToPlace.forEach((length, amount) -> {
            toHit += length * amount;
            for (int i = 0; i < amount; i++) {
                shipsPositions.add(new ArrayList<>());
            }
        });
    }

    public boolean addShip(Ship ship, int placedShipCount) {
        GameTypes.shipCoordinate posi;
        int[] shipPositions = ship.getPositions();

        for(int position : shipPositions) {
            if(ship.isHorizontal) {
                coordinateSystem[position][ship.y] = String.format("%d", placedShipCount + 1);
                posi = new GameTypes.shipCoordinate(position, ship.y);
                shipsPositions.get(placedShipCount).add(posi);
            } else {
                coordinateSystem[ship.x][position] = String.format("%d", placedShipCount + 1);
                posi = new GameTypes.shipCoordinate(ship.x, position);
                shipsPositions.get(placedShipCount).add(posi);
            }
        }
        
        return true;
    }

    public GameTypes.ShipPositionValidationResult validateShipPositions(Ship ship) {
        for(int position : ship.getPositions()) {
            boolean isOutOfBounds = position >= width || position >= height || position < 0;

            if(isOutOfBounds) {
                return new GameTypes.ShipPositionValidationResult(isOutOfBounds, false);
            }

            boolean isOverlapping = ship.isHorizontal 
                ? parseShipId(coordinateSystem[position][ship.y]) >= 0
                : parseShipId(coordinateSystem[ship.x][position]) >= 0;

            if(isOverlapping) {
                return new GameTypes.ShipPositionValidationResult(false, isOverlapping);
            }
        }
        return new GameTypes.ShipPositionValidationResult(false, false);
    }

    public GameTypes.HitShipResult hitField(int x, int y) {
        String shotField;
        try {
            shotField = coordinateSystem[x - 1][y - 1];
        } catch (Exception e) {
            throw new IllegalArgumentException("Out of bounds: " + x + ", " + y);
        }
        
        int shipId = parseShipId(shotField);
        if(shipId >= 0) {
            String newFieldValue = "shipHit_" + shotField;
            
            coordinateSystem[x - 1][y - 1] = newFieldValue;
            toHit--;

            List<GameTypes.shipCoordinate> shipPositions = shipsPositions.get(shipId - 1);

            boolean isShipSunken = true; 

            for(GameTypes.shipCoordinate coordinates : shipPositions) {
                String fieldValue = coordinateSystem[coordinates.x()][coordinates.y()];

                if (parseShipId(fieldValue) >= 0) {
                    isShipSunken = false;
                    break;
                }
            }

            return new GameTypes.HitShipResult(shotField, newFieldValue, true, isShipSunken);
        } else if(shotField.equals("")) {
            coordinateSystem[x][y] = "emptyHit";

            return new GameTypes.HitShipResult("", "emptyHit", false, false);
        }

        return new GameTypes.HitShipResult(shotField, shotField, false, false);
    }   
    
    public boolean allAreSunken() {
        return toHit == 0;
    }

    private int parseShipId(String coordinateSystemValue) {
        try {
            return Integer.parseInt(coordinateSystemValue);
        } catch (NumberFormatException err) {
            return -1;
        }
    }

    //TODO: überlegen, ob vom eigenen Feld auch in der Hitphase noch die Schiffe gesehen werden können sollen oder nicht
    public String convertBattlefieldToText(boolean showHiddenShips) {
        Function<String, String> mapFieldToText = (field) -> {
            if ("emptyHit".equals(field)) return "x";
            
            if (showHiddenShips && parseShipId(field) >= 0) {
                return parseShipId(field)< 10 ? field + " " : field;
            }

            if (field.startsWith("shipHit_")) {
                return field.replaceAll("[^0-9]", "");
            }

            return "  ";
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
            (textBlocks[1] + " " + textBlocks[0]).repeat(width);

        StringBuilder sb = new StringBuilder();

        sb.append(lineSeparator).append("\n");

        for (String mappedRow: mappedBattlefieldRows) {
            sb.append(mappedRow).append("\n");
            sb.append(lineSeparator).append("\n");
        }

        return sb.toString();
    }

    public String convertBattlefieldToText() {
        return convertBattlefieldToText(false);
    }

    public int getWidth() {
        return width;
    }
}
