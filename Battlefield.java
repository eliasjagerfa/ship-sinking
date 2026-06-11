public class Battlefield {
    final int width;
    final int height;
    final byte[][] coordinateSystem; //0 is free, 1 is already shot at, 2 is occupied, 3 is hit //PRÜFEN: int, damit das Spielfeld größer werden kann? Was ist mit Overflow? 
    private byte occupiedFields;
    private byte hitFields;

    public Battlefield(int width, int height) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new byte[height][width];
    }

    public boolean setShip(Ship ship) {
        for(byte i = 0; i < 2; i++) {
            for(byte position : ship.getPositions()) {
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
                } 
                else {
                    if(ship.isHorizontal) {
                        coordinateSystem[position][ship.y] = 2;
                    }
                    else {
                        coordinateSystem[ship.x][position] = 2;
                    }
                }
            }
        }
        return true;
    }
    public boolean hitField(byte x, byte y) {
        try{
            byte shotField = coordinateSystem[x][y];
            if(shotField == 0 || shotField == 2) {
                coordinateSystem[x][y]++;
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
