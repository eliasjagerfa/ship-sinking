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

    public GameTypes.ShipPositionValidationResult addShip(Ship ship, int placedShipCount) {
        GameTypes.ShipPositionValidationResult validationResult = validateShipPositions(ship);
        if (validationResult.isOutOfBounds() || validationResult.isOverlapping()) return validationResult;
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
        
        return new GameTypes.ShipPositionValidationResult(false, false);
    }

    //TODO: finish ts
    public void removeShip(int x, int y) {
        String shipId = coordinateSystem[x][y];
        int shipLength = 0;

        for (int rowIndex = 0; rowIndex < coordinateSystem.length; rowIndex++) {
            List<String> foundShipIds = 
                Arrays.stream(coordinateSystem[rowIndex])
                    .filter(fieldValue -> fieldValue.equals(shipId))
                    .toList();
                    
            shipLength += foundShipIds.size();
        }
        
        
        var posi = new GameTypes.shipCoordinate(x, y);
        //shipsPositions.get(shipId - 1).remove(posi);
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

    //TODO: implement initial checks and validate if it was already shot at
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
