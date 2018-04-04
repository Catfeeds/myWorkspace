/**
 *
 */
package me.suncloud.marrymemo.model;

import android.graphics.Matrix;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * @author iDay
 */
public class TransInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6787312850233338397L;
    private double a = 1;
    private double b = 0;
    private double c = 0;
    private double d = 1;
    private double tx = 0;
    private double ty = 0;

    public TransInfo(JSONObject json) {
        if (json != null) {
            this.a = json.optDouble("a", 1);
            this.b = json.optDouble("b", 0);
            this.c = json.optDouble("c", 0);
            this.d = json.optDouble("d", 1);
            this.tx = json.optDouble("tx", 0);
            this.ty = json.optDouble("ty", 0);
        }
    }

    public TransInfo(String transInfo) {
        if (!JSONUtil.isEmpty(transInfo)) {
            Pattern pattern = Pattern.compile("[^,]+");
            Matcher matcher = pattern.matcher(transInfo);
            try {
                int i = 0;
                while (matcher.find()) {
                    String string = matcher.group(0);
                    double point = Double.valueOf(string);
                    if (point != Double.NaN) {
                        switch (i) {
                            case 0:
                                a = point;
                                break;
                            case 1:
                                b = point;
                                break;
                            case 2:
                                c = point;
                                break;
                            case 3:
                                d = point;
                                break;
                            case 4:
                                tx = point;
                                break;
                            case 5:
                                ty = point;
                                break;
                        }
                        i++;
                        if (i > 5) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.a = 1;
                this.b = 0;
                this.c = 0;
                this.d = 1;
                this.tx = 0;
                this.ty = 0;
            }

        }
    }

    /**
     * @return the a
     */
    public double getA() {
        return a;
    }

    /**
     * @param a the a to set
     */
    public void setA(double a) {
        this.a = a;
    }

    /**
     * @return the b
     */
    public double getB() {
        return b;
    }

    /**
     * @param b the b to set
     */
    public void setB(double b) {
        this.b = b;
    }

    /**
     * @return the c
     */
    public double getC() {
        return c;
    }

    /**
     * @param c the c to set
     */
    public void setC(double c) {
        this.c = c;
    }

    /**
     * @return the d
     */
    public double getD() {
        return d;
    }

    /**
     * @param d the d to set
     */
    public void setD(double d) {
        this.d = d;
    }

    /**
     * @return the tx
     */
    public double getTx() {
        return tx;
    }

    /**
     * @param tx the tx to set
     */
    public void setTx(double tx) {
        this.tx = tx;
    }

    /**
     * @return the fy
     */
    public double getTy() {
        return ty;
    }

    /**
     * @param ty the fy to set
     */
    public void setTy(double ty) {
        this.ty = ty;
    }

    public float getScale() {
        return (float) a;
    }

    public float getScale2() {
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    public float getAngle() {
        return (float) (Math.atan2(b, a) * 180 / Math.PI);
    }

    public void setMatrix(Matrix matrix) {
        float[] m = new float[9];
        matrix.getValues(m);
        a = m[0];
        c = m[1];
        b = m[3];
        d = m[4];
    }


    public String getInfoStr() {
        return a + "," + b + "," + c + "," + d + "," + tx + "," + ty;
    }
}
