public class Battlefield {
    final int width;
    final int height;
    final byte[][] coordinateSystem; //0 is free, 1 is already shot at, 2 is occupied, 3 is hit
    private byte occupiedFields;
    private byte hitFields;

    public Battlefield(int width, int height) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new byte[height][width];
    }

    public boolean setShip(Ship ship) {
        boolean isInBounds = ship.x <= width || ship.y <= height;
        
        if(isInBounds) {
            for(byte position : ship.getPositions()) {
                if(ship.isHorizontal) {
                    coordinateSystem[position][ship.y] = 2;
                }
                else {
                    coordinateSystem[ship.x][position] = 2;
                }
            }
        } 
        return isInBounds;
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
