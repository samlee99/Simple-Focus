package com.example.pc.simplepomodoro;

/**
 * Created by PC on 5/22/2017.
 */

public class Tasks {

    private String task;
    private int iterations;

    public Tasks() {

    }

    public Tasks(String task, int iterations) {
        this.task = task;
        this.iterations = iterations;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
