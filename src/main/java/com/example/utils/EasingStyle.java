package com.example.utils;

import javafx.animation.Interpolator;

public class EasingStyle {

    public final static MyInterpolator InSine = new MyInterpolator((double x) -> 1 - Math.cos((x * Math.PI) / 2));
    public final static MyInterpolator OutSine = new MyInterpolator((double x) -> Math.sin((x * Math.PI) / 2));
    public final static MyInterpolator InOutSine = new MyInterpolator((double x) -> -(Math.cos(Math.PI * x) - 1) / 2);
    public final static MyInterpolator InQuad = new MyInterpolator((double x) -> x * x);
    public final static MyInterpolator OutQuad = new MyInterpolator((double x) -> 1 - (1 - x) * (1 - x));
    public final static MyInterpolator InOutQuad = new MyInterpolator((double x) -> x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2);
    public final static MyInterpolator InCubic = new MyInterpolator((double x) -> x * x * x);
    public final static MyInterpolator OutCubic = new MyInterpolator((double x) -> 1 - Math.pow(1 - x, 3));
    public final static MyInterpolator InOutCubic = new MyInterpolator((double x) -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2);
    public final static MyInterpolator InQuart = new MyInterpolator((double x) -> x * x * x * x);
    public final static MyInterpolator OutQuart = new MyInterpolator((double x) -> 1 - Math.pow(1 - x, 4));
    public final static MyInterpolator InOutQuart = new MyInterpolator((double x) -> x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2);
    public final static MyInterpolator InQuint = new MyInterpolator((double x) -> x * x * x * x * x);
    public final static MyInterpolator OutQuint = new MyInterpolator((double x) -> 1 - Math.pow(1 - x, 5));
    public final static MyInterpolator InOutQuint = new MyInterpolator((double x) -> x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2);
    public final static MyInterpolator InExpo = new MyInterpolator((double x) -> x == 0 ? 0 : Math.pow(2, 10 * x - 10));
    public final static MyInterpolator OutExpo = new MyInterpolator((double x) -> x == 1 ? 1 : 1 - Math.pow(2, -10 * x));
    public final static MyInterpolator InOutExpo = new MyInterpolator((double x) -> x == 0
            ? 0: x == 1? 1: x < 0.5 ? Math.pow(2, 20 * x - 10) / 2
                            : (2 - Math.pow(2, -20 * x + 10)) / 2);

    public final static MyInterpolator InCirc = new MyInterpolator((double x) -> 1 - Math.sqrt(1 - Math.pow(x, 2)));
    public final static MyInterpolator OutCirc = new MyInterpolator((double x) -> Math.sqrt(1 - Math.pow(x - 1, 2)));
    public final static MyInterpolator InOutCirc = new MyInterpolator((double x) -> x < 0.5
            ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
            : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2);

    public final static MyInterpolator InBack = new MyInterpolator((double x) -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        
        return c3 * x * x * x - c1 * x * x;
    });

    public final static MyInterpolator OutBack = new MyInterpolator((double x) -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        
        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
    });

    public final static MyInterpolator InOutBack = new MyInterpolator((double x) -> {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;
        
        return x < 0.5? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    });

    public final static MyInterpolator InElastic = new MyInterpolator((double x) -> {
        double c4 = (2 * Math.PI) / 3;
        
        return x == 0? 0: x == 1? 1
                : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4);
    });

    public final static MyInterpolator OutElastic = new MyInterpolator((double x) -> {
        double c4 = (2 * Math.PI) / 3;
        
        return x == 0? 0: x == 1? 1: Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1;
    });

    public final static MyInterpolator InOutElastic = new MyInterpolator((double x) -> {
        double c5 = (2 * Math.PI) / 4.5;
        
        return x == 0? 0: x == 1? 1: x < 0.5? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2
                : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1;
    });

    public final static MyInterpolator InBounce = new MyInterpolator((double x) -> 1 - easeOutBounce(1 - x));
    public final static MyInterpolator OutBounce = new MyInterpolator((double x) -> easeOutBounce(x));
    public final static MyInterpolator InOutBounce = new MyInterpolator((double x) -> x < 0.5
            ? (1 - easeOutBounce(1 - 2 * x)) / 2: (1 + easeOutBounce(2 * x - 1)) / 2);

    private static double easeOutBounce(double x) {
        double n1 = 7.5625;
        double d1 = 2.75;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5 / d1) * x + 0.75;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25 / d1) * x + 0.9375;
        } else {
            return n1 * (x -= 2.625 / d1) * x + 0.984375;
        }
    }
    public static class MyInterpolator extends Interpolator {

        private Style style;

        public MyInterpolator() {
            super();
        }

        public MyInterpolator(Style style) {
            super();
            this.style = style;
        }

        @Override
        protected double curve(double x) {
            return style != null ? style.calculate(x) : x;
        }
    }
    private static interface Style {

        public double calculate(double x);
    }
}
