package com.zzh.dreamchaser.debugBT.tool;

import java.util.ArrayList;
import java.util.List;

public class Interpolation {
    //数据点
    public static class Point {
        double x, y;

        public Point(double a, double b) {
            x = a;
            y = b;
        }
    }

    //牛顿基本差商
    public static class NewtonItp {
        private static final int maxsize = 100;

        public static class NewtonCurve {
            //完整的差商表
            Double[][] quot_table = new Double[maxsize][maxsize];
            //单独保存第一斜行的参数
            Double[] quot_coe = new Double[maxsize];
            double minX, maxX;
        }

        private NewtonCurve curve = new NewtonCurve();
        private List<Point> curvePoints;
        private int pointNum;

        public NewtonItp(List<Point> curvePoints, int pointNum) {
            this.curvePoints = curvePoints;
            this.pointNum = pointNum;
        }

        public NewtonCurve getCurve() {
            return this.curve;
        }

        public void calPolynomial() {
            curve.maxX = curvePoints.get(0).x;
            curve.minX = curvePoints.get(0).x;

            for (int i = 0; i < pointNum; i++) {
                //获取x的最大最小值
                if (curvePoints.get(i).x < curve.minX)
                    curve.minX = curvePoints.get(i).x;
                if (curvePoints.get(i).x > curve.maxX)
                    curve.maxX = curvePoints.get(i).x;
                //计算差商表，每轮n-k-1次
                for (int j = 0; j < pointNum - i; j++)
                    if (i == 0)
                        //初始先把y值存入差商表
                        curve.quot_table[0][j] = curvePoints.get(j).y;
                    else
                        //计算各阶差商，由上一阶差商计算
                        curve.quot_table[i][j] = (curve.quot_table[i - 1][j + 1] - curve.quot_table[i - 1][j]) / (curvePoints.get(i + j).x - curvePoints.get(j).x);
                //取出顶部单独保存作为参数
                curve.quot_coe[i] = curve.quot_table[i][0];
            }
        }

        public double getInterpolation(double x) {
            double y = curve.quot_coe[0];
            for (int i = 1; i < pointNum; i++) {
                double temp = 1;
                //根据x计算(x-x0)(x-x1)...(x-xi-1)
                for (int j = 0; j < i; j++)
                    temp *= (x - curvePoints.get(j).x);
                //系数与差商表值相乘
                y += curve.quot_coe[i] * temp;
            }
            return y;
        }
    }

    //拉格朗日插值
    public static class LagRangeItp {
        public static class LagrangeCurve {
            ArrayList<Double> coefficient = new ArrayList<>();
            double minX, maxX;
        }

        private LagrangeCurve curve = new LagrangeCurve();
        private List<Point> curvePoints;
        private int pointNum;

        public LagRangeItp(List<Point> curvePoints, int pointNum) {
            this.curvePoints = curvePoints;
            this.pointNum = pointNum;
        }

        public LagrangeCurve getCurve() {
            return this.curve;
        }

        public void calPolynomial() {
            curve.maxX = curvePoints.get(0).x;
            curve.minX = curvePoints.get(0).x;
            //0到n-1共n项
            for (int i = 0; i < pointNum; i++) {
                curve.coefficient.add(curvePoints.get(i).y);
                //获取x的最大最小值
                if (curvePoints.get(i).x < curve.minX)
                    curve.minX = curvePoints.get(i).x;
                if (curvePoints.get(i).x > curve.maxX)
                    curve.maxX = curvePoints.get(i).x;

                for (int j = 0; j < pointNum; j++)
                    if (j != i)
                        //计算f(xi)/((xi-x0)*...(xi-xi-1)(xi-xi+1)...(xi-xn-1))
                        curve.coefficient.set(i, curve.coefficient.get(i) / (curvePoints.get(i).x - curvePoints.get(j).x));
            }
        }

        public double getInterpolation(double x) {
            double y = 0;
            //0到n-1共n项
            for (int i = 0; i < curvePoints.size(); i++) {
                //获取计算好的系数f(xi)/((xi-x0)*...(xi-xi-1)(xi-xi+1)...(xi-xn-1))
                double temp = curve.coefficient.get(i);
                for (int j = 0; j < curvePoints.size(); j++)
                    if (j != i)
                        //根据x计算(x-x0)*...(x-xi-1)(x-xi+1)...(x-xn-1)乘到结果中
                        temp *= (x - curvePoints.get(j).x);
                y += temp;
            }
            return y;
        }
    }
}

