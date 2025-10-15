package top.continew.admin.exam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeRangeDTO {
    private LocalDateTime start;
    private LocalDateTime end;
    private int rowIndex;

    public boolean overlaps(TimeRangeDTO other) {
        return !this.end.isBefore(other.start) && !this.start.isAfter(other.end);
    }
}