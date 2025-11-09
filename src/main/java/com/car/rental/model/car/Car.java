package com.car.rental.model.car;

import com.car.rental.model.car.enums.CarType;
import com.car.rental.model.car.enums.FuelType;
import com.car.rental.model.car.enums.TransmissionType;

import java.util.Objects;

public abstract class Car {

    private FuelType fuelType;

    private TransmissionType transmissionType;

    private CarType carType;

    private int mileage;

    private int horsePower;

    private String licensePlate;

    public Car(FuelType fuelType, TransmissionType transmissionType, CarType carType, int mileage, int horsePower, String licensePlate) {
        this.fuelType = fuelType;
        this.transmissionType = transmissionType;
        this.carType = carType;
        this.mileage = mileage;
        this.horsePower = horsePower;
        this.licensePlate = licensePlate;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public abstract double getBasePricePerDay();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return mileage == car.mileage && horsePower == car.horsePower && fuelType == car.fuelType && transmissionType == car.transmissionType && carType == car.carType && Objects.equals(licensePlate, car.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fuelType, transmissionType, carType, mileage, horsePower, licensePlate);
    }

    @Override
    public String toString() {
        return "Car{" +
                "fuelType=" + fuelType +
                ", transmissionType=" + transmissionType +
                ", carType=" + carType +
                ", mileage=" + mileage +
                ", horsePower=" + horsePower +
                ", licensePlate='" + licensePlate + '\'' +
                '}';
    }

}
