import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Sprint {
    private final List<Task> tasks;
    private final String startDate;
    private final String endDate;

    public Sprint(String startDate, String endDate) {
        this.tasks = new ArrayList<>();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        tasks.add(task);
    }

    public int getRemainingTasksCount() {
        int count = 0;
        for (Task task : tasks) {
            if (task.getStatus() != TaskStatus.DONE) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Integer> calculateBurnDownData() {
        ArrayList<Integer> data = new ArrayList<>();
        int days = calculateDurationDays();
        int remaining = getRemainingTasksCount();
        if (days <= 1) {
            data.add(remaining);
            return data;
        }

        // 簡單線性燃盡模型：從目前剩餘任務數逐日遞減到 0
        for (int i = 0; i < days; i++) {
            double ratio = (double) i / (days - 1);
            int value = remaining - (int) Math.round(remaining * ratio);
            if (value < 0) {
                value = 0;
            }
            data.add(value);
        }
        return data;
    }

    private int calculateDurationDays() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            return (int) Math.max(days, 1);
        } catch (Exception e) {
            return 1;
        }
    }
}

