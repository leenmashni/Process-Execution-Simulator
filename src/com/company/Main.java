package com.company;
import java.io.*;
import java.util.*;

public class Main {

    public static class Task implements Comparable<Task> {
        private final String taskID;
        private final int creationTime;
        private final int executionTime;
        private final int priority;
        private int remainingTime;

        public Task(int creationTime, int executionTime, int priority, String taskID) {
            this.creationTime = creationTime;
            this.executionTime = executionTime;
            this.priority = priority;
            this.taskID = taskID;
            this.remainingTime = executionTime;
        }

        public int getCreationTime() {
            return creationTime;
        }

        public int getExecutionTime() {
            return executionTime;
        }

        public int getPriority() {
            return priority;
        }

        public String getTaskID() {
            return taskID;
        }

        public int getRemainingTime() {
            return remainingTime;
        }

        public void execute() {
            if (remainingTime > 0) {
                --remainingTime;
            }
        }

        @Override
        public int compareTo(Task other) {
            if (this.priority == other.priority) {
                return Integer.compare(other.executionTime, this.executionTime);
            }
            return Integer.compare(other.priority, this.priority);
        }
    }

    public static class Processor {
        private final String processorID;
        private Task currentTask;

        public Processor(String processorID) {
            this.processorID = processorID;
            this.currentTask = null;
        }

        public boolean isAvailable() {
            return currentTask == null;
        }

        public void setTask(Task task) {
            this.currentTask = task;
        }

        public Task completeTask() {
            Task completedTask = this.currentTask;
            this.currentTask = null;
            return completedTask;
        }

        public Task execute() {
            if (currentTask != null) {
                currentTask.execute();
                if (currentTask.getRemainingTime() == 0) {
                    return completeTask();
                }
            }
            return null;
        }

        public String getProcessorID() {
            return processorID;
        }

        public Task getCurrentTask() {
            return currentTask;
        }
    }

    public static class clockCycle {
        private int cycle;

        public clockCycle() {
            this.cycle = 0;
        }

        public void nextCycle() throws InterruptedException {
            ++cycle;
            Thread.sleep(1000);
        }

        public int getCycle() {
            return cycle;
        }
    }

    public static class Scheduler {
        private final Vector<Processor> processors;
        private final PriorityQueue<Task> taskQueue;

        public Scheduler(Vector<Processor> processors) {
            this.processors = processors;
            this.taskQueue = new PriorityQueue<>();
        }

        public void addTask(Task task) {
            taskQueue.add(task);
        }

        public void scheduleTasks() {
            for (Processor processor : processors) {
                if (processor.isAvailable() && !taskQueue.isEmpty()) {
                    Task task = taskQueue.poll();
                    processor.setTask(task);
                }
            }
        }

        public void executeTasks() {
            for (Processor processor : processors) {
                Task completeTask = processor.execute();
                if (completeTask != null) {
                    System.out.println("  Task " + completeTask.getTaskID() + " is completed on " + processor.getProcessorID());
                }
                else if (processor.getCurrentTask() != null) {
                    System.out.println("  Task " + processor.getCurrentTask().getTaskID() + " is running on " + processor.getProcessorID());
                }
                else {
                    System.out.println("  " + processor.getProcessorID() + " is empty");
                }
            }
            System.out.println();
        }
    }

    public static class Simulator {
        private final int numProcessors;
        private final int numCycles;
        private final String tasksFilePath;
        private final Scheduler scheduler;
        private final Vector<Task> tasks;

        public Simulator(int numProcessors, int numCycles, String tasksFilePath) {
            this.numProcessors = numProcessors;
            this.numCycles = numCycles;
            this.tasksFilePath = tasksFilePath;
            this.scheduler = new Scheduler(createProcessors(numProcessors));
            this.tasks = new Vector<>();
            loadTasks();
        }

        public Vector<Processor> createProcessors(int numProcessors) {
            Vector<Processor> processors = new Vector<>();
            for (int i = 1; i <= numProcessors; i++) {
                processors.add(new Processor("P" + i));
            }
            return processors;
        }

        public void loadTasks() {
            try (BufferedReader br = new BufferedReader(new FileReader(tasksFilePath))) {
                int numTasks = Integer.parseInt(br.readLine().trim());
                for (int i = 1; i <= numTasks; i++) {
                    String line = br.readLine();
                    String[] parts = line.split(" ");
                    int creationTime = Integer.parseInt(parts[0]);
                    int executionTime = Integer.parseInt(parts[1]);
                    int priority = Integer.parseInt(parts[2]);
                    tasks.add(new Task(creationTime, executionTime, priority, "T" + i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() throws InterruptedException {
            clockCycle cycles = new clockCycle();
            while (cycles.getCycle() < numCycles) {
                System.out.println("Clock Cycle C" + (cycles.getCycle() + 1) + ":");

                for (Task task : tasks) {
                    if (task.getCreationTime() == cycles.getCycle() + 1) {
                        scheduler.addTask(task);
                        System.out.println("  Task " + task.getTaskID() + " is created with execution time " + task.getExecutionTime() + " and priority " + task.getPriority());
                    }
                }

                scheduler.scheduleTasks();

                scheduler.executeTasks();

                cycles.nextCycle();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String filePath = "/home/shahed/Downloads/Processor Execution Simulator/src/com/company/Input";
        Scanner scan = new Scanner(System.in);
        int numProcessors = scan.nextInt();
        int numCycles = scan.nextInt();
        Simulator simulator = new Simulator(numProcessors, numCycles, filePath);
        simulator.run();
    }
}



