package com.example.rpc.server;

public class Car implements IVehical{

    @Override
    public void run() {
    System.out.println("Car is running");
    }

}