public class Battlefield {
    private byte width;
    private byte height;
    private byte[][] coordinateSystem; //0 is free, 1 is already shot at, 2 is occupied, 3 is hit
    private byte occupiedFields;
    private byte hitFields;

    public Battlefield(byte width, byte height) {
        this.width = width;
        this.height = height;
        this.coordinateSystem = new byte[height][width];
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
