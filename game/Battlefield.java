package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Battlefield {
    private final String[][] coordinateSystem;
    private int toHit;
    private final List<List<GameTypes.shipCoordinate>> shipsPositions;
    
    public Battlefield(int size, Map<Integer, Integer> shipsToPlace) {
        this.coordinateSystem = new String[size][size];
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

    public GameTypes.ShipPositionValidationResult addShip(Ship ship, int placedShipCount, Integer freeShipId) {
        GameTypes.ShipPositionValidationResult validationResult = validateShipPositions(ship);
        if (validationResult.isOutOfBounds() || validationResult.isOverlapping()) return validationResult;
        GameTypes.shipCoordinate posi;
        int[] shipPositions = ship.getPositions();

        for(int position : shipPositions) {
            if(ship.isHorizontal) {
                coordinateSystem[position][ship.y] = String.format("%d", freeShipId);
                posi = new GameTypes.shipCoordinate(position, ship.y);
                shipsPositions.get(placedShipCount).add(posi);
            } else {
                coordinateSystem[ship.x][position] = String.format("%d", freeShipId);
                posi = new GameTypes.shipCoordinate(ship.x, position);
                shipsPositions.get(placedShipCount).add(posi);
            }
        }
        
        return new GameTypes.ShipPositionValidationResult(false, false);
    }

    public GameTypes.RemovalResult removeShip(int x, int y) {
        boolean isOutOfBounds = x <= 0 || x >= coordinateSystem.length || y <= 0 || y >= coordinateSystem.length;
        if (isOutOfBounds) return new GameTypes.RemovalResult(isOutOfBounds, false, null, 0);

        String shipId = coordinateSystem[x - 1][y - 1];

        boolean isEmptyField = parseShipId(shipId) < 0;
        if (isEmptyField) return new GameTypes.RemovalResult(false, isEmptyField, null, 0);

        int shipLength = 0;

        for (String[] collumn : coordinateSystem) {
            List<String> foundShipIds = 
                Arrays.stream(collumn)
                    .filter(fieldValue -> fieldValue.equals(shipId))
                    .toList();
                    
            shipLength += foundShipIds.size();

            for (int rowIndex = 0; rowIndex < coordinateSystem.length; rowIndex++) {
                if (collumn[rowIndex].equals(shipId)) {
                    collumn[rowIndex] = "";
                }
            }
        }
        
        
        shipsPositions.remove(parseShipId(shipId));

        return new GameTypes.RemovalResult(false, false, parseShipId(shipId), shipLength);
    }

    public GameTypes.ShipPositionValidationResult validateShipPositions(Ship ship) {
        for(int position : ship.getPositions()) {
            boolean isOutOfBounds = position >= coordinateSystem.length || position >= coordinateSystem.length || position < 0;

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
        boolean isOutOfBounds = x <= 0 || x > coordinateSystem.length || y <= 0 || y > coordinateSystem.length;
        if (isOutOfBounds) return new GameTypes.HitShipResult(isOutOfBounds, false, "", "", false, false);

        String shotField = coordinateSystem[x - 1][y - 1];

        boolean isAlreadyShotAt = shotField.contains("Hit");
        if (isAlreadyShotAt) return new GameTypes.HitShipResult(isOutOfBounds, isAlreadyShotAt, "", "", false, false);
        
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

            return new GameTypes.HitShipResult(false, false, shotField, newFieldValue, true, isShipSunken);
        } else if(shotField.equals("")) {
            coordinateSystem[x - 1][y - 1] = "emptyHit";

            return new GameTypes.HitShipResult(false, false, "", "emptyHit", false, false);
        }

        return new GameTypes.HitShipResult(false, false, shotField, shotField, false, false);
    }   
    
    public boolean allAreSunken() {
        return toHit == 0;
    }

    int parseShipId(String fieldValue) {
        try {
            return Integer.parseInt(fieldValue);
        } catch (NumberFormatException err) {
            return -1;
        }
    }

    public int getWidth() {
        return coordinateSystem.length;
    }

    public String[][] getCoordinateSystem() {
        return coordinateSystem;
    }
}
