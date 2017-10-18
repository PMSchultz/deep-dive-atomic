package edu.cnm.deepdive.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicRaceCondition {
  
  private static final int UPPER_LIMIT = 20_000_000;
  private static final int NUM_THREADS = 10;
  private static final String START_MESSAGE = "Starting %d threads.%n;";
  private static final String THREAD_COMPLETE_MESSAGE = "Thread %d complete.%n";
  private static final String FINISH_MESSAGE = "Sum = %,d.%n";

  private AtomicLong sum;//provides synchronized operation to manipulate object
  private CountDownLatch signal;
  
  public static void main(String[] args) {
   AtomicRaceCondition race = new AtomicRaceCondition();
   race.start();
   race.finish();

  }
  
  private void start() {
    System.out.printf(START_MESSAGE, NUM_THREADS);
    signal = new CountDownLatch(NUM_THREADS);//Countdown latch doesn't let multiple threads manipulate at once
    sum = new AtomicLong(0);
    for (int i = 0; i < NUM_THREADS; i++) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < UPPER_LIMIT; i++) {
            sum.addAndGet(ThreadLocalRandom.current().nextInt(2));
          }
          System.out.printf(THREAD_COMPLETE_MESSAGE,  Thread.currentThread().getId());
          signal.countDown();
        }
      }).start();//when we call start on a thread object, java creates executes
    }
  }
  private void finish() {
    try {
      signal.await();
    } catch(InterruptedException ex) {
      //do nothing
    } finally {
      System.out.printf(FINISH_MESSAGE, sum.longValue());
    }
  }

}
