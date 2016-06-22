

import static java.lang.Math.*;
import Jama.Matrix;

/**
 *
 * CODE ADAPTED FROM:
 * http://the-lost-beauty.blogspot.com/2009/12/java-implementation-of-kalman-filter.html
 * AND:
 * http://the-lost-beauty.blogspot.com/2009/12/simulation-and-kalman-filter-for-3rd.html
 *
 */
public class KalmanFilter {

    protected Matrix X, X0;
    protected Matrix F, B, U, Q;
    protected Matrix H, R;
    protected Matrix P, P0;

    public static KalmanFilter buildKF(double x, double y, double dt, double processNoisePSD, double measurementNoiseVariance) {
        KalmanFilter KF = new KalmanFilter();

        //state vector
        KF.setX(new Matrix(new double[][]{{
                        0,
                        0,
                        0,
                        0}}).transpose());

        //error covariance matrix
        KF.setP(Matrix.identity(4, 4).times(0d));

        //transition matrix
        KF.setF(new Matrix(new double[][]{
                    {1, 0, dt, 0},
                    {0, 1, 0d, dt},
                    {0, 0, 1d, 0},
                    {0, 0, 0d, 1d},}));

        //input gain matrix
        KF.setB(new Matrix(new double[][]{{0, 0, 0, 0}}).transpose());

        //input vector
        KF.setU(new Matrix(new double[][]{{0}}));

        //process noise covariance matrix
        KF.setQ(new Matrix(new double[][]{
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}}).times(pow(processNoisePSD, 2)));

        //measurement matrix
        KF.setH(new Matrix(new double[][]{
                    {1, 0, 0, 0},
                    {0, 1, 0, 0}}));

        //measurement noise covariance matrix
        KF.setR(Matrix.identity(2, 2).times(measurementNoiseVariance));

        return KF;
    }

    public void predict() {
        X0 = F.times(X).plus(B.times(U));

        P0 = F.times(P).times(F.transpose()).plus(Q);
    }

    public void correct(Matrix Z) {
        Matrix S = H.times(P0).
                times(H.transpose()).
                plus(R);

        Matrix K = P0.times(H.transpose()).times(S.inverse());

        X = X0.plus(K.times(Z.minus(H.times(X0))));

        Matrix I = Matrix.identity(P0.getRowDimension(), P0.getColumnDimension());
        P = (I.minus(K.times(H))).times(P0);
    }

    public Matrix getB() {
        return B;
    }

    public void setB(Matrix B) {
        this.B = B;
    }

    public Matrix getF() {
        return F;
    }

    public void setF(Matrix F) {
        this.F = F;
    }

    public Matrix getH() {
        return H;
    }

    public void setH(Matrix H) {
        this.H = H;
    }

    public Matrix getP() {
        return P;
    }

    public void setP(Matrix P) {
        this.P = P;
    }

    public Matrix getP0() {
        return P0;
    }

    public void setP0(Matrix P0) {
        this.P0 = P0;
    }

    public Matrix getQ() {
        return Q;
    }

    public void setQ(Matrix Q) {
        this.Q = Q;
    }

    public Matrix getR() {
        return R;
    }

    public void setR(Matrix R) {
        this.R = R;
    }

    public Matrix getU() {
        return U;
    }

    public void setU(Matrix U) {
        this.U = U;
    }

    public Matrix getX() {
        return X;
    }

    public void setX(Matrix X) {
        this.X = X;
    }

    public Matrix getX0() {
        return X0;
    }

    public void setX0(Matrix X0) {
        this.X0 = X0;
    }
}
