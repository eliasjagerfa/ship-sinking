public class Battlefield {
    final int width;
    final int height;
    private final int[][] coordinateSystem; //0 is free, 1 is already shot at, 2 is occupied, 3 is hit //PRÜFEN: Was ist mit Overflow? 
    private int occupiedFields;
    private int hitFields;

    public Battlefield(int width, int height) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new int[height][width];
    }


    public boolean setShip(Ship ship) {
        for(int i = 0; i < 2; i++) {
            for(int position : ship.getPositions()) {
                if(i == 0) {
                    boolean isInBounds = ship.isHorizontal 
                        ? position <= width && position >= 0
                        : position <= height && position >= 0;

                    boolean isOverlapping = ship.isHorizontal 
                        ? coordinateSystem[position][ship.y] == 2 
                        : coordinateSystem[ship.x][position] == 2;

                    if(!isInBounds || isOverlapping){
                        return false; 
                    }
                } else {
                    if(ship.isHorizontal) {
                        coordinateSystem[position][ship.y] = 2;
                        occupiedFields++;
                    } else {
                        coordinateSystem[ship.x][position] = 2;
                        occupiedFields++;
                    }
                }
            }
        }
        return true;
    }
    public boolean hitField(int x, int y) {
        try{
            int shotField = coordinateSystem[x][y];
            if(shotField == 0 || shotField == 2) {
                coordinateSystem[x][y]++;
                hitFields += shotField == 2 ? 1 : 0;
                return true;
            }
        }
        catch(IndexOutOfBoundsException err){}

        return false;
    }   
    
    public boolean allAreSunken() {
        return (hitFields == occupiedFields);
    } 
}
