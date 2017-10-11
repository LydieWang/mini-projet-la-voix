package com.polytech.di.tianxue.voix_analyse;

/**
 * Created by Administrator on 07/10/2017.
 */

public class ComplexNumber {
    public double real, imaginary;

    public ComplexNumber(double real,double imaginary){
        this.real = real;
        this.imaginary = imaginary;
    }

    public ComplexNumber(){
        this(0,0);
    }

    public ComplexNumber(ComplexNumber c){
        this(c.real,c.imaginary);
    }

    @Override
    public String toString() {
        return "(" + this.real + "+" + this.imaginary + "i)";
    }

    //加法
    public final ComplexNumber add(ComplexNumber c){
        return new ComplexNumber(this.real + c.real, this.imaginary + c.imaginary);
    }

    //减法
    public final ComplexNumber minus(ComplexNumber c){
        return new ComplexNumber(this.real - c.real, this.imaginary - c.imaginary);
    }

    //求模值
    public final double getMod(){
        return Math.sqrt(this.real * this.real + this.imaginary * this.imaginary);
    }

    //乘法
    public final ComplexNumber multiply(ComplexNumber c){
        return new ComplexNumber(
                this.real * c.real - this.imaginary * c.imaginary,
                this.real * c.imaginary + this.imaginary * c.real);
    }
}
