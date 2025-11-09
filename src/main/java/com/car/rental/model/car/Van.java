package com.car.rental.model.car;

import com.car.rental.model.car.enums.CarType;
import com.car.rental.model.car.enums.FuelType;
import com.car.rental.model.car.enums.TransmissionType;

public class Van extends Car {

    private static final double PRICE_PER_DAY = 110.00;

    public Van(FuelType fuelType, TransmissionType transmissionType, int mileage, int horsePower, String licensePlate) {
        super(fuelType, transmissionType, CarType.VAN, mileage, horsePower, licensePlate);
    }

    @Override
    public double getBasePricePerDay() {
        return PRICE_PER_DAY;
    }

}
