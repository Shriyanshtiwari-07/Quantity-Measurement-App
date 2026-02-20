package quantitymeasurementapp;

public class FeetEquality {

    private final double value;

    public FeetEquality(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        FeetEquality other = (FeetEquality) obj;

        return Double.compare(this.value, other.value) == 0;
    }
}