package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;


public class MultiThreadedSumMatrix implements SumMatrix{
    
    private final int nThreads;
    
    public MultiThreadedSumMatrix(final int n){
        this.nThreads = n;
    }

    private static class Worker extends Thread{
        
        private final int startpos;
        private final int nelem;
        private long res;
        private final double[][] matrix;

        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        public void run() {
            res = 0;
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (final double m : matrix[i]){
                    this.res += m;
                }
            }
        }

        public long getResult() {
            return this.res;
        }

    }

    public double sum(final double[][] matrix) {
        final int size = matrix.length % nThreads + matrix.length / nThreads;
        final List<Worker> workers = new ArrayList<>(nThreads);
        
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        
        for (final Worker w: workers) {
            w.start();
        }
        
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        
        return sum;
    }
}
